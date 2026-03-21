package red.aviora.redmc.tracker.managers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.tracker.TrackerPlugin;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.tracker.models.PlayerData;
import red.aviora.redmc.tracker.utils.TrackerDataStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TrackerManager {

    private final TrackerDataStorage storage;
    private final ConfigManager configManager;
    private final LocaleManager localeManager;

    private final Map<UUID, UUID> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledTask> tasks = new ConcurrentHashMap<>();

    public TrackerManager(TrackerDataStorage storage, ConfigManager configManager, LocaleManager localeManager) {
        this.storage = storage;
        this.configManager = configManager;
        this.localeManager = localeManager;
    }

    public boolean isTracking(Player admin) {
        return sessions.containsKey(admin.getUniqueId());
    }

    public UUID getTrackedUuid(Player admin) {
        return sessions.get(admin.getUniqueId());
    }

    public void startTracking(Player admin, Player target) {
        stopTracking(admin);

        UUID adminUuid = admin.getUniqueId();
        UUID targetUuid = target.getUniqueId();
        sessions.put(adminUuid, targetUuid);

        long interval = configManager.getInt("config.yml", "update-interval", 20);

        ScheduledTask task = admin.getScheduler().runAtFixedRate(
                TrackerPlugin.getInstance(),
                scheduledTask -> {
                    Player t = admin.getServer().getPlayer(targetUuid);
                    if (t == null || !t.isOnline()) {
                        stopTracking(admin);
                        return;
                    }
                    String format = configManager.getString("config.yml", "tracker-format",
                            "<gray>X: <white>%x% <gray>Y: <white>%y% <gray>Z: <white>%z%");
                    String text = VaultPlugin.resolvePlayer(format
                            .replace("%x%", String.valueOf((int) t.getX()))
                            .replace("%y%", String.valueOf((int) t.getY()))
                            .replace("%z%", String.valueOf((int) t.getZ()))
                            .replace("%world%", t.getWorld().getName()), t);
                    admin.sendActionBar(ApiUtils.getMM().deserialize(text));
                },
                null,
                1L,
                interval
        );

        tasks.put(adminUuid, task);
    }

    public void stopTracking(Player admin) {
        UUID adminUuid = admin.getUniqueId();
        sessions.remove(adminUuid);
        ScheduledTask task = tasks.remove(adminUuid);
        if (task != null) task.cancel();
    }

    public void reloadData() {
        stopAllSessions();
    }

    public void stopAllSessions() {
        tasks.values().forEach(ScheduledTask::cancel);
        tasks.clear();
        sessions.clear();
    }

    public void restorePosition(Player player) {
        if (!configManager.getBoolean("config.yml", "restore-on-join", true)) return;
        if (player.hasPermission("redmc.tracker.bypass")) return;

        PlayerData data = storage.load(player.getUniqueId());
        if (data == null) return;

        org.bukkit.World world = player.getServer().getWorld(data.getWorld());
        if (world == null) return;

        Location location = data.toLocation();

        TrackerPlugin.getInstance().getServer().getGlobalRegionScheduler().runDelayed(
                TrackerPlugin.getInstance(),
                task -> {
                    if (!player.isOnline()) return;
                    player.teleportAsync(location).thenRun(() ->
                            player.getScheduler().run(TrackerPlugin.getInstance(), t ->
                                    ApiUtils.sendPlayerMessageArgs(player,
                                            localeManager.getMessage(player, "position-restored"),
                                            "%prefix%", localeManager.getMessage(player, "prefix")),
                                    null));
                },
                1L
        );
    }

    public void savePosition(Player player) {
        if (!configManager.getBoolean("config.yml", "save-on-quit", true)) return;
        storage.save(PlayerData.fromLocation(player.getUniqueId(), player.getLocation()));
    }
}
