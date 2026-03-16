package red.aviora.redmc.chat.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.chat.ChatPlugin;

public class DeathListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		if (!ChatPlugin.getInstance().getChatManager().isDeathEnabled()) return;

		event.deathMessage(null);

		Player victim = event.getEntity();
		Player killer = victim.getKiller();
		Entity lastDamager = null;

		if (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent entityEvent) {
			lastDamager = entityEvent.getDamager();
		}

		ItemStack weapon = null;
		if (killer != null) {
			ItemStack held = killer.getInventory().getItemInMainHand();
			if (!held.getType().isAir()) {
				weapon = held;
			}
		}

		String group = determineGroup(victim, killer, lastDamager, weapon);
		ChatPlugin.getInstance().getChatManager().broadcastDeathMessage(victim, killer, lastDamager, group, weapon);
	}

	private String determineGroup(Player victim, Player killer, Entity lastDamager, ItemStack weapon) {
		if (killer != null) return weapon != null ? "by_player_weapon" : "by_player";
		if (victim.getLastDamageCause() == null) return "default";

		EntityDamageEvent.DamageCause cause = victim.getLastDamageCause().getCause();
		return switch (cause) {
			case FALL -> "by_fall";
			case FIRE, FIRE_TICK, HOT_FLOOR -> "by_fire";
			case LAVA -> "by_lava";
			case DROWNING -> "by_drowning";
			case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "by_explosion";
			case VOID -> "by_void";
			case MAGIC -> "by_magic";
			case WITHER -> "by_wither";
			case STARVATION -> "by_starve";
			case LIGHTNING -> "by_lightning";
			case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> lastDamager != null ? "by_entity" : "default";
			default -> "default";
		};
	}
}
