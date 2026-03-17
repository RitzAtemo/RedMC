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
import red.aviora.redmc.teleport.utils.LimitUtils;

public class HomeSetCommand implements Command<CommandSourceStack> {

    private final boolean hasNameArg;

    public HomeSetCommand(boolean hasNameArg) {
        this.hasNameArg = hasNameArg;
    }

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

        String name = hasNameArg ? StringArgumentType.getString(context, "name") : "home";
        int limit = LimitUtils.getHomeLimit(player);
        boolean set = plugin.getHomeManager().setHome(player.getUniqueId(), name, player.getLocation(), limit);

        if (!set) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "home.set-limit"),
                        "%prefix%", locale.getMessage(player, "prefix"),
                        "%limit%", String.valueOf(limit))
                )
            ).create();
        }

        ApiUtils.sendPlayerMessageArgs(player,
            locale.getMessage(player, "home.set-success"),
            "%prefix%", locale.getMessage(player, "prefix"),
            "%home%", name);

        return Command.SINGLE_SUCCESS;
    }
}
