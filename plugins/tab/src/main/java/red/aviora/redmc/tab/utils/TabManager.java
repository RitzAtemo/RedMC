package red.aviora.redmc.tab.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.tab.TabPlugin;
import red.aviora.redmc.tab.models.TabAnimation;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TabManager {

	private TabAnimation headerAnimation;
	private TabAnimation footerAnimation;
	private String playerRowFormat;
	private int playerRowInterval;

	private Object animationTask;
	private long lastPlayerRowUpdate = 0;

	public TabManager() {
	}

	public void loadAll() {
		loadAnimationsFromConfig();
	}

	public void reloadAll() {
		loadAll();
		applyTabToAll();
	}

	private void loadAnimationsFromConfig() {
		ConfigManager cfg = TabPlugin.getInstance().getConfigManager();

		boolean headerAnim = cfg.getBoolean("config.yml", "header.animation", false);
		int headerInterval = cfg.getInt("config.yml", "header.interval", 20);
		List<?> headerFramesRaw = cfg.getList("config.yml", "header.frames", List.of("<gray>Server Tab"));
		List<String> headerFrames = headerFramesRaw.stream().map(Object::toString).toList();
		headerAnimation = new TabAnimation(headerFrames, headerInterval, headerAnim);

		boolean footerAnim = cfg.getBoolean("config.yml", "footer.animation", false);
		int footerInterval = cfg.getInt("config.yml", "footer.interval", 20);
		List<?> footerFramesRaw = cfg.getList("config.yml", "footer.frames", List.of(""));
		List<String> footerFrames = footerFramesRaw.stream().map(Object::toString).toList();
		footerAnimation = new TabAnimation(footerFrames, footerInterval, footerAnim);

		playerRowFormat = cfg.getString("config.yml", "player-row.format", "<white>##PlayerName##");
		playerRowInterval = cfg.getInt("config.yml", "player-row.interval", 20);
	}

	public void startAnimations() {
		TabPlugin plugin = TabPlugin.getInstance();
		lastPlayerRowUpdate = System.currentTimeMillis();

		animationTask = Bukkit.getAsyncScheduler().runAtFixedRate(
			plugin,
			task -> {
				headerAnimation.tick();
				footerAnimation.tick();
				updateHeaderFooterForAll();

				long now = System.currentTimeMillis();
				if (now - lastPlayerRowUpdate >= playerRowInterval) {
					updatePlayerRowsForAll();
					lastPlayerRowUpdate = now;
				}
			},
			0L,
			50L,
			TimeUnit.MILLISECONDS
		);
	}

	public void stopAnimations() {
		if (animationTask != null) {
			try {
				animationTask.getClass().getMethod("cancel").invoke(animationTask);
			} catch (Exception ignored) {
			}
			animationTask = null;
		}
	}

	private void updateHeaderFooterForAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendHeaderFooter(player);
		}
	}

	private void updatePlayerRowsForAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			updatePlayerRow(player);
		}
	}

	private void applyTabToAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			applyTab(player);
		}
	}

	public void applyTab(Player player) {
		sendHeaderFooter(player);
		updatePlayerRow(player);
	}

	private void sendHeaderFooter(Player player) {
		String headerText = resolvePlaceholders(headerAnimation.getCurrentFrame(), player);
		String footerText = resolvePlaceholders(footerAnimation.getCurrentFrame(), player);

		Component header = ApiUtils.formatText(headerText);
		Component footer = ApiUtils.formatText(footerText);

		player.sendPlayerListHeaderAndFooter(header, footer);
	}

	public void updatePlayerRow(Player player) {
		String format = playerRowFormat;

		String resolved = format.replace("##PlayerName##", player.getName());
		resolved = resolvePlaceholders(resolved, player);

		player.playerListName(ApiUtils.formatText(resolved));
	}

	private String resolvePlaceholders(String text, Player player) {
		try {
			var placeholdersPlugin = JavaPlugin.getPlugin(
				red.aviora.redmc.placeholders.PlaceholdersPlugin.class
			);
			if (placeholdersPlugin != null) {
				return placeholdersPlugin.getPlaceholderResolver().parseString(text, player);
			}
		} catch (Exception e) {
		}
		return text;
	}

	public TabAnimation getHeaderAnimation() { return headerAnimation; }
	public TabAnimation getFooterAnimation() { return footerAnimation; }
	public String getPlayerRowFormat() { return playerRowFormat; }
}
