package red.aviora.redmc.moderation.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.vault.VaultPlugin;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class UnmuteCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        if (!sender.hasPermission("redmc.moderation.unmute")) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.no-permission"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        String playerName = StringArgumentType.getString(context, "player");

        UUID targetUuid = plugin.getMuteManager().findMutedByName(playerName);
        if (targetUuid == null) {
            @SuppressWarnings("deprecation")
            OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
            if (offline.hasPlayedBefore()) targetUuid = offline.getUniqueId();
        }

        if (targetUuid == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.player-not-found"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        boolean unmuted = plugin.getMuteManager().unmute(targetUuid);

        String key = unmuted ? "mute.unmute-success" : "mute.not-muted";
        String msg = VaultPlugin.resolvePlayerByUuid(
            ApiUtils.formatTextString(locale.getMessage(sender, key),
                "%prefix%", locale.getMessage(sender, "prefix")),
            targetUuid);
        sender.sendMessage(ApiUtils.formatText(msg));

        return Command.SINGLE_SUCCESS;
    }

}
