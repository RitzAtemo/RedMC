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
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.player-not-found"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        boolean unmuted = plugin.getMuteManager().unmute(target.getUniqueId());

        if (unmuted) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "mute.unmute-success"),
                "%prefix%", locale.getMessage(sender, "prefix"),
                "%player%", target.getName());
        } else {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "mute.not-muted"),
                "%prefix%", locale.getMessage(sender, "prefix"),
                "%player%", target.getName());
        }

        return Command.SINGLE_SUCCESS;
    }
}
