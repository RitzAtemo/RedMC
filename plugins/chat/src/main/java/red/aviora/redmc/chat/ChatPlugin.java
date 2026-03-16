package red.aviora.redmc.chat;

import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.chat.listeners.AdvancementListener;
import red.aviora.redmc.chat.listeners.ChatListener;
import red.aviora.redmc.chat.listeners.DeathListener;
import red.aviora.redmc.chat.listeners.JoinLeaveListener;
import red.aviora.redmc.chat.managers.AlertManager;
import red.aviora.redmc.chat.managers.ChatManager;
import red.aviora.redmc.chat.managers.SessionManager;

public class ChatPlugin extends JavaPlugin {

	private static ChatPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private ChatManager chatManager;
	private AlertManager alertManager;
	private SessionManager sessionManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		sessionManager = new SessionManager();

		chatManager = new ChatManager();
		chatManager.loadAll();

		alertManager = new AlertManager();
		alertManager.loadAll();
		alertManager.start();

		if (configManager.getBoolean("config.yml", "advancement.disable-vanilla", true)) {
			getServer().getGlobalRegionScheduler().run(this, task -> {
				for (var world : getServer().getWorlds()) {
					world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
				}
			});
		}

		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new DeathListener(), this);
		getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
		getServer().getPluginManager().registerEvents(new AdvancementListener(), this);
	}

	@Override
	public void onDisable() {
		if (alertManager != null) {
			alertManager.stop();
		}
	}

	public static ChatPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public ChatManager getChatManager() { return chatManager; }
	public AlertManager getAlertManager() { return alertManager; }
	public SessionManager getSessionManager() { return sessionManager; }
}
