package red.aviora.redmc.holograms.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.holograms.HologramsPlugin;
import red.aviora.redmc.holograms.models.HologramData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HologramDataStorage {

	private final File dataFile;

	public HologramDataStorage() {
		this.dataFile = new File(HologramsPlugin.getInstance().getDataFolder(), "holograms.yml");
	}

	public Map<String, HologramData> loadHolograms() {
		Map<String, HologramData> holograms = new HashMap<>();

		if (!dataFile.exists()) {
			return holograms;
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
		ConfigurationSection section = config.getConfigurationSection("holograms");

		if (section == null) {
			return holograms;
		}

		for (String id : section.getKeys(false)) {
			ConfigurationSection sec = section.getConfigurationSection(id);
			if (sec == null) continue;

			String name = sec.getString("name", id);
			String world = sec.getString("world", "world");
			double x = sec.getDouble("x", 0);
			double y = sec.getDouble("y", 64);
			double z = sec.getDouble("z", 0);

			HologramData data = new HologramData(id, name, world, x, y, z);
			data.getLines().addAll(sec.getStringList("lines"));

			holograms.put(id, data);
		}

		return holograms;
	}

	public void saveHolograms(Map<String, HologramData> holograms) {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection section = config.createSection("holograms");

		for (HologramData data : holograms.values()) {
			ConfigurationSection sec = section.createSection(data.getId());
			sec.set("name", data.getName());
			sec.set("world", data.getWorld());
			sec.set("x", data.getX());
			sec.set("y", data.getY());
			sec.set("z", data.getZ());
			sec.set("lines", data.getLines());
		}

		try {
			config.save(dataFile);
		} catch (IOException e) {
			ApiUtils.log("Failed to save holograms.yml: " + e.getMessage());
		}
	}
}
