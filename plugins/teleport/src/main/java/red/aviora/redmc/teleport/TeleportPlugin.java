package red.aviora.redmc.teleport;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.listeners.PlayerDeathListener;
import red.aviora.redmc.teleport.listeners.PlayerJoinListener;
import red.aviora.redmc.teleport.listeners.PlayerQuitListener;
import red.aviora.redmc.teleport.listeners.PlayerRespawnListener;
import red.aviora.redmc.teleport.managers.BackManager;
import red.aviora.redmc.teleport.managers.HomeManager;
import red.aviora.redmc.teleport.managers.RtpManager;
import red.aviora.redmc.teleport.managers.SpawnManager;
import red.aviora.redmc.teleport.managers.TpaManager;
import red.aviora.redmc.teleport.managers.WarpManager;
import red.aviora.redmc.teleport.utils.TeleportDataStorage;

public class TeleportPlugin extends JavaPlugin {

    private static TeleportPlugin instance;

    private ConfigManager configManager;
    private LocaleManager localeManager;
    private TeleportDataStorage dataStorage;
    private SpawnManager spawnManager;
    private WarpManager warpManager;
    private HomeManager homeManager;
    private BackManager backManager;
    private RtpManager rtpManager;
    private TpaManager tpaManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, "config.yml");
        localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

        dataStorage = new TeleportDataStorage();

        spawnManager = new SpawnManager();
        spawnManager.loadAll();

        warpManager = new WarpManager();
        warpManager.loadAll();

        homeManager = new HomeManager();
        homeManager.loadAll();

        backManager = new BackManager();
        rtpManager = new RtpManager();
        tpaManager = new TpaManager();

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
    }

    @Override
    public void onDisable() {
        if (tpaManager != null) tpaManager.cancelAll();
        if (homeManager != null) homeManager.saveAll();
    }

    public static TeleportPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LocaleManager getLocaleManager() { return localeManager; }
    public TeleportDataStorage getDataStorage() { return dataStorage; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public BackManager getBackManager() { return backManager; }
    public RtpManager getRtpManager() { return rtpManager; }
    public TpaManager getTpaManager() { return tpaManager; }
}
