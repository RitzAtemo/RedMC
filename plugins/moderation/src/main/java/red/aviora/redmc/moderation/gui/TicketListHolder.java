package red.aviora.redmc.moderation.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TicketListHolder implements InventoryHolder {

    private Inventory inventory;
    private int page;
    private boolean showOnlyOpen;

    public TicketListHolder(int page, boolean showOnlyOpen) {
        this.page = page;
        this.showOnlyOpen = showOnlyOpen;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public boolean isShowOnlyOpen() { return showOnlyOpen; }
    public void setShowOnlyOpen(boolean showOnlyOpen) { this.showOnlyOpen = showOnlyOpen; }
}
