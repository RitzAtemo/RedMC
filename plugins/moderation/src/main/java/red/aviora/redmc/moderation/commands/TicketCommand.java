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
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.moderation.managers.TicketManager;
import red.aviora.redmc.moderation.models.Ticket;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TicketCommand {

    public static class CreateCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!(sender instanceof Player player)) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.only-players"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            if (!player.hasPermission("redmc.ticket")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            String message = StringArgumentType.getString(context, "message");
            Ticket ticket = plugin.getTicketManager().createTicket(player.getUniqueId(), player.getName(), message);

            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "ticket.created"),
                "%prefix%", locale.getMessage(sender, "prefix"),
                "%id%", ticket.getShortId());

            // Notify online staff
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (online.hasPermission("redmc.tickets")) {
                    String notify = VaultPlugin.resolvePlayer(
                        ApiUtils.formatTextString(locale.getMessage(online, "ticket.notify-staff"),
                            "%id%", ticket.getShortId(),
                            "%message%", message),
                        player);
                    online.sendMessage(ApiUtils.formatText(notify));
                }
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class ListCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!(sender instanceof Player player)) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.only-players"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            if (!player.hasPermission("redmc.ticket")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            List<Ticket> tickets = plugin.getTicketManager().getByAuthor(player.getUniqueId());
            if (tickets.isEmpty()) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "ticket.no-tickets"),
                    "%prefix%", locale.getMessage(sender, "prefix"));
                return Command.SINGLE_SUCCESS;
            }

            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "ticket.list-header"),
                "%prefix%", locale.getMessage(sender, "prefix"));

            for (Ticket ticket : tickets) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "ticket.list-entry"),
                    "%id%", ticket.getShortId(),
                    "%status%", ticket.getStatus().name(),
                    "%message%", ticket.getMessage().length() > 30 ? ticket.getMessage().substring(0, 30) + "..." : ticket.getMessage());
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class CloseCommand implements Command<CommandSourceStack> {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSender sender = context.getSource().getSender();
            ModerationPlugin plugin = JavaPlugin.getPlugin(ModerationPlugin.class);
            LocaleManager locale = plugin.getLocaleManager();

            if (!(sender instanceof Player player)) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.only-players"),
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

            // Check ownership or permission
            if (!ticket.getAuthorUuid().equals(player.getUniqueId()) && !player.hasPermission("redmc.tickets")) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    locale.getMessage(sender, "error.no-permission"),
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
}
