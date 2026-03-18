package red.aviora.redmc.holograms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.aviora.redmc.holograms.HologramsPlugin;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		HologramsPlugin.getInstance().getHologramManager().refreshAllForPlayer(event.getPlayer());
	}
}
