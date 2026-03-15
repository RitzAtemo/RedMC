package red.aviora.redmc.chat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;

public class JoinLeaveListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ChatPlugin plugin = ChatPlugin.getInstance();
		boolean enabled = plugin.getConfigManager().getBoolean("config.yml", "join-leave.join.enabled", true);
		event.joinMessage(null);
		if (!enabled) return;

		String playerName = event.getPlayer().getName();
		for (Player online : Bukkit.getOnlinePlayers()) {
			String format = plugin.getLocaleManager().getMessage(online, "join-leave.join");
			online.sendMessage(ApiUtils.formatText(format.replace("%player%", playerName)));
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		ChatPlugin plugin = ChatPlugin.getInstance();
		Player quitter = event.getPlayer();
		boolean enabled = plugin.getConfigManager().getBoolean("config.yml", "join-leave.leave.enabled", true);
		event.quitMessage(null);

		if (enabled) {
			String playerName = quitter.getName();
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online.equals(quitter)) continue;
				String format = plugin.getLocaleManager().getMessage(online, "join-leave.leave");
				online.sendMessage(ApiUtils.formatText(format.replace("%player%", playerName)));
			}
		}

		plugin.getSessionManager().clearSession(quitter.getUniqueId());
	}
}
