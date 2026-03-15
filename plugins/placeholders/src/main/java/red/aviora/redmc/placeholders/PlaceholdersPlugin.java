package red.aviora.redmc.placeholders;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.placeholders.registries.GeneralRegistryGenerator;
import red.aviora.redmc.placeholders.utils.PlaceholderRegistryManager;
import red.aviora.redmc.placeholders.utils.PlaceholderParser;

import java.util.regex.Pattern;

public class PlaceholdersPlugin extends JavaPlugin {

	public static final String NULL_VALUE = "";

	private static PlaceholdersPlugin instance;

	private ConfigManager configManager;

	private PlaceholderRegistryManager registryManager;
	private PlaceholderParser placeholderResolver;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");

		registryManager = new PlaceholderRegistryManager();

		String pattern = configManager.getString("config.yml", "pattern", "##(.*?)##");
		placeholderResolver = new PlaceholderParser(registryManager, Pattern.compile(pattern));

		int generalRegistryWeight = configManager.getInt("config.yml", "general-registry-weight", 0);
		registryManager.addRegistry(new GeneralRegistryGenerator().generate(), generalRegistryWeight);
	}

	public static PlaceholdersPlugin getInstance() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public PlaceholderRegistryManager getRegistryManager() {
		return registryManager;
	}

	public PlaceholderParser getPlaceholderResolver() {
		return placeholderResolver;
	}
}