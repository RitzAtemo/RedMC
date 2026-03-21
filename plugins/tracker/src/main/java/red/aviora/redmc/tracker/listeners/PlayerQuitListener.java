package red.aviora.redmc.tracker.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.tracker.TrackerPlugin;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        TrackerPlugin.getInstance().getTrackerManager().savePosition(event.getPlayer());
        TrackerPlugin.getInstance().getTrackerManager().stopTracking(event.getPlayer());
    }
}
