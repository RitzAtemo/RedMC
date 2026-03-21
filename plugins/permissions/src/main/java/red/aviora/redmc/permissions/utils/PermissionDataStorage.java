package red.aviora.redmc.permissions.utils;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.permissions.models.Group;
import red.aviora.redmc.permissions.models.PermissionEntry;
import red.aviora.redmc.permissions.models.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PermissionDataStorage {

	private final File groupsFile;
	private final File playersFile;

	public PermissionDataStorage() {
		JavaPlugin plugin = JavaPlugin.getPlugin(PermissionsPlugin.class);
		groupsFile = new File(plugin.getDataFolder(), "groups.yml");
		playersFile = new File(plugin.getDataFolder(), "players.yml");
	}

	public Map<String, Group> loadGroups() {
		Map<String, Group> groups = new HashMap<>();

		if (!groupsFile.exists()) {
			JavaPlugin.getPlugin(PermissionsPlugin.class).getConfigManager().reload();
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(groupsFile);
		ConfigurationSection root = config.getConfigurationSection("groups");
		if (root == null) return groups;

		for (String id : root.getKeys(false)) {
			ConfigurationSection sec = root.getConfigurationSection(id);
			if (sec == null) continue;

			String displayName = sec.getString("displayName", id);
			Group group = new Group(id, displayName);

			for (String parent : sec.getStringList("inherits")) {
				group.addInherit(parent);
			}

			for (Map<?, ?> map : sec.getMapList("permissions")) {
				String name = (String) map.get("name");
				int weight = map.containsKey("weight") ? ((Number) map.get("weight")).intValue() : 0;
				boolean allowed = map.containsKey("allowed") ? (Boolean) map.get("allowed") : true;
				if (name != null && !name.isEmpty()) {
					group.addPermission(new PermissionEntry(name, weight, allowed));
				}
			}

			groups.put(id.toLowerCase(), group);
		}

		return groups;
	}

	public void saveGroups(Map<String, Group> groups) {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection root = config.createSection("groups");

		for (Group group : groups.values()) {
			ConfigurationSection sec = root.createSection(group.getId());
			sec.set("displayName", group.getDisplayName());

			if (!group.getInherits().isEmpty()) {
				sec.set("inherits", group.getInherits());
			}

			List<Map<String, Object>> permList = new ArrayList<>();
			for (PermissionEntry entry : group.getPermissions()) {
				Map<String, Object> map = new LinkedHashMap<>();
				map.put("name", entry.getName());
				map.put("weight", entry.getWeight());
				map.put("allowed", entry.isAllowed());
				permList.add(map);
			}
			sec.set("permissions", permList);
		}

		try {
			config.save(groupsFile);
		} catch (IOException e) {
			ApiUtils.log(e.getMessage());
		}
	}

	public Map<UUID, PlayerData> loadPlayers() {
		Map<UUID, PlayerData> players = new HashMap<>();

		if (!playersFile.exists()) {
			JavaPlugin.getPlugin(PermissionsPlugin.class).getConfigManager().reload();
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(playersFile);
		ConfigurationSection root = config.getConfigurationSection("players");
		if (root == null) return players;

		for (String uuidStr : root.getKeys(false)) {
			ConfigurationSection sec = root.getConfigurationSection(uuidStr);
			if (sec == null) continue;

			UUID uuid = UUID.fromString(uuidStr);
			String name = sec.getString("name", "Unknown");

			PlayerData player = new PlayerData(uuid, name);

			for (String gid : sec.getStringList("groups")) {
				player.addGroup(gid);
			}

			for (Map<?, ?> map : sec.getMapList("permissions")) {
				String permName = (String) map.get("name");
				int weight = map.containsKey("weight") ? ((Number) map.get("weight")).intValue() : 0;
				boolean allowed = map.containsKey("allowed") ? (Boolean) map.get("allowed") : true;
				if (permName != null && !permName.isEmpty()) {
					player.addPermission(new PermissionEntry(permName, weight, allowed));
				}
			}

			players.put(uuid, player);
		}

		return players;
	}

	public void savePlayers(Map<UUID, PlayerData> players) {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection root = config.createSection("players");

		for (PlayerData player : players.values()) {
			ConfigurationSection sec = root.createSection(player.getUuid().toString());
			sec.set("name", player.getName());
			sec.set("groups", player.getGroupIds());

			List<Map<String, Object>> permList = new ArrayList<>();
			for (PermissionEntry entry : player.getPermissions()) {
				Map<String, Object> map = new LinkedHashMap<>();
				map.put("name", entry.getName());
				map.put("weight", entry.getWeight());
				map.put("allowed", entry.isAllowed());
				permList.add(map);
			}
			sec.set("permissions", permList);
		}

		try {
			config.save(playersFile);
		} catch (IOException e) {
			ApiUtils.log(e.getMessage());
		}
	}
}
