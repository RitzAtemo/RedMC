package red.aviora.redmc.moderation.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.aviora.redmc.moderation.ModerationPlugin;

public class ModerationPlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        ModerationPlugin plugin = ModerationPlugin.getInstance();
        var uuid = event.getPlayer().getUniqueId();
        if (plugin.getMuteManager().isMuted(uuid)) {
            plugin.getMuteManager().applyMeta(uuid);
        }
    }
}
