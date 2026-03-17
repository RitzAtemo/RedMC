package red.aviora.redmc.perks.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PerksDataStorage {

	private final Map<UUID, PlayerData> data = new ConcurrentHashMap<>();
	private final File playersFile;

	public PerksDataStorage() {
		this.playersFile = new File(PerksPlugin.getInstance().getDataFolder(), "perks_players.yml");
	}

	public void loadAll() {
		data.clear();
		if (!playersFile.exists()) return;

		YamlConfiguration config = YamlConfiguration.loadConfiguration(playersFile);
		ConfigurationSection playersSection = config.getConfigurationSection("players");
		if (playersSection == null) return;

		int backpackSize = PerksPlugin.getInstance().getConfigManager().getInt("config.yml", "backpack.size", 54);

		for (String uuidStr : playersSection.getKeys(false)) {
			try {
				UUID uuid = UUID.fromString(uuidStr);
				ConfigurationSection playerSec = playersSection.getConfigurationSection(uuidStr);
				if (playerSec == null) continue;

				PlayerData playerData = new PlayerData();
				playerData.setJoinMessage(playerSec.getString("join-message"));
				playerData.setQuitMessage(playerSec.getString("quit-message"));

				ConfigurationSection backpackSec = playerSec.getConfigurationSection("backpack");
				if (backpackSec != null) {
					ItemStack[] contents = new ItemStack[backpackSize];
					for (String slotKey : backpackSec.getKeys(false)) {
						try {
							int slot = Integer.parseInt(slotKey);
							if (slot >= 0 && slot < backpackSize) {
								contents[slot] = backpackSec.getItemStack(slotKey);
							}
						} catch (NumberFormatException ignored) {}
					}
					playerData.setBackpackContents(contents);
				}

				data.put(uuid, playerData);
			} catch (IllegalArgumentException e) {
				ApiUtils.logArgs("Invalid UUID in perks_players.yml: %uuid%", "%uuid%", uuidStr);
			}
		}
	}

	public void saveAll() {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection playersSection = config.createSection("players");

		for (Map.Entry<UUID, PlayerData> entry : data.entrySet()) {
			UUID uuid = entry.getKey();
			PlayerData playerData = entry.getValue();
			ConfigurationSection playerSec = playersSection.createSection(uuid.toString());

			if (playerData.getJoinMessage() != null) {
				playerSec.set("join-message", playerData.getJoinMessage());
			}
			if (playerData.getQuitMessage() != null) {
				playerSec.set("quit-message", playerData.getQuitMessage());
			}

			ItemStack[] contents = playerData.getBackpackContents();
			if (contents != null) {
				ConfigurationSection backpackSec = playerSec.createSection("backpack");
				for (int i = 0; i < contents.length; i++) {
					if (contents[i] != null && !contents[i].getType().isAir()) {
						backpackSec.set(String.valueOf(i), contents[i]);
					}
				}
			}
		}

		try {
			config.save(playersFile);
		} catch (IOException e) {
			ApiUtils.log("Failed to save perks_players.yml: " + e.getMessage());
		}
	}

	public PlayerData getOrCreate(UUID uuid) {
		return data.computeIfAbsent(uuid, k -> new PlayerData());
	}

	public PlayerData getPlayerData(UUID uuid) {
		return getOrCreate(uuid);
	}
}
