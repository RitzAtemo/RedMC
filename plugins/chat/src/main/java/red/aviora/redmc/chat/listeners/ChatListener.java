package red.aviora.redmc.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import red.aviora.redmc.chat.ChatPlugin;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncChatEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();

		if (player.hasMetadata("redmc:muted")) return;

		String message = PlainTextComponentSerializer.plainText().serialize(event.message());

		ChatPlugin plugin = ChatPlugin.getInstance();
		plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
			plugin.getChatManager().processChat(player, message)
		);
	}
}
