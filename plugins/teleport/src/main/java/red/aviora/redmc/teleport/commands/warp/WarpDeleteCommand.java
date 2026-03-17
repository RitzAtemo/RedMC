package red.aviora.redmc.teleport.commands.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;

public class WarpDeleteCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();
        CommandSender sender = context.getSource().getSender();

        String name = StringArgumentType.getString(context, "name");
        if (!plugin.getWarpManager().exists(name)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(sender, "warp.delete-not-found"),
                        "%prefix%", locale.getMessage(sender, "prefix"))
                )
            ).create();
        }

        plugin.getWarpManager().deleteWarp(name);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            locale.getMessage(sender, "warp.delete-success"),
            "%prefix%", locale.getMessage(sender, "prefix"));

        return Command.SINGLE_SUCCESS;
    }
}
