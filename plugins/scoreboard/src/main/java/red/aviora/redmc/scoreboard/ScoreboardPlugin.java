package red.aviora.redmc.scoreboard;

import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.scoreboard.listeners.PlayerJoinListener;
import red.aviora.redmc.scoreboard.listeners.PlayerQuitListener;
import red.aviora.redmc.scoreboard.utils.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardPlugin extends JavaPlugin {

	private static ScoreboardPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private ScoreboardManager scoreboardManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		scoreboardManager = new ScoreboardManager();
		scoreboardManager.loadAll();
		scoreboardManager.startAnimations();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
	}

	@Override
	public void onDisable() {
		if (scoreboardManager != null) {
			scoreboardManager.stopAnimations();
			scoreboardManager.saveAll();
		}
	}

	public static ScoreboardPlugin getInstance() { return instance; }
	public ConfigManager getConfigManager() { return configManager; }
	public LocaleManager getLocaleManager() { return localeManager; }
	public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
}
