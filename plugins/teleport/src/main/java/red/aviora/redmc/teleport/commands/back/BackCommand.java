package red.aviora.redmc.teleport.commands.back;

import com.mojang.brigadier.Command;
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
import red.aviora.redmc.teleport.utils.LimitUtils;

public class BackCommand implements Command<CommandSourceStack> {

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

        int limit = LimitUtils.getBackLimit(player);
        boolean bypassCooldown = LimitUtils.hasBackCooldownBypass(player);
        if (!plugin.getBackManager().canUse(player, limit, bypassCooldown)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "back.limit-reached"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        Location previous = plugin.getBackManager().pop(player);
        if (previous == null) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "back.no-location"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        plugin.getBackManager().recordUse(player, bypassCooldown);

        player.teleportAsync(previous).thenAccept(success -> {
            if (success) {
                ApiUtils.sendPlayerMessageArgs(player,
                    locale.getMessage(player, "back.success"),
                    "%prefix%", locale.getMessage(player, "prefix"));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
