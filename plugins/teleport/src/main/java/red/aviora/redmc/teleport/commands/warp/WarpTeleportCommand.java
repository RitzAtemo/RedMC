package red.aviora.redmc.teleport.commands.warp;

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
import red.aviora.redmc.teleport.models.Warp;

public class WarpTeleportCommand implements Command<CommandSourceStack> {

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
        Warp warp = plugin.getWarpManager().getWarp(name);
        if (warp == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "warp.teleport-not-found"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        Location loc = warp.getLocation().toBukkitLocation();
        if (loc == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "warp.teleport-not-found"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        plugin.getBackManager().push(player, player.getLocation());
        player.teleportAsync(loc).thenAccept(success -> {
            if (success) {
                ApiUtils.sendPlayerMessageArgs(player,
                    locale.getMessage(player, "warp.teleport-success"),
                    "%prefix%", locale.getMessage(player, "prefix"));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
