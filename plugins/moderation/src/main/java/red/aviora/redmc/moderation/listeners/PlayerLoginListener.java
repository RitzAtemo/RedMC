package red.aviora.redmc.moderation.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.managers.BanManager;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.utils.DurationParser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerLoginListener implements Listener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        ModerationPlugin plugin = ModerationPlugin.getInstance();
        BanManager banManager = plugin.getBanManager();

        if (!banManager.isBanned(event.getPlayer().getUniqueId())) return;

        ModerationAction ban = banManager.getActiveBan(event.getPlayer().getUniqueId());
        if (ban == null) return;

        LocaleManager locale = plugin.getLocaleManager();
        // Use console sender as fallback for locale since player hasn't joined yet
        String screenRaw;
        if (ban.isPermanent()) {
            screenRaw = locale.getMessage(event.getPlayer(), "ban.screen-perm")
                .replace("%reason%", ban.getReason());
        } else {
            long expiresAt = ban.getTimestamp() + ban.getDuration() * 1000L;
            String expiry = DATE_FORMAT.format(new Date(expiresAt));
            screenRaw = locale.getMessage(event.getPlayer(), "ban.screen")
                .replace("%reason%", ban.getReason())
                .replace("%expiry%", expiry);
        }

        Component kickMessage = ApiUtils.getMM().deserialize(screenRaw);
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
    }
}
