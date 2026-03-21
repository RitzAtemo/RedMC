package red.aviora.redmc.moderation.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.gui.TicketListGui;
import red.aviora.redmc.moderation.gui.TicketViewGui;
import red.aviora.redmc.moderation.managers.TicketManager;
import red.aviora.redmc.moderation.models.Ticket;
import red.aviora.redmc.vault.VaultPlugin;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class TicketsCommand {

    public static class ListCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!sender.hasPermission("redmc.tickets")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            if (!(sender instanceof Player player)) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.only-players"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
                TicketListGui.open(player, 0, false)
            );

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class ViewCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!sender.hasPermission("redmc.tickets")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            if (!(sender instanceof Player player)) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.only-players"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            String id = StringArgumentType.getString(context, "id");
            Ticket ticket = plugin.getTicketManager().getById(id);

            if (ticket == null) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.ticket-not-found"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
                TicketViewGui.open(player, ticket)
            );

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class CloseCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!sender.hasPermission("redmc.tickets")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            String id = StringArgumentType.getString(context, "id");
            TicketManager ticketManager = plugin.getTicketManager();
            Ticket ticket = ticketManager.getById(id);

            if (ticket == null) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.ticket-not-found"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            boolean closed = ticketManager.closeTicket(ticket.getId());
            if (closed) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "ticket.closed"),
                    "%prefix%", locale.getMessage(sender, "prefix"),
                    "%id%", ticket.getShortId());
            } else {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "ticket.already-closed"),
                    "%prefix%", locale.getMessage(sender, "prefix"),
                    "%id%", ticket.getShortId());
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class ReplyCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!sender.hasPermission("redmc.tickets")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            String id = StringArgumentType.getString(context, "id");
            String message = StringArgumentType.getString(context, "message");

            UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : new UUID(0, 0);
            String staffName = sender.getName();
            Player staffPlayer = sender instanceof Player p2 ? p2 : null;

            TicketManager ticketManager = plugin.getTicketManager();
            Ticket ticket = ticketManager.getById(id);

            if (ticket == null) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.ticket-not-found"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            ticketManager.addReply(ticket.getId(), staffUuid, staffName, message);

            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "ticket.reply-sent"),
                "%prefix%", locale.getMessage(sender, "prefix"),
                "%id%", ticket.getShortId());

            // Notify all online staff
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (online.hasPermission("redmc.tickets") && !online.getUniqueId().equals(staffUuid)) {
                    String notify = VaultPlugin.resolveTwoPlayers(
                        ApiUtils.formatTextString(locale.getMessage(online, "ticket.notify-reply"),
                            "%id%", ticket.getShortId(),
                            "%message%", message),
                        staffPlayer, (Player) null);
                    online.sendMessage(ApiUtils.formatText(notify));
                }
            }

            return Command.SINGLE_SUCCESS;
        }
    }
}
