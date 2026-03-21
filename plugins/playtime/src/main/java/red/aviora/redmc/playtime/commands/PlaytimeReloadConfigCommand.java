package red.aviora.redmc.playtime.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.playtime.PlaytimePlugin;

@SuppressWarnings("UnstableApiUsage")
public class PlaytimeReloadConfigCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        LocaleManager locale = PlaytimePlugin.getInstance().getLocaleManager();

        PlaytimePlugin.getInstance().getConfigManager().reload();

        ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "reload.config-success"),
                "%prefix%", locale.getMessage(sender, "prefix"));
        return Command.SINGLE_SUCCESS;
    }
}
