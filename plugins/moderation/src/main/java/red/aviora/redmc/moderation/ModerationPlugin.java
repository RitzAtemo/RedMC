package red.aviora.redmc.moderation;

import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.listeners.InventoryClickListener;
import red.aviora.redmc.moderation.listeners.ModerationPlayerJoinListener;
import red.aviora.redmc.moderation.listeners.MutedCommandListener;
import red.aviora.redmc.moderation.listeners.PlayerChatListener;
import red.aviora.redmc.moderation.listeners.PlayerLoginListener;
import red.aviora.redmc.moderation.managers.BanManager;
import red.aviora.redmc.moderation.managers.MuteManager;
import red.aviora.redmc.moderation.managers.WarnManager;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.utils.ModerationDataStorage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModerationPlugin extends JavaPlugin {

    private static ModerationPlugin instance;

    private ConfigManager configManager;
    private LocaleManager localeManager;
    private ModerationDataStorage dataStorage;
    private WarnManager warnManager;
    private MuteManager muteManager;
    private BanManager banManager;
    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, "config.yml", "actions.yml", "lang/en_US.yml", "lang/ru_RU.yml");
        localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

        dataStorage = new ModerationDataStorage(configManager);

        Map<UUID, List<ModerationAction>> actionsMap = dataStorage.loadActions();

        warnManager = new WarnManager(dataStorage);
        warnManager.setActionsMap(actionsMap);

        muteManager = new MuteManager(dataStorage, actionsMap);
        muteManager.load();

        banManager = new BanManager(dataStorage, actionsMap);
        banManager.load();

        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new ModerationPlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MutedCommandListener(), this);

        // Restore mute metadata for already-online players (e.g. after reload)
        for (var player : getServer().getOnlinePlayers()) {
            if (muteManager.isMuted(player.getUniqueId())) {
                muteManager.applyMeta(player.getUniqueId());
            }
        }
    }

    public void reloadData() {
        Map<UUID, List<ModerationAction>> actionsMap = dataStorage.loadActions();
        warnManager.setActionsMap(actionsMap);
        muteManager.setActionsMap(actionsMap);
        banManager.setActionsMap(actionsMap);
    }

    public static ModerationPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LocaleManager getLocaleManager() { return localeManager; }
    public ModerationDataStorage getDataStorage() { return dataStorage; }
    public WarnManager getWarnManager() { return warnManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public BanManager getBanManager() { return banManager; }
}
