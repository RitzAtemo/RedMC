package red.aviora.redmc.perks.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {

	private final Set<UUID> frozen = new HashSet<>();

	public boolean isFrozen(UUID uuid) {
		return frozen.contains(uuid);
	}

	public boolean isEmpty() {
		return frozen.isEmpty();
	}

	public boolean toggle(UUID uuid) {
		if (frozen.contains(uuid)) {
			frozen.remove(uuid);
			return false;
		} else {
			frozen.add(uuid);
			return true;
		}
	}

	public void onPlayerQuit(UUID uuid) {
		frozen.remove(uuid);
	}
}
