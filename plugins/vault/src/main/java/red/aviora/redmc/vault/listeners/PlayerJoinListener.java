package red.aviora.redmc.vault.listeners;

import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.utils.VaultManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		VaultManager manager = JavaPlugin.getPlugin(VaultPlugin.class).getVaultManager();
		var player = event.getPlayer();
		manager.getOrCreatePlayer(player.getUniqueId(), player.getName());
		manager.saveAll();
	}
}
