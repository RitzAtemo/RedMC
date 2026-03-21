package red.aviora.redmc.playtime;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;
import red.aviora.redmc.playtime.listeners.PlayerActivityListener;
import red.aviora.redmc.playtime.listeners.PlayerJoinListener;
import red.aviora.redmc.playtime.listeners.PlayerQuitListener;
import red.aviora.redmc.playtime.managers.AfkManager;
import red.aviora.redmc.playtime.managers.PlaytimeManager;
import red.aviora.redmc.playtime.registries.PlaytimePlaceholderRegistry;
import red.aviora.redmc.playtime.utils.PlaytimeDataStorage;

public class PlaytimePlugin extends JavaPlugin {

    private static PlaytimePlugin instance;

    private ConfigManager configManager;
    private LocaleManager localeManager;
    private PlaytimeManager playtimeManager;
    private AfkManager afkManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this,
                "config.yml",
                "playtime.yml",
                "lang/en_US.yml",
                "lang/ru_RU.yml"
        );
        localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

        PlaytimeDataStorage storage = new PlaytimeDataStorage(configManager);
        playtimeManager = new PlaytimeManager(storage, configManager);
        afkManager = new AfkManager(configManager, localeManager, playtimeManager);

        // Register online players (e.g. after reload)
        for (var player : getServer().getOnlinePlayers()) {
            playtimeManager.onJoin(player);
            afkManager.onJoin(player);
        }

        playtimeManager.startAutoSave();
        afkManager.startAfkCheckTask();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerActivityListener(), this);

        registerPlaceholders();
    }

    @Override
    public void onDisable() {
        if (playtimeManager != null) {
            for (var player : getServer().getOnlinePlayers()) {
                afkManager.onQuit(player);
                playtimeManager.onQuit(player);
            }
        }
    }

    private void registerPlaceholders() {
        PlaceholdersPlugin placeholders = JavaPlugin.getPlugin(PlaceholdersPlugin.class);
        if (placeholders != null) {
            placeholders.getRegistryManager().addRegistry(PlaytimePlaceholderRegistry.generate(), 5);
        }
    }

    public static PlaytimePlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LocaleManager getLocaleManager() { return localeManager; }
    public PlaytimeManager getPlaytimeManager() { return playtimeManager; }
    public AfkManager getAfkManager() { return afkManager; }
}
