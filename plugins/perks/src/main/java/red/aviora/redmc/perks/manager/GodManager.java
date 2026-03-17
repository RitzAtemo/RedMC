package red.aviora.redmc.perks.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GodManager {

	private final Set<UUID> godMode = new HashSet<>();

	public boolean hasGod(UUID uuid) {
		return godMode.contains(uuid);
	}

	public boolean toggle(UUID uuid) {
		if (godMode.contains(uuid)) {
			godMode.remove(uuid);
			return false;
		} else {
			godMode.add(uuid);
			return true;
		}
	}

	public void onPlayerQuit(UUID uuid) {
		godMode.remove(uuid);
	}
}
