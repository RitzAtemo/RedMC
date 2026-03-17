package red.aviora.redmc.teleport.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.SerializableLocation;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        boolean overrideOriginal = plugin.getConfigManager().getBoolean("config.yml", "spawn.override-original", true);
        if (!overrideOriginal) return;
        SerializableLocation spawn = plugin.getSpawnManager().getSpawn();
        if (spawn == null) return;
        Location loc = spawn.toBukkitLocation();
        if (loc == null) return;
        event.setRespawnLocation(loc);
    }
}
