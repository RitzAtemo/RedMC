package red.aviora.redmc.perks.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.perks.storage.PlayerData;

public class PerksPlayerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PerksPlugin plugin = PerksPlugin.getInstance();

		for (Player online : player.getServer().getOnlinePlayers()) {
			if (plugin.getVanishManager().isVanished(online.getUniqueId()) && !online.equals(player)) {
				player.hidePlayer(plugin, online);
			}
		}

		if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
			event.joinMessage(null);
			return;
		}

		PlayerData data = plugin.getDataStorage().getPlayerData(player.getUniqueId());
		String customJoin = data.getJoinMessage();
		if (customJoin != null && !customJoin.isBlank() && player.hasPermission("redmc.perks.setjoin")) {
			event.joinMessage(ApiUtils.formatText(customJoin, "%player%", player.getName()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PerksPlugin plugin = PerksPlugin.getInstance();

		if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
			event.quitMessage(null);
			plugin.getVanishManager().onPlayerQuit(player.getUniqueId());
		} else {
			PlayerData data = plugin.getDataStorage().getPlayerData(player.getUniqueId());
			String customQuit = data.getQuitMessage();
			if (customQuit != null && !customQuit.isBlank() && player.hasPermission("redmc.perks.setquit")) {
				event.quitMessage(ApiUtils.formatText(customQuit, "%player%", player.getName()));
			}
		}

		plugin.getFlyManager().onPlayerQuit(player);
		plugin.getNoFallManager().onPlayerQuit(player.getUniqueId());
		plugin.getFreezeManager().onPlayerQuit(player.getUniqueId());
		plugin.getGodManager().onPlayerQuit(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		PerksPlugin plugin = PerksPlugin.getInstance();

		if (plugin.getGodManager().hasGod(player.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		if (event.getCause() == EntityDamageEvent.DamageCause.FALL
			&& plugin.getNoFallManager().hasNoFall(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMove(PlayerMoveEvent event) {
		PerksPlugin plugin = PerksPlugin.getInstance();
		if (plugin.getFreezeManager().isEmpty()) return;

		Player player = event.getPlayer();
		if (!plugin.getFreezeManager().isFrozen(player.getUniqueId())) return;

		var from = event.getFrom();
		var to = event.getTo();
		if (to == null) return;

		if (from.getBlockX() != to.getBlockX()
			|| from.getBlockY() != to.getBlockY()
			|| from.getBlockZ() != to.getBlockZ()) {
			event.setCancelled(true);
		}
	}
}
