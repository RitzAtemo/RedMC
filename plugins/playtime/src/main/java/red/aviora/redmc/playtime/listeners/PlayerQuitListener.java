package red.aviora.redmc.playtime.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.playtime.PlaytimePlugin;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        PlaytimePlugin plugin = PlaytimePlugin.getInstance();
        plugin.getAfkManager().onQuit(event.getPlayer());
        plugin.getPlaytimeManager().onQuit(event.getPlayer());
    }
}
