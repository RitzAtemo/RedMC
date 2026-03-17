package red.aviora.redmc.perks.manager;

import red.aviora.redmc.perks.PerksPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

	private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

	public boolean isOnCooldown(UUID uuid, String feature) {
		long cooldownSeconds = getCooldownSeconds(feature);
		if (cooldownSeconds <= 0) return false;
		Map<String, Long> playerCooldowns = cooldowns.get(uuid);
		if (playerCooldowns == null) return false;
		Long lastUse = playerCooldowns.get(feature);
		if (lastUse == null) return false;
		return (System.currentTimeMillis() - lastUse) < cooldownSeconds * 1000L;
	}

	public long getRemainingSeconds(UUID uuid, String feature) {
		long cooldownSeconds = getCooldownSeconds(feature);
		Map<String, Long> playerCooldowns = cooldowns.get(uuid);
		if (playerCooldowns == null) return 0;
		Long lastUse = playerCooldowns.get(feature);
		if (lastUse == null) return 0;
		long elapsed = (System.currentTimeMillis() - lastUse) / 1000L;
		return Math.max(0L, cooldownSeconds - elapsed);
	}

	public void setCooldown(UUID uuid, String feature) {
		cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(feature, System.currentTimeMillis());
	}

	private long getCooldownSeconds(String feature) {
		return PerksPlugin.getInstance().getConfigManager().getInt("config.yml", "cooldowns." + feature, 0);
	}
}
