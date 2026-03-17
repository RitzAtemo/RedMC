package red.aviora.redmc.perks.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.perks.inventory.BackpackHolder;
import red.aviora.redmc.perks.storage.PlayerData;

public class BackpackManager {

	public void openBackpack(Player player) {
		PerksPlugin plugin = PerksPlugin.getInstance();
		int size = plugin.getConfigManager().getInt("config.yml", "backpack.size", 54);
		PlayerData playerData = plugin.getDataStorage().getPlayerData(player.getUniqueId());

		BackpackHolder holder = new BackpackHolder(player.getUniqueId());
		Inventory inv = Bukkit.createInventory(
			holder,
			size,
			ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "backpack.title"))
		);
		holder.setInventory(inv);

		ItemStack[] saved = playerData.getBackpackContents();
		if (saved != null) {
			for (int i = 0; i < Math.min(saved.length, size); i++) {
				if (saved[i] != null) inv.setItem(i, saved[i]);
			}
		}

		player.openInventory(inv);
	}
}
