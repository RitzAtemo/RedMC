package red.aviora.redmc.tab.listeners;

import red.aviora.redmc.tab.TabPlugin;
import red.aviora.redmc.tab.utils.TabManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		TabManager manager = JavaPlugin.getPlugin(TabPlugin.class).getTabManager();
		var player = event.getPlayer();

		player.getScheduler().runDelayed(
			JavaPlugin.getPlugin(TabPlugin.class),
			task -> manager.applyTab(player),
			null,
			2L
		);
	}
}
