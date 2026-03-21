package red.aviora.redmc.chat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;

public class JoinLeaveListener implements Listener {

	private static final NamespacedKey JOIN_OVERRIDE = new NamespacedKey("redmc", "join-override");
	private static final NamespacedKey QUIT_OVERRIDE = new NamespacedKey("redmc", "quit-override");

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		ChatPlugin plugin = ChatPlugin.getInstance();
		Player player = event.getPlayer();
		event.joinMessage(null);

		if (player.getPersistentDataContainer().has(JOIN_OVERRIDE)) {
			player.getPersistentDataContainer().remove(JOIN_OVERRIDE);
			return;
		}

		String playerName = player.getName();

		boolean joinEnabled = plugin.getConfigManager().getBoolean("config.yml", "join-leave.join.enabled", true);
		if (joinEnabled) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				String format = plugin.getLocaleManager().getMessage(online, "join-leave.join");
				online.sendMessage(ApiUtils.formatText(format.replace("%player%", playerName)));
			}
		}

		boolean newbieEnabled = plugin.getConfigManager().getBoolean("config.yml", "join-leave.newbie.enabled", true);
		if (newbieEnabled && !player.hasPlayedBefore()) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				String format = plugin.getLocaleManager().getMessage(online, "join-leave.newbie");
				online.sendMessage(ApiUtils.formatText(format.replace("%player%", playerName)));
			}
		}

		boolean isNewbie = !player.hasPlayedBefore();
		boolean welcomeEnabled = plugin.getConfigManager().getBoolean("config.yml", "welcome.enabled", true);
		boolean welcomeNewbieEnabled = plugin.getConfigManager().getBoolean("config.yml", "welcome.newbie.enabled", true);
		boolean newbieFirst = plugin.getConfigManager().getBoolean("config.yml", "welcome.newbie.priority-first", false);

		String welcomeMsg = plugin.getLocaleManager().getMessage(player, "welcome.returning");
		String welcomeNewbieMsg = plugin.getLocaleManager().getMessage(player, "welcome.newbie");

		if (isNewbie && welcomeNewbieEnabled && newbieFirst) {
			player.sendMessage(ApiUtils.formatText(welcomeNewbieMsg.replace("%player%", playerName)));
		}
		if (welcomeEnabled) {
			player.sendMessage(ApiUtils.formatText(welcomeMsg.replace("%player%", playerName)));
		}
		if (isNewbie && welcomeNewbieEnabled && !newbieFirst) {
			player.sendMessage(ApiUtils.formatText(welcomeNewbieMsg.replace("%player%", playerName)));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event) {
		ChatPlugin plugin = ChatPlugin.getInstance();
		Player quitter = event.getPlayer();
		event.quitMessage(null);

		if (quitter.getPersistentDataContainer().has(QUIT_OVERRIDE)) {
			plugin.getSessionManager().clearSession(quitter.getUniqueId());
			return;
		}

		boolean enabled = plugin.getConfigManager().getBoolean("config.yml", "join-leave.leave.enabled", true);
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
