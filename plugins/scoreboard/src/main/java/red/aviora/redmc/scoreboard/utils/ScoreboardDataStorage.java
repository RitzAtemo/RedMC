package red.aviora.redmc.scoreboard.utils;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.scoreboard.ScoreboardPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardDataStorage {

	private final File playersFile;

	public ScoreboardDataStorage() {
		this.playersFile = new File(ScoreboardPlugin.getInstance().getDataFolder(), "scoreboard_players.yml");
	}

	public Map<UUID, Boolean> loadVisibility() {
		Map<UUID, Boolean> visibility = new HashMap<>();

		if (!playersFile.exists()) {
			return visibility;
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(playersFile);
		ConfigurationSection playersSection = config.getConfigurationSection("players");

		if (playersSection == null) {
			return visibility;
		}

		for (String uuidStr : playersSection.getKeys(false)) {
			try {
				UUID uuid = UUID.fromString(uuidStr);
				boolean visible = playersSection.getBoolean(uuidStr + ".visible", true);
				visibility.put(uuid, visible);
			} catch (IllegalArgumentException e) {
				ApiUtils.logArgs("Invalid UUID in scoreboard_players.yml: %uuid%", "%uuid%", uuidStr);
			}
		}

		return visibility;
	}

	public void saveVisibility(Map<UUID, Boolean> visibility) {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection playersSection = config.createSection("players");

		for (Map.Entry<UUID, Boolean> entry : visibility.entrySet()) {
			playersSection.set(entry.getKey().toString() + ".visible", entry.getValue());
		}

		try {
			config.save(playersFile);
		} catch (IOException e) {
			ApiUtils.log("Failed to save scoreboard_players.yml: " + e.getMessage());
		}
	}
}
