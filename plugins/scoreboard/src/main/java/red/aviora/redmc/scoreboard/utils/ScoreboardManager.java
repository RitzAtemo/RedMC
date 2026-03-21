package red.aviora.redmc.scoreboard.utils;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.scoreboard.ScoreboardPlugin;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.scoreboard.models.ScoreboardAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager {

	private ScoreboardAnimation titleAnimation;
	private List<String> lines;

	private final Map<UUID, FastBoard> playerBoards = new ConcurrentHashMap<>();
	private final Map<UUID, Boolean> playerVisibility = new ConcurrentHashMap<>();

	private final ScoreboardDataStorage storage;

	private Object animationTask;

	public ScoreboardManager() {
		this.storage = new ScoreboardDataStorage();
	}

	public void loadAll() {
		loadFromConfig();
		playerVisibility.putAll(storage.loadVisibility());
	}

	public void saveAll() {
		storage.saveVisibility(playerVisibility);
	}

	public void reloadAll() {
		loadAll();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (isVisible(player)) {
				updateBoard(player);
			}
		}
	}

	private void loadFromConfig() {
		ConfigManager cfg = ScoreboardPlugin.getInstance().getConfigManager();

		boolean titleAnim = cfg.getBoolean("config.yml", "title.animation", false);
		int titleInterval = cfg.getInt("config.yml", "title.interval", 20);
		List<?> titleFramesRaw = cfg.getList("config.yml", "title.frames", List.of("<gray>Scoreboard"));
		List<String> titleFrames = titleFramesRaw.stream().map(Object::toString).toList();
		titleAnimation = new ScoreboardAnimation(titleFrames, titleInterval, titleAnim);

		List<?> linesRaw = cfg.getList("config.yml", "lines", List.of());
		lines = new ArrayList<>();
		for (Object entry : linesRaw) {
			if (entry instanceof Map<?, ?> map) {
				Object text = map.get("text");
				lines.add(text != null ? text.toString() : "");
			} else {
				lines.add(entry.toString());
			}
		}
	}

	public void startAnimations() {
		ScoreboardPlugin plugin = ScoreboardPlugin.getInstance();

		animationTask = Bukkit.getAsyncScheduler().runAtFixedRate(
			plugin,
			task -> {
				titleAnimation.tick();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (!isVisible(player)) continue;
					updateBoard(player);
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

	public void applyScoreboard(Player player) {
		FastBoard board = playerBoards.computeIfAbsent(player.getUniqueId(), k -> new FastBoard(player));
		updateBoard(player, board);
	}

	public void removeScoreboard(Player player) {
		FastBoard board = playerBoards.remove(player.getUniqueId());
		if (board != null && !board.isDeleted()) {
			board.delete();
		}
	}

	public boolean toggleVisibility(Player player) {
		boolean next = !isVisible(player);
		playerVisibility.put(player.getUniqueId(), next);
		storage.saveVisibility(playerVisibility);

		if (next) {
			applyScoreboard(player);
		} else {
			removeScoreboard(player);
		}

		return next;
	}

	public boolean isVisible(Player player) {
		return playerVisibility.getOrDefault(player.getUniqueId(), true);
	}

	private void updateBoard(Player player) {
		FastBoard board = playerBoards.get(player.getUniqueId());
		if (board == null || board.isDeleted()) return;
		updateBoard(player, board);
	}

	private void updateBoard(Player player, FastBoard board) {
		board.updateTitle(format(resolvePlaceholders(titleAnimation.getCurrentFrame(), player)));

		List<Component> components = new ArrayList<>();
		for (String line : lines) {
			components.add(format(resolvePlaceholders(line, player)));
		}
		board.updateLines(components);
	}

	private Component format(String text) {
		return ApiUtils.formatText(text);
	}

	private String resolvePlaceholders(String text, Player player) {
		text = VaultPlugin.resolvePlayer(text, player);
		try {
			var placeholdersPlugin = JavaPlugin.getPlugin(
				red.aviora.redmc.placeholders.PlaceholdersPlugin.class
			);
			if (placeholdersPlugin != null) {
				return placeholdersPlugin.getPlaceholderResolver().parseString(text, player);
			}
		} catch (Exception ignored) {
		}
		return text;
	}

	public ScoreboardAnimation getTitleAnimation() { return titleAnimation; }
	public List<String> getLines() { return lines; }
}
