package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class HealCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "heal");

		AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
		if (maxHealth != null) {
			player.setHealth(maxHealth.getValue());
		}

		ItemStack[] armor = player.getInventory().getArmorContents();
		for (ItemStack piece : armor) {
			if (piece == null || piece.getType().isAir()) continue;
			ItemMeta meta = piece.getItemMeta();
			if (meta instanceof Damageable damageable) {
				damageable.setDamage(0);
				piece.setItemMeta(meta);
			}
		}

		PerksCommandHelper.applyCooldown(player, "heal");

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "heal.success"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
