package red.aviora.redmc.moderation.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TicketViewHolder implements InventoryHolder {

    private Inventory inventory;
    private final String ticketId;

    public TicketViewHolder(String ticketId) {
        this.ticketId = ticketId;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getTicketId() { return ticketId; }
}
