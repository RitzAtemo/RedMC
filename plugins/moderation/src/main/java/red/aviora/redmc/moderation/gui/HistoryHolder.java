package red.aviora.redmc.moderation.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HistoryHolder implements InventoryHolder {

    private Inventory inventory;
    private final UUID targetUuid;
    private final String targetName;
    private int page;

    public HistoryHolder(UUID targetUuid, String targetName, int page) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.page = page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public UUID getTargetUuid() { return targetUuid; }
    public String getTargetName() { return targetName; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
}
