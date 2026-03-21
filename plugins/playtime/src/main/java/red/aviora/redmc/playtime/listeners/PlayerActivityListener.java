package red.aviora.redmc.playtime.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import red.aviora.redmc.playtime.PlaytimePlugin;
import red.aviora.redmc.playtime.managers.AfkManager;

public class PlayerActivityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        AfkManager afk = PlaytimePlugin.getInstance().getAfkManager();
        afk.recordMove(event.getPlayer(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        PlaytimePlugin.getInstance().getAfkManager().recordActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        PlaytimePlugin.getInstance().getAfkManager().recordActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        PlaytimePlugin.getInstance().getAfkManager().recordActivity(event.getPlayer());
    }
}
