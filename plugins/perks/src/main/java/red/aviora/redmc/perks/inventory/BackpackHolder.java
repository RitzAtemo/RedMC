package red.aviora.redmc.perks.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class BackpackHolder implements InventoryHolder {

	private final UUID playerUuid;
	private Inventory inventory;

	public BackpackHolder(UUID playerUuid) {
		this.playerUuid = playerUuid;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public UUID getPlayerUuid() {
		return playerUuid;
	}
}
