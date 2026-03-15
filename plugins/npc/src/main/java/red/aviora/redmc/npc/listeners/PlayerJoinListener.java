package red.aviora.redmc.npc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.aviora.redmc.npc.NpcPlugin;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		var player = event.getPlayer();
		NpcPlugin.getInstance().getNpcManager().spawnAllForPlayer(player);
		NpcInteractionListener.inject(player);
	}
}
