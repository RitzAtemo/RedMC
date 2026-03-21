package red.aviora.redmc.playtime.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.aviora.redmc.playtime.PlaytimePlugin;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        PlaytimePlugin plugin = PlaytimePlugin.getInstance();
        plugin.getPlaytimeManager().onJoin(event.getPlayer());
        plugin.getAfkManager().onJoin(event.getPlayer());
    }
}
