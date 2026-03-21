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
import red.aviora.redmc.moderation.gui.HistoryGui;
import red.aviora.redmc.moderation.models.ModerationAction;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HistoryCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        if (!sender.hasPermission("redmc.moderation.history")) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.no-permission"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        if (!(sender instanceof Player viewer)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "error.only-players"),
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

        List<ModerationAction> history = plugin.getWarnManager().getActionsMap()
            .getOrDefault(target.getUniqueId(), java.util.Collections.emptyList());
        if (history.isEmpty()) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "history.no-history"),
                "%prefix%", locale.getMessage(sender, "prefix"),
                "%player%", target.getName());
            return Command.SINGLE_SUCCESS;
        }

        plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
            HistoryGui.open(viewer, target.getUniqueId(), target.getName(), 0)
        );

        return Command.SINGLE_SUCCESS;
    }
}
