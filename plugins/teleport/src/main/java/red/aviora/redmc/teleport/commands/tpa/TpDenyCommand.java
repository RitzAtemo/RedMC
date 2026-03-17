package red.aviora.redmc.teleport.commands.tpa;

import com.mojang.brigadier.Command;
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

public class TpDenyCommand implements Command<CommandSourceStack> {

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

        TeleportRequest request = plugin.getTpaManager().deny(player);
        if (request == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "tpa.no-request"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        ApiUtils.sendPlayerMessageArgs(player,
            locale.getMessage(player, "tpa.deny-success"),
            "%prefix%", locale.getMessage(player, "prefix"));

        Player requester = Bukkit.getPlayer(request.getRequesterUuid());
        if (requester != null && requester.isOnline()) {
            ApiUtils.sendPlayerMessageArgs(requester,
                locale.getMessage(requester, "tpa.request-denied"),
                "%prefix%", locale.getMessage(requester, "prefix"),
                "%player%", player.getName());
        }

        return Command.SINGLE_SUCCESS;
    }
}
