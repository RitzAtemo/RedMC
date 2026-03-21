package red.aviora.redmc.teleport.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.SerializableLocation;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        plugin.getHomeManager().loadPlayer(event.getPlayer().getUniqueId());

        if (!event.getPlayer().hasPlayedBefore()) {
            boolean newbieEnabled = plugin.getConfigManager().getBoolean("config.yml", "spawn.newbie-spawn-enabled", true);
            if (newbieEnabled) {
                SerializableLocation newbieSpawn = plugin.getSpawnManager().getNewbieSpawn();
                if (newbieSpawn != null) {
                    Location loc = newbieSpawn.toBukkitLocation();
                    if (loc != null) {
                        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
                            if (!event.getPlayer().isOnline()) return;
                            event.getPlayer().teleportAsync(loc);
                        }, 1L);
                    }
                }
            }
        }
    }
}
