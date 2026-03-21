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

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class WarnCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        if (!sender.hasPermission("redmc.moderation.warn")) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.no-permission"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        String playerName = StringArgumentType.getString(context, "player");
        String reason = StringArgumentType.getString(context, "reason");

        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.player-not-found"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
        String staffName = sender.getName();

        plugin.getWarnManager().warn(target.getUniqueId(), staffUuid, staffName, reason);

        ApiUtils.sendCommandSenderMessageArgs(sender,
            locale.getMessage(sender, "warn.success"),
            "%prefix%", locale.getMessage(sender, "prefix"),
            "%player%", target.getName(),
            "%reason%", reason);

        ApiUtils.sendPlayerMessageArgs(target,
            locale.getMessage(target, "warn.received"),
            "%prefix%", locale.getMessage(target, "prefix"),
            "%reason%", reason);

        if (plugin.getConfigManager().getBoolean("config.yml", "moderation.broadcast-warns", true)) {
            ApiUtils.broadcastMessageArgs(
                locale.getMessage(sender, "warn.notify"),
                "%player%", target.getName(),
                "%staff%", staffName,
                "%reason%", reason);
        }

        return Command.SINGLE_SUCCESS;
    }
}
