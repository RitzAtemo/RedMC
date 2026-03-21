package red.aviora.redmc.tracker.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.tracker.TrackerPlugin;

@SuppressWarnings("UnstableApiUsage")
public class TrackerReloadAllCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        TrackerPlugin plugin = JavaPlugin.getPlugin(TrackerPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        plugin.getConfigManager().reload();
        plugin.getTrackerManager().reloadData();

        ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "reload-all-success"),
                "%prefix%", locale.getMessage(sender, "prefix"));

        return Command.SINGLE_SUCCESS;
    }
}
