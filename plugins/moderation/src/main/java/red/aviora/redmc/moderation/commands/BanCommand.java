package red.aviora.redmc.moderation.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.moderation.utils.DurationParser;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class BanCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        if (!sender.hasPermission("redmc.moderation.ban")) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.no-permission"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        String playerName = StringArgumentType.getString(context, "player");
        String durationStr = StringArgumentType.getString(context, "duration");
        String reason = StringArgumentType.getString(context, "reason");

        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.player-not-found"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        long duration = DurationParser.parse(durationStr);
        if (!DurationParser.isValid(duration)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.invalid-duration"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
        String staffName = sender.getName();

        plugin.getBanManager().ban(target.getUniqueId(), target.getName(), staffUuid, staffName, reason, duration);

        boolean permanent = duration == -1;
        String successKey = permanent ? "ban.success-perm" : "ban.success";
        String durationFormatted = DurationParser.format(duration);

        String successMsg = VaultPlugin.resolvePlayer(
            ApiUtils.formatTextString(locale.getMessage(sender, successKey),
                "%prefix%", locale.getMessage(sender, "prefix"),
                "%duration%", durationFormatted,
                "%reason%", reason),
            target);
        sender.sendMessage(ApiUtils.formatText(successMsg));

        // Kick the player
        final String finalReason = reason;
        final long finalDuration = duration;
        plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> {
            if (target.isOnline()) {
                String kickMsgRaw;
                if (permanent) {
                    kickMsgRaw = locale.getMessage(target, "ban.screen-perm")
                        .replace("%reason%", finalReason);
                } else {
                    long expiresAt = System.currentTimeMillis() + finalDuration * 1000L;
                    String expiry = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(expiresAt));
                    kickMsgRaw = locale.getMessage(target, "ban.screen")
                        .replace("%reason%", finalReason)
                        .replace("%expiry%", expiry);
                }
                target.kick(ApiUtils.getMM().deserialize(kickMsgRaw));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
