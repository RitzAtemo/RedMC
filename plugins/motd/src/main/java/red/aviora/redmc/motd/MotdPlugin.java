package red.aviora.redmc.motd;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.motd.listeners.ServerPingListener;
import red.aviora.redmc.motd.utils.MotdManager;

public class MotdPlugin extends JavaPlugin {

	private static MotdPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private MotdManager motdManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		motdManager = new MotdManager();
		motdManager.loadTemplates();
		motdManager.loadIcons();

		getServer().getPluginManager().registerEvents(new ServerPingListener(), this);
	}

	public static MotdPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public MotdManager getMotdManager() { return motdManager; }
}
