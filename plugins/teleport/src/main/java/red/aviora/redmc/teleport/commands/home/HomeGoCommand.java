package red.aviora.redmc.teleport.commands.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.Home;

public class HomeGoCommand implements Command<CommandSourceStack> {

    private final boolean hasNameArg;

    public HomeGoCommand(boolean hasNameArg) {
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
        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), name);
        if (home == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "home.teleport-not-found"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        Location loc = home.getLocation().toBukkitLocation();
        if (loc == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "home.teleport-not-found"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        plugin.getBackManager().push(player, player.getLocation());
        player.teleportAsync(loc).thenAccept(success -> {
            if (success) {
                ApiUtils.sendPlayerMessageArgs(player,
                    locale.getMessage(player, "home.teleport-success"),
                    "%prefix%", locale.getMessage(player, "prefix"));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
