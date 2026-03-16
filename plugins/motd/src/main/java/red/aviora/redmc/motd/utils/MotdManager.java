package red.aviora.redmc.motd.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.motd.MotdPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class MotdManager {

	private final List<List<String>> templates = new ArrayList<>();
	private final List<CachedServerIcon> icons = new ArrayList<>();
	private final AtomicInteger templateIndex = new AtomicInteger(0);
	private final AtomicInteger iconIndex = new AtomicInteger(0);

	public void loadTemplates() {
		templates.clear();
		templateIndex.set(0);
		MotdPlugin plugin = JavaPlugin.getPlugin(MotdPlugin.class);
		YamlConfiguration config = plugin.getConfigManager().getConfig("config.yml");
		if (config == null) return;
		List<?> rawTemplates = config.getList("motd.templates");
		if (rawTemplates == null) return;
		for (Object rawTemplate : rawTemplates) {
			if (rawTemplate instanceof List<?> lines) {
				List<String> lineList = lines.stream()
					.filter(l -> l instanceof String)
					.map(l -> (String) l)
					.toList();
				if (!lineList.isEmpty()) {
					templates.add(lineList);
				}
			}
		}
	}

	public void loadIcons() {
		icons.clear();
		iconIndex.set(0);
		MotdPlugin plugin = JavaPlugin.getPlugin(MotdPlugin.class);
		ConfigManager configManager = plugin.getConfigManager();
		List<?> rawFiles = configManager.getList("config.yml", "icons.files");
		if (rawFiles == null) return;
		for (Object rawFile : rawFiles) {
			if (rawFile instanceof String path) {
				File iconFile = new File(plugin.getDataFolder(), path);
				if (!iconFile.exists()) {
					ApiUtils.logArgs(
						"MOTD icon file not found: %path%",
						"%path%", iconFile.getAbsolutePath()
					);
					continue;
				}
				try {
					icons.add(Bukkit.loadServerIcon(iconFile));
				} catch (Exception e) {
					ApiUtils.logArgs(
						"Failed to load MOTD icon %path%: %error%",
						"%path%", path,
						"%error%", e.getMessage()
					);
				}
			}
		}
	}

	public List<String> getTemplate() {
		if (templates.isEmpty()) return List.of("<#F0F8FF>A Minecraft Server", "");
		MotdPlugin plugin = JavaPlugin.getPlugin(MotdPlugin.class);
		String mode = plugin.getConfigManager().getString("config.yml", "motd.mode", "random");
		if ("sequential".equals(mode)) {
			int idx = templateIndex.getAndUpdate(i -> (i + 1) % templates.size());
			return templates.get(idx);
		}
		return templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
	}

	public CachedServerIcon getIcon() {
		if (icons.isEmpty()) return null;
		MotdPlugin plugin = JavaPlugin.getPlugin(MotdPlugin.class);
		String mode = plugin.getConfigManager().getString("config.yml", "icons.mode", "random");
		if ("sequential".equals(mode)) {
			int idx = iconIndex.getAndUpdate(i -> (i + 1) % icons.size());
			return icons.get(idx);
		}
		return icons.get(ThreadLocalRandom.current().nextInt(icons.size()));
	}

}
