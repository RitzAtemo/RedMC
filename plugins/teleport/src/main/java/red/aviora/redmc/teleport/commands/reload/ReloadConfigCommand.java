package red.aviora.redmc.teleport.commands.reload;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;

public class ReloadConfigCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        plugin.getConfigManager().reload();

        ApiUtils.sendCommandSenderMessageArgs(sender,
            locale.getMessage(sender, "reload.config-success"),
            "%prefix%", locale.getMessage(sender, "prefix"));

        return Command.SINGLE_SUCCESS;
    }
}
