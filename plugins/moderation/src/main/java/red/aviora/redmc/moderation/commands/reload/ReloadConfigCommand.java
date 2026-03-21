package red.aviora.redmc.moderation.commands.reload;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;

@SuppressWarnings("UnstableApiUsage")
public class ReloadConfigCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        plugin.getConfigManager().reload();

        ApiUtils.sendCommandSenderMessageArgs(sender,
            locale.getMessage(sender, "reload.config-success"),
            "%prefix%", locale.getMessage(sender, "prefix"));

        return Command.SINGLE_SUCCESS;
    }
}
