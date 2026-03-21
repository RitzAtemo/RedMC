package red.aviora.redmc.tracker.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.tracker.TrackerPlugin;
import red.aviora.redmc.tracker.managers.TrackerManager;
import red.aviora.redmc.vault.VaultPlugin;

@SuppressWarnings("UnstableApiUsage")
public class TrackerStartCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        LocaleManager locale = TrackerPlugin.getInstance().getLocaleManager();
        TrackerManager manager = TrackerPlugin.getInstance().getTrackerManager();

        if (!(sender instanceof Player admin)) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(sender, "error.only-players"),
                            "%prefix%", locale.getMessage(sender, "prefix"))
            )).create();
        }

        String targetName = StringArgumentType.getString(context, "player");
        Player target = sender.getServer().getPlayer(targetName);

        if (target == null) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(sender, "error.player-not-found"),
                            "%prefix%", locale.getMessage(sender, "prefix"))
            )).create();
        }

        if (manager.isTracking(admin) && target.getUniqueId().equals(manager.getTrackedUuid(admin))) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(VaultPlugin.resolvePlayer(
                            ApiUtils.formatTextString(locale.getMessage(sender, "already-tracking"),
                                "%prefix%", locale.getMessage(sender, "prefix")),
                            target))
            )).create();
        }

        manager.startTracking(admin, target);

        String msg = VaultPlugin.resolvePlayer(
                ApiUtils.formatTextString(locale.getMessage(admin, "tracking-started"),
                    "%prefix%", locale.getMessage(admin, "prefix")),
                target);
        admin.sendMessage(ApiUtils.formatText(msg));

        return Command.SINGLE_SUCCESS;
    }
}
