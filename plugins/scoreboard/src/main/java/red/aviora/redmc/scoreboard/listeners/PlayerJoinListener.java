package red.aviora.redmc.scoreboard.listeners;

import red.aviora.redmc.scoreboard.ScoreboardPlugin;
import red.aviora.redmc.scoreboard.utils.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		ScoreboardManager manager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getScoreboardManager();
		var player = event.getPlayer();

		player.getScheduler().runDelayed(
			JavaPlugin.getPlugin(ScoreboardPlugin.class),
			task -> {
				if (manager.isVisible(player)) {
					manager.applyScoreboard(player);
				}
			},
			null,
			2L
		);
	}
}
