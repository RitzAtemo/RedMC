package red.aviora.redmc.teleport.commands.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;

public class HomeDeleteCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        if (!(context.getSource().getSender() instanceof Player player)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(context.getSource().getSender(), "error.only-players"),
                        "%prefix%", locale.getMessage(context.getSource().getSender(), "prefix"))
                )
            ).create();
        }

        String name = StringArgumentType.getString(context, "name");
        boolean deleted = plugin.getHomeManager().deleteHome(player.getUniqueId(), name);
        if (!deleted) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "home.delete-not-found"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        ApiUtils.sendPlayerMessageArgs(player,
            locale.getMessage(player, "home.delete-success"),
            "%prefix%", locale.getMessage(player, "prefix"));

        return Command.SINGLE_SUCCESS;
    }
}
