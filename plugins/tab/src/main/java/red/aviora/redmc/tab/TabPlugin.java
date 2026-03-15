package red.aviora.redmc.tab;

import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.tab.listeners.PlayerJoinListener;
import red.aviora.redmc.tab.utils.TabManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TabPlugin extends JavaPlugin {

	private static TabPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private TabManager tabManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		tabManager = new TabManager();
		tabManager.loadAll();
		tabManager.startAnimations();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
	}

	@Override
	public void onDisable() {
		if (tabManager != null) {
			tabManager.stopAnimations();
		}
	}

	public static TabPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public TabManager getTabManager() { return tabManager; }
}
