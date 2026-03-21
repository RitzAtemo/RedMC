package red.aviora.redmc.perks.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.perks.storage.PlayerData;

public class PerksPlayerListener implements Listener {

	private static final NamespacedKey JOIN_OVERRIDE = new NamespacedKey("redmc", "join-override");
	private static final NamespacedKey QUIT_OVERRIDE = new NamespacedKey("redmc", "quit-override");

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PerksPlugin plugin = PerksPlugin.getInstance();

		for (Player online : player.getServer().getOnlinePlayers()) {
			if (plugin.getVanishManager().isVanished(online.getUniqueId()) && !online.equals(player)) {
				player.hideEntity(plugin, online);
			}
		}

		if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
			event.joinMessage(null);
			return;
		}

		PlayerData data = plugin.getDataStorage().getPlayerData(player.getUniqueId());
		String customJoin = data.getJoinMessage();
		if (customJoin != null && !customJoin.isBlank() && player.hasPermission("redmc.perks.setjoin")) {
			String resolvedJoin = VaultPlugin.resolvePlayer(customJoin, player);
			for (Player online : player.getServer().getOnlinePlayers()) {
				online.sendMessage(ApiUtils.formatText(resolvedJoin));
			}
			event.joinMessage(null);
			player.getPersistentDataContainer().set(JOIN_OVERRIDE, PersistentDataType.BOOLEAN, true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
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
				String resolvedQuit = VaultPlugin.resolvePlayer(customQuit, player);
				for (Player online : player.getServer().getOnlinePlayers()) {
					if (online.equals(player)) continue;
					online.sendMessage(ApiUtils.formatText(resolvedQuit));
				}
				event.quitMessage(null);
				player.getPersistentDataContainer().set(QUIT_OVERRIDE, PersistentDataType.BOOLEAN, true);
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
