package red.aviora.redmc.teleport.commands.home;

import com.mojang.brigadier.Command;
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
import red.aviora.redmc.teleport.models.Home;

import java.util.List;

public class HomeListCommand implements Command<CommandSourceStack> {

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

        List<Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        if (homes.isEmpty()) {
            ApiUtils.sendPlayerMessageArgs(player,
                locale.getMessage(player, "home.list-empty"),
                "%prefix%", locale.getMessage(player, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        ApiUtils.sendPlayerMessageArgs(player,
            locale.getMessage(player, "home.list-header"),
            "%prefix%", locale.getMessage(player, "prefix"));

        for (Home home : homes) {
            ApiUtils.sendPlayerMessageArgs(player,
                locale.getMessage(player, "home.list-entry"),
                "%home%", home.getName());
        }

        return Command.SINGLE_SUCCESS;
    }
}
