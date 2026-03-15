package red.aviora.redmc.chat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import red.aviora.redmc.chat.ChatPlugin;

public class SayCommandListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage();
		if (!msg.toLowerCase().startsWith("/say ") && !msg.equalsIgnoreCase("/say")) return;

		event.setCancelled(true);
		Player player = event.getPlayer();
		String text = msg.length() > 5 ? msg.substring(5).trim() : "";
		if (text.isEmpty()) return;

		ChatPlugin plugin = ChatPlugin.getInstance();
		plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
			plugin.getChatManager().processChat(player, text)
		);
	}
}
