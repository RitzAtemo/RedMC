package red.aviora.redmc.perks.manager;

import org.bukkit.entity.Player;
import red.aviora.redmc.perks.PerksPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

	private final Set<UUID> vanished = new HashSet<>();

	public boolean isVanished(UUID uuid) {
		return vanished.contains(uuid);
	}

	public boolean toggle(Player player) {
		if (vanished.contains(player.getUniqueId())) {
			unvanish(player);
			return false;
		} else {
			vanish(player);
			return true;
		}
	}

	public void vanish(Player player) {
		vanished.add(player.getUniqueId());
		PerksPlugin plugin = PerksPlugin.getInstance();
		for (Player online : player.getServer().getOnlinePlayers()) {
			if (!online.equals(player)) {
				online.hideEntity(plugin, player);
			}
		}
	}

	public void unvanish(Player player) {
		vanished.remove(player.getUniqueId());
		PerksPlugin plugin = PerksPlugin.getInstance();
		for (Player online : player.getServer().getOnlinePlayers()) {
			online.showEntity(plugin, player);
		}
	}

	public void onPlayerQuit(UUID uuid) {
		vanished.remove(uuid);
	}
}
