package red.aviora.redmc.vault.utils;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VaultDataStorage {

	private final File playersFile;
	private final ConfigManager configManager;

	public VaultDataStorage() {
		this.configManager = VaultPlugin.getInstance().getConfigManager();
		this.playersFile = new File(VaultPlugin.getInstance().getDataFolder(), "vault_players.yml");
	}

	public Map<UUID, VaultPlayerData> loadPlayers() {
		Map<UUID, VaultPlayerData> players = new HashMap<>();

		if (!playersFile.exists()) {
			configManager.reload();
			return players;
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(playersFile);
		ConfigurationSection playersSection = config.getConfigurationSection("players");

		if (playersSection == null) {
			return players;
		}

		double startingBalance = configManager.getDouble("config.yml", "starting-balance", 0.0);

		for (String uuidStr : playersSection.getKeys(false)) {
			try {
				UUID uuid = UUID.fromString(uuidStr);
				ConfigurationSection playerSec = playersSection.getConfigurationSection(uuidStr);

				if (playerSec == null) continue;

				String name = playerSec.getString("name", "Unknown");

				VaultPlayerData playerData = new VaultPlayerData(uuid, name, 0.0);

				ConfigurationSection balancesSection = playerSec.getConfigurationSection("balances");
				if (balancesSection != null) {
					for (String currencyId : balancesSection.getKeys(false)) {
						double balance = balancesSection.getDouble(currencyId, 0.0);
						playerData.setBalance(currencyId, balance);
					}
				}

				players.put(uuid, playerData);
			} catch (IllegalArgumentException e) {
				ApiUtils.logArgs("Invalid UUID in vault_players.yml: %uuid%", "%uuid%", uuidStr);
			}
		}

		return players;
	}

	public void savePlayers(Map<UUID, VaultPlayerData> players) {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection playersSection = config.createSection("players");

		for (VaultPlayerData playerData : players.values()) {
			ConfigurationSection playerSec = playersSection.createSection(playerData.getUuid().toString());
			playerSec.set("name", playerData.getName());

			ConfigurationSection balancesSection = playerSec.createSection("balances");
			for (var entry : playerData.getBalances().entrySet()) {
				balancesSection.set(entry.getKey(), entry.getValue());
			}
		}

		try {
			config.save(playersFile);
		} catch (IOException e) {
			ApiUtils.log("Failed to save vault_players.yml: " + e.getMessage());
		}
	}
}
