package red.aviora.redmc.moderation.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.managers.MuteManager;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.utils.DurationParser;

public class PlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ModerationPlugin plugin = ModerationPlugin.getInstance();
        MuteManager muteManager = plugin.getMuteManager();

        if (!muteManager.isMuted(player.getUniqueId())) return;

        event.setCancelled(true);
        LocaleManager locale = plugin.getLocaleManager();
        ModerationAction mute = muteManager.getActiveMute(player.getUniqueId());

        if (mute == null) return;

        if (mute.isPermanent()) {
            ApiUtils.sendCommandSenderMessageArgs(player,
                locale.getMessage(player, "mute.blocked-perm"),
                "%prefix%", locale.getMessage(player, "prefix"));
        } else {
            long expiresAt = mute.getTimestamp() + mute.getDuration() * 1000L;
            String remaining = DurationParser.formatRemaining(expiresAt);
            ApiUtils.sendCommandSenderMessageArgs(player,
                locale.getMessage(player, "mute.blocked"),
                "%prefix%", locale.getMessage(player, "prefix"),
                "%remaining%", remaining);
        }
    }
}
