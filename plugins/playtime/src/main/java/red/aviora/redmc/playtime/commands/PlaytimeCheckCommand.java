package red.aviora.redmc.playtime.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.playtime.PlaytimePlugin;
import red.aviora.redmc.playtime.utils.PlaytimeFormatter;

@SuppressWarnings("UnstableApiUsage")
public class PlaytimeCheckCommand implements Command<CommandSourceStack> {

    private final boolean withTarget;

    public PlaytimeCheckCommand(boolean withTarget) {
        this.withTarget = withTarget;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSender sender = context.getSource().getSender();
        LocaleManager locale = PlaytimePlugin.getInstance().getLocaleManager();

        if (withTarget) {
            if (!sender.hasPermission("redmc.playtime.others")) {
                throw ApiUtils.noPermissionException(locale, sender);
            }

            String targetName = StringArgumentType.getString(context, "player");
            Player target = sender.getServer().getPlayer(targetName);

            if (target == null) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                        locale.getMessage(sender, "error.player-not-found"),
                        "%prefix%", locale.getMessage(sender, "prefix"),
                        "%player%", targetName);
                return 0;
            }

            long seconds = PlaytimePlugin.getInstance().getPlaytimeManager()
                    .getTotalPlaytimeSeconds(target.getUniqueId());
            ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "playtime.other"),
                    "%prefix%", locale.getMessage(sender, "prefix"),
                    "%player%", target.getName(),
                    "%playtime%", PlaytimeFormatter.format(seconds));
        } else {
            if (!(sender instanceof Player player)) {
                throw ApiUtils.noPermissionException(locale, sender);
            }

            long seconds = PlaytimePlugin.getInstance().getPlaytimeManager()
                    .getTotalPlaytimeSeconds(player.getUniqueId());
            ApiUtils.sendPlayerMessageArgs(player,
                    locale.getMessage(player, "playtime.own"),
                    "%prefix%", locale.getMessage(player, "prefix"),
                    "%playtime%", PlaytimeFormatter.format(seconds));
        }

        return Command.SINGLE_SUCCESS;
    }
}
