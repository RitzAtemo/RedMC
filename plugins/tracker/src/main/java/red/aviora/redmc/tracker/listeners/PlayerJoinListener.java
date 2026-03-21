package red.aviora.redmc.tracker.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.aviora.redmc.tracker.TrackerPlugin;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        TrackerPlugin.getInstance().getTrackerManager().restorePosition(event.getPlayer());
    }
}
