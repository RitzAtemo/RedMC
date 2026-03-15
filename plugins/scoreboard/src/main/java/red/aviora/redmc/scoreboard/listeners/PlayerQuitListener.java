package red.aviora.redmc.scoreboard.listeners;

import red.aviora.redmc.scoreboard.ScoreboardPlugin;
import red.aviora.redmc.scoreboard.utils.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		ScoreboardManager manager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getScoreboardManager();
		manager.removeScoreboard(event.getPlayer());
	}
}
