package red.aviora.redmc.holograms;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.holograms.listeners.PlayerJoinListener;
import red.aviora.redmc.holograms.utils.HologramManager;
import red.aviora.redmc.holograms.utils.HologramRefreshTask;

public class HologramsPlugin extends JavaPlugin {

	private static HologramsPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private HologramManager hologramManager;
	private HologramRefreshTask refreshTask;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		hologramManager = new HologramManager();
		hologramManager.loadAll();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

		refreshTask = new HologramRefreshTask();
		refreshTask.start();
	}

	@Override
	public void onDisable() {
		if (refreshTask != null) {
			refreshTask.stop();
		}
		if (hologramManager != null) {
			hologramManager.despawnAll();
		}
	}

	public static HologramsPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public HologramManager getHologramManager() { return hologramManager; }
}
