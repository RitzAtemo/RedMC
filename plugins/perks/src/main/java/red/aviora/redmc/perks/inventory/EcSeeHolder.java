package red.aviora.redmc.perks.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class EcSeeHolder implements InventoryHolder {

	private final UUID targetUuid;
	private Inventory inventory;

	public EcSeeHolder(UUID targetUuid) {
		this.targetUuid = targetUuid;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public UUID getTargetUuid() {
		return targetUuid;
	}
}
