package red.aviora.redmc.vault;

import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;
import red.aviora.redmc.vault.listeners.PlayerJoinListener;
import red.aviora.redmc.vault.registries.VaultPlaceholderRegistry;
import red.aviora.redmc.vault.utils.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultPlugin extends JavaPlugin {

	private static VaultPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private VaultManager vaultManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml", "vault_players.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		vaultManager = new VaultManager();
		vaultManager.loadAll();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

		registerPlaceholders();
	}

	private void registerPlaceholders() {
		PlaceholdersPlugin placeholdersPlugin = JavaPlugin.getPlugin(PlaceholdersPlugin.class);
		if (placeholdersPlugin != null) {
			placeholdersPlugin.getRegistryManager().addRegistry(
				VaultPlaceholderRegistry.generate(),
				10
			);
		}
	}

	public static VaultPlugin getInstance() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public LocaleManager getLocaleManager() {
		return localeManager;
	}

	public VaultManager getVaultManager() {
		return vaultManager;
	}
}
