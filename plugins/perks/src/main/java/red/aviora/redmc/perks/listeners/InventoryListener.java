package red.aviora.redmc.perks.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.perks.inventory.BackpackHolder;
import red.aviora.redmc.perks.inventory.DisposeHolder;
import red.aviora.redmc.perks.inventory.EcSeeHolder;
import red.aviora.redmc.perks.inventory.InvseeHolder;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof BackpackHolder holder) {
			ItemStack[] contents = event.getInventory().getContents();
			PerksPlugin.getInstance().getDataStorage().getPlayerData(holder.getPlayerUuid()).setBackpackContents(contents.clone());
		}

		if (event.getInventory().getHolder() instanceof DisposeHolder) {
			event.getInventory().clear();
		}

		if (event.getInventory().getHolder() instanceof InvseeHolder holder) {
			ItemStack[] contents = event.getInventory().getContents();
			PerksPlugin.getInstance().getOfflinePlayerInventoryUtil().saveInventory(holder.getTargetUuid(), contents);
		}

		if (event.getInventory().getHolder() instanceof EcSeeHolder holder) {
			ItemStack[] contents = event.getInventory().getContents();
			PerksPlugin.getInstance().getOfflinePlayerInventoryUtil().saveEnderChest(holder.getTargetUuid(), contents);
		}
	}
}
