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

public class RepairAllCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "repair-all");

		int count = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null || item.getType().isAir()) continue;
			ItemMeta meta = item.getItemMeta();
			if (meta instanceof Damageable damageable && damageable.getDamage() > 0) {
				damageable.setDamage(0);
				item.setItemMeta(meta);
				count++;
			}
		}

		if (count == 0) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "repair.all.nothing"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		PerksCommandHelper.applyCooldown(player, "repair-all");

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "repair.all.success"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
			"%count%", String.valueOf(count)
		);
		return SINGLE_SUCCESS;
	}
}
