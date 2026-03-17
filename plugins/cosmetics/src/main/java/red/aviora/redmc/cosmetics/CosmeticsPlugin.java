package red.aviora.redmc.cosmetics;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.cosmetics.listener.PlayerConnectionListener;
import red.aviora.redmc.cosmetics.listener.PlayerMoveListener;
import red.aviora.redmc.cosmetics.manager.PlayerCosmeticsManager;
import red.aviora.redmc.cosmetics.manager.TemplateManager;
import red.aviora.redmc.cosmetics.renderer.CosmeticRenderer;
import red.aviora.redmc.cosmetics.renderer.TrailTracker;
import red.aviora.redmc.cosmetics.storage.PlayerCosmeticsStorage;
import red.aviora.redmc.cosmetics.storage.TemplateStorage;

public class CosmeticsPlugin extends JavaPlugin {

    private static CosmeticsPlugin instance;

    private ConfigManager configManager;
    private LocaleManager localeManager;
    private TemplateStorage templateStorage;
    private TemplateManager templateManager;
    private PlayerCosmeticsStorage playerCosmeticsStorage;
    private PlayerCosmeticsManager playerCosmeticsManager;
    private CosmeticRenderer cosmeticRenderer;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, "config.yml");
        localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

        templateStorage = new TemplateStorage();
        templateManager = new TemplateManager(templateStorage);
        templateManager.loadAll();

        playerCosmeticsStorage = new PlayerCosmeticsStorage();
        playerCosmeticsManager = new PlayerCosmeticsManager(playerCosmeticsStorage);

        int historySize = configManager.getInt("config.yml", "renderer.trail-history-size", 12);
        double minDistance = configManager.getDouble("config.yml", "renderer.trail-min-distance", 0.25);
        TrailTracker trailTracker = new TrailTracker(historySize, minDistance);

        cosmeticRenderer = new CosmeticRenderer(trailTracker);
        cosmeticRenderer.start();

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);

        getServer().getOnlinePlayers().forEach(playerCosmeticsManager::onJoin);
    }

    @Override
    public void onDisable() {
        if (cosmeticRenderer != null) cosmeticRenderer.stop();
        if (playerCosmeticsManager != null) playerCosmeticsManager.saveAll();
    }

    public static CosmeticsPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LocaleManager getLocaleManager() { return localeManager; }
    public TemplateManager getTemplateManager() { return templateManager; }
    public PlayerCosmeticsManager getPlayerCosmeticsManager() { return playerCosmeticsManager; }
    public CosmeticRenderer getCosmeticRenderer() { return cosmeticRenderer; }
}
