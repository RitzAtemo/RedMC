package red.aviora.redmc.permissions.utils;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import red.aviora.redmc.permissions.models.Group;
import red.aviora.redmc.permissions.models.PermissionEntry;
import red.aviora.redmc.permissions.models.PlayerData;

import java.util.*;

public class PermissionResolver {

	public static Map<String, Boolean> resolve(PlayerData player, Map<String, Group> groups) {
		Map<String, PermissionEntry> resolved = new HashMap<>();

		for (String groupId : player.getGroupIds()) {
			resolveGroup(groupId, groups, resolved, new HashSet<>());
		}

		applyPermissions(player.getPermissions(), resolved);

		return expandWildcards(resolved);
	}

	private static void resolveGroup(String groupId, Map<String, Group> groups,
	                                  Map<String, PermissionEntry> target, Set<String> visited) {
		String lower = groupId.toLowerCase();
		if (visited.contains(lower)) return;
		visited.add(lower);

		Group group = groups.get(lower);
		if (group == null) return;

		for (String parentId : group.getInherits()) {
			resolveGroup(parentId, groups, target, visited);
		}

		applyPermissions(group.getPermissions(), target);
	}

	private static Map<String, Boolean> expandWildcards(Map<String, PermissionEntry> resolved) {
		List<Map.Entry<String, PermissionEntry>> wildcards = resolved.entrySet().stream()
				.filter(e -> isWildcard(e.getKey()))
				.sorted((a, b) -> b.getKey().length() - a.getKey().length())
				.toList();

		Map<String, Boolean> result = new HashMap<>();

		for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
			String name = perm.getName().toLowerCase();
			for (Map.Entry<String, PermissionEntry> wildcard : wildcards) {
				if (matchesWildcard(name, wildcard.getKey())) {
					result.put(name, wildcard.getValue().isAllowed());
					break;
				}
			}
		}

		for (Map.Entry<String, PermissionEntry> entry : resolved.entrySet()) {
			if (!isWildcard(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue().isAllowed());
			}
		}

		return result;
	}

	private static boolean isWildcard(String name) {
		return name.equals("*") || name.endsWith(".*");
	}

	private static boolean matchesWildcard(String permission, String pattern) {
		if (pattern.equals("*")) return true;
		if (pattern.endsWith(".*")) {
			String prefix = pattern.substring(0, pattern.length() - 2);
			return permission.equals(prefix) || permission.startsWith(prefix + ".");
		}
		return false;
	}

	private static void applyPermissions(List<PermissionEntry> entries, Map<String, PermissionEntry> target) {
		for (PermissionEntry entry : entries) {
			String name = entry.getName().toLowerCase();
			PermissionEntry existing = target.get(name);
			if (existing == null || entry.getWeight() > existing.getWeight()) {
				target.put(name, entry);
			}
		}
	}
}
