package red.aviora.redmc.teleport.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        plugin.getHomeManager().savePlayer(event.getPlayer().getUniqueId());
        plugin.getBackManager().clearPlayer(event.getPlayer().getUniqueId());
        plugin.getRtpManager().clearPlayer(event.getPlayer().getUniqueId());
        plugin.getTpaManager().clearPlayer(event.getPlayer().getUniqueId());
    }
}
