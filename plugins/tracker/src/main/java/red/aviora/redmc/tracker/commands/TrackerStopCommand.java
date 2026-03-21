package red.aviora.redmc.tracker.commands;

import com.mojang.brigadier.Command;
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

@SuppressWarnings("UnstableApiUsage")
public class TrackerStopCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        LocaleManager locale = TrackerPlugin.getInstance().getLocaleManager();
        TrackerManager manager = TrackerPlugin.getInstance().getTrackerManager();

        if (!(sender instanceof Player admin)) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(sender, "players-only"),
                            "%prefix%", locale.getMessage(sender, "prefix"))
            )).create();
        }

        if (!manager.isTracking(admin)) {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(sender, "not-tracking"),
                            "%prefix%", locale.getMessage(sender, "prefix"))
            )).create();
        }

        manager.stopTracking(admin);

        ApiUtils.sendPlayerMessageArgs(admin,
                locale.getMessage(admin, "tracking-stopped"),
                "%prefix%", locale.getMessage(admin, "prefix"));

        return Command.SINGLE_SUCCESS;
    }
}
