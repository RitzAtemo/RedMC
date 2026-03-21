package red.aviora.redmc.moderation.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.managers.MuteManager;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.utils.DurationParser;

import java.util.List;

public class MutedCommandListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("redmc:muted")) return;

        String input = event.getMessage(); // e.g. "/msg Player hello"
        String command = input.substring(1).split(" ")[0].toLowerCase();

        List<String> blocked = ModerationPlugin.getInstance()
                .getConfigManager()
                .getConfig("config.yml")
                .getStringList("moderation.mute.blocked-commands");

        if (!blocked.contains(command)) return;

        event.setCancelled(true);

        ModerationPlugin plugin = ModerationPlugin.getInstance();
        MuteManager muteManager = plugin.getMuteManager();
        LocaleManager locale = plugin.getLocaleManager();
        ModerationAction mute = muteManager.getActiveMute(player.getUniqueId());

        if (mute == null) return;

        if (mute.isPermanent()) {
            ApiUtils.sendCommandSenderMessageArgs(player,
                    locale.getMessage(player, "mute.blocked-perm"),
                    "%prefix%", locale.getMessage(player, "prefix"));
        } else {
            long expiresAt = mute.getTimestamp() + mute.getDuration() * 1000L;
            ApiUtils.sendCommandSenderMessageArgs(player,
                    locale.getMessage(player, "mute.blocked"),
                    "%prefix%", locale.getMessage(player, "prefix"),
                    "%remaining%", DurationParser.formatRemaining(expiresAt));
        }
    }
}
