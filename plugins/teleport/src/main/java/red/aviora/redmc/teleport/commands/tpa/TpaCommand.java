package red.aviora.redmc.teleport.commands.tpa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.TeleportRequest;

public class TpaCommand implements Command<CommandSourceStack> {

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

        String targetName = StringArgumentType.getString(context, "player");
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "error.player-not-found"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        if (target.equals(player)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "tpa.cannot-self"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        if (plugin.getTpaManager().isOnCooldown(player)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "tpa.cooldown"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        TeleportRequest request = plugin.getTpaManager().sendRequest(player, target, false);

        ApiUtils.sendPlayerMessageArgs(player,
            locale.getMessage(player, "tpa.request-sent"),
            "%prefix%", locale.getMessage(player, "prefix"),
            "%player%", target.getName());

        ApiUtils.sendPlayerMessageArgs(target,
            locale.getMessage(target, "tpa.request-received"),
            "%prefix%", locale.getMessage(target, "prefix"),
            "%player%", player.getName());

        return Command.SINGLE_SUCCESS;
    }
}
