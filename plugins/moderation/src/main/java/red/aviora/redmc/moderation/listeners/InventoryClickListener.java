package red.aviora.redmc.moderation.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.gui.HistoryGui;
import red.aviora.redmc.moderation.gui.HistoryHolder;
import red.aviora.redmc.moderation.gui.TicketListGui;
import red.aviora.redmc.moderation.gui.TicketListHolder;
import red.aviora.redmc.moderation.gui.TicketViewGui;
import red.aviora.redmc.moderation.gui.TicketViewHolder;
import red.aviora.redmc.moderation.managers.TicketManager;
import red.aviora.redmc.moderation.models.Ticket;
import red.aviora.redmc.moderation.models.TicketStatus;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof HistoryHolder historyHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 45) {
                // Previous page
                int newPage = historyHolder.getPage() - 1;
                if (newPage >= 0) {
                    player.closeInventory();
                    HistoryGui.open(player, historyHolder.getTargetUuid(), historyHolder.getTargetName(), newPage);
                }
            } else if (slot == 49) {
                player.closeInventory();
            } else if (slot == 53) {
                // Next page
                int newPage = historyHolder.getPage() + 1;
                player.closeInventory();
                HistoryGui.open(player, historyHolder.getTargetUuid(), historyHolder.getTargetName(), newPage);
            }

        } else if (holder instanceof TicketListHolder listHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 45) {
                int newPage = listHolder.getPage() - 1;
                if (newPage >= 0) {
                    player.closeInventory();
                    TicketListGui.open(player, newPage, listHolder.isShowOnlyOpen());
                }
            } else if (slot == 49) {
                // Toggle filter
                boolean newFilter = !listHolder.isShowOnlyOpen();
                player.closeInventory();
                TicketListGui.open(player, 0, newFilter);
            } else if (slot == 53) {
                int newPage = listHolder.getPage() + 1;
                player.closeInventory();
                TicketListGui.open(player, newPage, listHolder.isShowOnlyOpen());
            } else if (slot >= 0 && slot < 45) {
                // Click on a ticket item - figure out which ticket
                TicketManager ticketManager = ModerationPlugin.getInstance().getTicketManager();
                java.util.List<Ticket> displayed = listHolder.isShowOnlyOpen()
                    ? ticketManager.getOpen()
                    : ticketManager.getAll();
                int index = listHolder.getPage() * 45 + slot;
                if (index < displayed.size()) {
                    Ticket ticket = displayed.get(index);
                    player.closeInventory();
                    TicketViewGui.open(player, ticket);
                }
            }

        } else if (holder instanceof TicketViewHolder viewHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 45) {
                // Back to ticket list
                player.closeInventory();
                TicketListGui.open(player, 0, false);
            } else if (slot == 49) {
                // Close ticket if staff and ticket is open
                if (player.hasPermission("redmc.tickets")) {
                    TicketManager ticketManager = ModerationPlugin.getInstance().getTicketManager();
                    Ticket ticket = ticketManager.getById(viewHolder.getTicketId());
                    if (ticket != null && ticket.getStatus() == TicketStatus.OPEN) {
                        ticketManager.closeTicket(ticket.getId());
                        player.closeInventory();
                        TicketViewGui.open(player, ticket);
                    }
                }
            }
        }
    }
}
