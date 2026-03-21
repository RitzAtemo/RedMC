package red.aviora.redmc.playtime.managers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.playtime.PlaytimePlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AfkManager {

    private final ConfigManager configManager;
    private final LocaleManager localeManager;
    private final PlaytimeManager playtimeManager;

    // Last activity timestamp (epoch seconds) per player
    private final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();
    // Last known location for movement threshold check
    private final Map<UUID, Location> lastLocation = new ConcurrentHashMap<>();
    // AFK status
    private final Map<UUID, Boolean> afkStatus = new ConcurrentHashMap<>();
    // When the player went AFK (epoch seconds)
    private final Map<UUID, Long> afkSince = new ConcurrentHashMap<>();

    public AfkManager(ConfigManager configManager, LocaleManager localeManager, PlaytimeManager playtimeManager) {
        this.configManager = configManager;
        this.localeManager = localeManager;
        this.playtimeManager = playtimeManager;
    }

    public boolean isEnabled() {
        return configManager.getBoolean("config.yml", "afk.enabled", true);
    }

    public void onJoin(Player player) {
        resetActivity(player);
        afkStatus.put(player.getUniqueId(), false);
    }

    public void onQuit(Player player) {
        UUID uuid = player.getUniqueId();
        Boolean wasAfk = afkStatus.get(uuid);
        if (Boolean.TRUE.equals(wasAfk)) {
            Long since = afkSince.get(uuid);
            if (since != null) {
                long afkSeconds = System.currentTimeMillis() / 1000L - since;
                playtimeManager.addAfkSeconds(uuid, afkSeconds);
            }
        }
        lastActivity.remove(uuid);
        lastLocation.remove(uuid);
        afkStatus.remove(uuid);
        afkSince.remove(uuid);
    }

    public void recordActivity(Player player) {
        if (!isEnabled()) return;
        if (player.hasPermission("redmc.playtime.afk.bypass")) return;

        UUID uuid = player.getUniqueId();
        lastActivity.put(uuid, System.currentTimeMillis() / 1000L);

        if (Boolean.TRUE.equals(afkStatus.get(uuid))) {
            setAfk(player, false);
        }
    }

    public void recordMove(Player player, Location to) {
        if (!isEnabled()) return;
        if (player.hasPermission("redmc.playtime.afk.bypass")) return;

        UUID uuid = player.getUniqueId();
        Location last = lastLocation.get(uuid);

        if (last != null) {
            double threshold = configManager.getDouble("config.yml", "afk.movement-threshold", 2.0);
            double distSq = last.distanceSquared(to);
            if (distSq < threshold * threshold) return;
        }

        lastLocation.put(uuid, to.clone());
        recordActivity(player);
    }

    public void resetActivity(Player player) {
        UUID uuid = player.getUniqueId();
        lastActivity.put(uuid, System.currentTimeMillis() / 1000L);
        lastLocation.put(uuid, player.getLocation().clone());
    }

    public boolean isAfk(UUID uuid) {
        return Boolean.TRUE.equals(afkStatus.get(uuid));
    }

    public void startAfkCheckTask() {
        PlaytimePlugin.getInstance().getServer().getGlobalRegionScheduler().runAtFixedRate(
                PlaytimePlugin.getInstance(),
                task -> checkAfk(),
                20L,
                20L
        );
    }

    private void checkAfk() {
        if (!isEnabled()) return;

        long timeout = configManager.getInt("config.yml", "afk.timeout", 300);
        boolean kickEnabled = configManager.getBoolean("config.yml", "afk.kick.enabled", false);
        long kickDelay = configManager.getInt("config.yml", "afk.kick.delay", 600);
        long now = System.currentTimeMillis() / 1000L;

        for (Player player : PlaytimePlugin.getInstance().getServer().getOnlinePlayers()) {
            if (player.hasPermission("redmc.playtime.afk.bypass")) continue;

            UUID uuid = player.getUniqueId();
            long last = lastActivity.getOrDefault(uuid, now);
            long idle = now - last;

            boolean currentlyAfk = Boolean.TRUE.equals(afkStatus.get(uuid));

            if (!currentlyAfk && idle >= timeout) {
                setAfk(player, true);
            } else if (currentlyAfk && kickEnabled) {
                Long since = afkSince.get(uuid);
                if (since != null && (now - since) >= kickDelay) {
                    String reason = configManager.getString("config.yml", "afk.kick.reason",
                            "<red>You were kicked for being AFK.");
                    player.getScheduler().run(PlaytimePlugin.getInstance(), t ->
                            player.kick(MiniMessage.miniMessage().deserialize(reason)), null);
                }
            }
        }
    }

    private void setAfk(Player player, boolean afk) {
        UUID uuid = player.getUniqueId();
        afkStatus.put(uuid, afk);

        if (afk) {
            afkSince.put(uuid, System.currentTimeMillis() / 1000L);

            if (configManager.getBoolean("config.yml", "afk.broadcast.on-afk", true)) {
                ApiUtils.broadcastMessageArgs(
                        localeManager.getMessage(player, "afk.now-afk"),
                        "%player%", player.getName());
            }
            ApiUtils.sendPlayerMessageArgs(player,
                    localeManager.getMessage(player, "afk.self-afk"),
                    "%prefix%", localeManager.getMessage(player, "prefix"));
        } else {
            Long since = afkSince.remove(uuid);
            if (since != null) {
                long afkSeconds = System.currentTimeMillis() / 1000L - since;
                playtimeManager.addAfkSeconds(uuid, afkSeconds);
            }

            if (configManager.getBoolean("config.yml", "afk.broadcast.on-return", true)) {
                ApiUtils.broadcastMessageArgs(
                        localeManager.getMessage(player, "afk.returned"),
                        "%player%", player.getName());
            }
            ApiUtils.sendPlayerMessageArgs(player,
                    localeManager.getMessage(player, "afk.self-returned"),
                    "%prefix%", localeManager.getMessage(player, "prefix"));
        }
    }
}
