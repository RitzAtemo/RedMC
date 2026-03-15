package red.aviora.redmc.npc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.npc.NpcPlugin;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		var player = event.getPlayer();
		NpcInteractionListener.remove(player);
		NpcPlugin.getInstance().getNpcManager().clearPlayerCooldowns(player);
	}
}
