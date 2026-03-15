package red.aviora.redmc.npc;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.npc.listeners.PlayerJoinListener;
import red.aviora.redmc.npc.listeners.PlayerQuitListener;
import red.aviora.redmc.npc.utils.NpcManager;

public class NpcPlugin extends JavaPlugin {

	private static NpcPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private NpcManager npcManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		npcManager = new NpcManager();
		npcManager.loadAll();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
	}

	@Override
	public void onDisable() {
		if (npcManager != null) {
			npcManager.despawnAll();
		}
	}

	public static NpcPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public NpcManager getNpcManager() { return npcManager; }
}
