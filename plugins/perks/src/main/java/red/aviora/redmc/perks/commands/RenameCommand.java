package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class RenameCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "rename");

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType().isAir()) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "rename.empty"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		String name = StringArgumentType.getString(ctx, "name");
		ItemMeta meta = item.getItemMeta();
		meta.displayName(ApiUtils.formatText(name));
		item.setItemMeta(meta);
		PerksCommandHelper.applyCooldown(player, "rename");

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "rename.success"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
