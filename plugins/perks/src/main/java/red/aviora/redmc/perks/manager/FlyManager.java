package red.aviora.redmc.perks.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FlyManager {

	private final Set<UUID> flying = new HashSet<>();

	public boolean hasFly(UUID uuid) {
		return flying.contains(uuid);
	}

	public boolean toggle(Player player) {
		UUID uuid = player.getUniqueId();
		if (flying.contains(uuid)) {
			flying.remove(uuid);
			player.setAllowFlight(false);
			player.setFlying(false);
			return false;
		} else {
			flying.add(uuid);
			player.setAllowFlight(true);
			return true;
		}
	}

	public void onPlayerQuit(Player player) {
		UUID uuid = player.getUniqueId();
		flying.remove(uuid);
		player.setAllowFlight(false);
		player.setFlying(false);
	}
}
