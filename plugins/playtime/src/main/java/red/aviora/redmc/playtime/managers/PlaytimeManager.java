package red.aviora.redmc.playtime.managers;

import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.playtime.PlaytimePlugin;
import red.aviora.redmc.playtime.models.PlayerPlaytimeData;
import red.aviora.redmc.playtime.utils.PlaytimeDataStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeManager {

    private final PlaytimeDataStorage storage;
    private final ConfigManager configManager;

    // session start time in epoch seconds for each online player
    private final Map<UUID, Long> sessionStart = new ConcurrentHashMap<>();
    // AFK time accumulated during current session (seconds)
    private final Map<UUID, Long> sessionAfkSeconds = new ConcurrentHashMap<>();
    // loaded data cache
    private final Map<UUID, PlayerPlaytimeData> cache = new ConcurrentHashMap<>();

    public PlaytimeManager(PlaytimeDataStorage storage, ConfigManager configManager) {
        this.storage = storage;
        this.configManager = configManager;
    }

    public void onJoin(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerPlaytimeData data = storage.load(uuid);

        long now = System.currentTimeMillis() / 1000L;
        if (data.getFirstJoin() == 0) {
            data.setFirstJoin(now);
        }

        cache.put(uuid, data);
        sessionStart.put(uuid, now);
        sessionAfkSeconds.put(uuid, 0L);
    }

    public void onQuit(Player player) {
        UUID uuid = player.getUniqueId();
        flushSession(uuid);
        cache.remove(uuid);
        sessionStart.remove(uuid);
        sessionAfkSeconds.remove(uuid);
    }

    public void addAfkSeconds(UUID uuid, long seconds) {
        sessionAfkSeconds.merge(uuid, seconds, Long::sum);
    }

    public long getSessionPlaytimeSeconds(UUID uuid) {
        Long start = sessionStart.get(uuid);
        if (start == null) return 0;
        long elapsed = System.currentTimeMillis() / 1000L - start;
        long afk = sessionAfkSeconds.getOrDefault(uuid, 0L);
        return Math.max(0, elapsed - afk);
    }

    public long getTotalPlaytimeSeconds(UUID uuid) {
        PlayerPlaytimeData data = cache.get(uuid);
        long stored = (data != null) ? data.getPlaytimeSeconds() : storage.load(uuid).getPlaytimeSeconds();
        return stored + getSessionPlaytimeSeconds(uuid);
    }

    public void flushAllSessions() {
        for (UUID uuid : sessionStart.keySet()) {
            flushSession(uuid);
        }
    }

    private void flushSession(UUID uuid) {
        PlayerPlaytimeData data = cache.get(uuid);
        if (data == null) return;

        long sessionSeconds = getSessionPlaytimeSeconds(uuid);
        data.addSeconds(sessionSeconds);
        data.setLastSeen(System.currentTimeMillis() / 1000L);

        // reset session start so we don't double-count if called again
        sessionStart.put(uuid, System.currentTimeMillis() / 1000L);
        sessionAfkSeconds.put(uuid, 0L);

        storage.save(data);
    }

    public void reloadData() {
        // flush current sessions, reload from disk
        flushAllSessions();
        for (UUID uuid : cache.keySet()) {
            cache.put(uuid, storage.load(uuid));
            sessionStart.put(uuid, System.currentTimeMillis() / 1000L);
            sessionAfkSeconds.put(uuid, 0L);
        }
    }

    public void startAutoSave() {
        long interval = configManager.getInt("config.yml", "playtime.save-interval", 6000);
        PlaytimePlugin.getInstance().getServer().getGlobalRegionScheduler().runAtFixedRate(
                PlaytimePlugin.getInstance(),
                task -> flushAllSessions(),
                interval,
                interval
        );
    }
}
