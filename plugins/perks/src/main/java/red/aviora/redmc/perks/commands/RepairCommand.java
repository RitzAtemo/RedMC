package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class RepairCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "repair");

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType().isAir()) {
			ApiUtils.sendPlayerMessage(player, ApiUtils.formatTextString(
				plugin.getLocaleManager().getMessage(player, "repair.nothing"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			));
			return SINGLE_SUCCESS;
		}

		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof Damageable damageable) || damageable.getDamage() == 0) {
			ApiUtils.sendPlayerMessage(player, ApiUtils.formatTextString(
				plugin.getLocaleManager().getMessage(player, "repair.nothing"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			));
			return SINGLE_SUCCESS;
		}

		damageable.setDamage(0);
		item.setItemMeta(meta);
		PerksCommandHelper.applyCooldown(player, "repair");

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "repair.success"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
