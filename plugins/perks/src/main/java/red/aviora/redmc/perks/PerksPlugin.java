package red.aviora.redmc.perks;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.perks.listeners.InventoryListener;
import red.aviora.redmc.perks.listeners.PerksPlayerListener;
import red.aviora.redmc.perks.manager.BackpackManager;
import red.aviora.redmc.perks.manager.CooldownManager;
import red.aviora.redmc.perks.manager.FlyManager;
import red.aviora.redmc.perks.manager.FreezeManager;
import red.aviora.redmc.perks.manager.GodManager;
import red.aviora.redmc.perks.manager.NoFallManager;
import red.aviora.redmc.perks.manager.VanishManager;
import red.aviora.redmc.perks.storage.PerksDataStorage;

public class PerksPlugin extends JavaPlugin {

	private static PerksPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private PerksDataStorage dataStorage;
	private CooldownManager cooldownManager;
	private FlyManager flyManager;
	private NoFallManager noFallManager;
	private BackpackManager backpackManager;
	private VanishManager vanishManager;
	private GodManager godManager;
	private FreezeManager freezeManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		dataStorage = new PerksDataStorage();
		dataStorage.loadAll();

		cooldownManager = new CooldownManager();
		flyManager = new FlyManager();
		noFallManager = new NoFallManager();
		backpackManager = new BackpackManager();
		vanishManager = new VanishManager();
		godManager = new GodManager();
		freezeManager = new FreezeManager();

		getServer().getPluginManager().registerEvents(new PerksPlayerListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
	}

	@Override
	public void onDisable() {
		if (dataStorage != null) {
			dataStorage.saveAll();
		}
	}

	public static PerksPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public PerksDataStorage getDataStorage() { return dataStorage; }
	public CooldownManager getCooldownManager() { return cooldownManager; }
	public FlyManager getFlyManager() { return flyManager; }
	public NoFallManager getNoFallManager() { return noFallManager; }
	public BackpackManager getBackpackManager() { return backpackManager; }
	public VanishManager getVanishManager() { return vanishManager; }
	public GodManager getGodManager() { return godManager; }
	public FreezeManager getFreezeManager() { return freezeManager; }
}
