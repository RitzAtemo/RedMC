package red.aviora.redmc.tracker;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.tracker.listeners.PlayerJoinListener;
import red.aviora.redmc.tracker.listeners.PlayerQuitListener;
import red.aviora.redmc.tracker.managers.TrackerManager;
import red.aviora.redmc.tracker.utils.TrackerDataStorage;

public class TrackerPlugin extends JavaPlugin {

    private static TrackerPlugin instance;

    private ConfigManager configManager;
    private LocaleManager localeManager;
    private TrackerManager trackerManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this,
                "config.yml",
                "tracker.yml",
                "lang/en_US.yml",
                "lang/ru_RU.yml"
        );
        localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

        TrackerDataStorage storage = new TrackerDataStorage(configManager);
        trackerManager = new TrackerManager(storage, configManager, localeManager);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    @Override
    public void onDisable() {
        if (trackerManager != null) {
            for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                trackerManager.savePosition(player);
            }
            trackerManager.stopAllSessions();
        }
    }

    public static TrackerPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LocaleManager getLocaleManager() { return localeManager; }
    public TrackerManager getTrackerManager() { return trackerManager; }
}
