package red.aviora.redmc.vault.utils;

import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.permissions.models.Group;
import red.aviora.redmc.permissions.models.PermissionEntry;
import red.aviora.redmc.permissions.models.PlayerData;
import red.aviora.redmc.permissions.utils.PermissionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class VaultMetaResolver {

	private static final String PREFIX_PERM = "vault.prefix.";
	private static final String SUFFIX_PERM = "vault.suffix.";
	private static final String ALTNAME_PERM = "vault.altname.";

	public static String getPrefix(UUID uuid, PermissionManager pm) {
		return resolvePlayer(uuid, PREFIX_PERM, pm);
	}

	public static String getSuffix(UUID uuid, PermissionManager pm) {
		return resolvePlayer(uuid, SUFFIX_PERM, pm);
	}

	public static String getAltName(UUID uuid, PermissionManager pm) {
		return resolvePlayer(uuid, ALTNAME_PERM, pm);
	}

	public static String getGroupPrefix(String groupId, PermissionManager pm) {
		return resolveGroup(groupId, PREFIX_PERM, pm);
	}

	public static String getGroupSuffix(String groupId, PermissionManager pm) {
		return resolveGroup(groupId, SUFFIX_PERM, pm);
	}

	private static String resolvePlayer(UUID uuid, String permPrefix, PermissionManager pm) {
		PlayerData player = pm.getPlayers().get(uuid);
		if (player == null) {
			return "";
		}

		PermissionEntry highest = null;
		int maxWeight = -1;

		for (PermissionEntry entry : player.getPermissions()) {
			if (entry.getName().startsWith(permPrefix) && entry.isAllowed()) {
				if (entry.getWeight() > maxWeight) {
					highest = entry;
					maxWeight = entry.getWeight();
				}
			}
		}

		for (String groupId : player.getGroupIds()) {
			Group group = pm.getGroups().get(groupId.toLowerCase());
			if (group == null) continue;

			for (PermissionEntry entry : group.getPermissions()) {
				if (entry.getName().startsWith(permPrefix) && entry.isAllowed()) {
					if (entry.getWeight() > maxWeight) {
						highest = entry;
						maxWeight = entry.getWeight();
					}
				}
			}
		}

		if (highest == null) {
			return "";
		}

		return highest.getName().substring(permPrefix.length());
	}

	private static String resolveGroup(String groupId, String permPrefix, PermissionManager pm) {
		Group group = pm.getGroups().get(groupId.toLowerCase());
		if (group == null) {
			return "";
		}

		PermissionEntry highest = null;
		int maxWeight = -1;

		for (PermissionEntry entry : group.getPermissions()) {
			if (entry.getName().startsWith(permPrefix) && entry.isAllowed()) {
				if (entry.getWeight() > maxWeight) {
					highest = entry;
					maxWeight = entry.getWeight();
				}
			}
		}

		if (highest == null) {
			return "";
		}

		return highest.getName().substring(permPrefix.length());
	}
}
