package red.aviora.redmc.perks.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoFallManager {

	private final Set<UUID> protected_ = new HashSet<>();

	public boolean hasNoFall(UUID uuid) {
		return protected_.contains(uuid);
	}

	public boolean toggle(UUID uuid) {
		if (protected_.contains(uuid)) {
			protected_.remove(uuid);
			return false;
		} else {
			protected_.add(uuid);
			return true;
		}
	}

	public void onPlayerQuit(UUID uuid) {
		protected_.remove(uuid);
	}
}
