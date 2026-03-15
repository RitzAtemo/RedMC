package red.aviora.redmc.chat.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AlertManager {

	private boolean enabled;
	private long interval;
	private int noRepeatCount;
	private List<String> messages = new ArrayList<>();
	private final LinkedList<Integer> recentIndices = new LinkedList<>();
	private final Random random = new Random();
	private Object task;

	public void loadAll() {
		var cfg = ChatPlugin.getInstance().getConfigManager();
		enabled = cfg.getBoolean("config.yml", "alerts.enabled", true);
		interval = cfg.getInt("config.yml", "alerts.interval", 300);
		noRepeatCount = cfg.getInt("config.yml", "alerts.no-repeat-count", 3);
		List<?> raw = cfg.getList("config.yml", "alerts.messages", List.of());
		messages = raw.stream().map(Object::toString).toList();
	}

	public void start() {
		if (!enabled || messages.isEmpty()) return;
		ChatPlugin plugin = ChatPlugin.getInstance();
		task = Bukkit.getAsyncScheduler().runAtFixedRate(
			plugin,
			t -> broadcastNext(),
			interval,
			interval,
			TimeUnit.SECONDS
		);
	}

	public void stop() {
		if (task != null) {
			try {
				task.getClass().getMethod("cancel").invoke(task);
			} catch (Exception ignored) {
			}
			task = null;
		}
	}

	public void reload() {
		stop();
		recentIndices.clear();
		loadAll();
		start();
	}

	private void broadcastNext() {
		if (messages.isEmpty()) return;

		List<Integer> available = new ArrayList<>();
		for (int i = 0; i < messages.size(); i++) {
			if (!recentIndices.contains(i)) available.add(i);
		}
		if (available.isEmpty()) {
			recentIndices.clear();
			for (int i = 0; i < messages.size(); i++) available.add(i);
		}

		int idx = available.get(random.nextInt(available.size()));
		recentIndices.addLast(idx);
		while (recentIndices.size() > noRepeatCount) recentIndices.removeFirst();

		Component component = ApiUtils.formatText(messages.get(idx));
		for (var player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(component);
		}
	}
}
