package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.perks.inventory.InvseeHolder;
import red.aviora.redmc.perks.util.OfflinePlayerDataUtil;

public class InvseeCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();
		String prefix = plugin.getLocaleManager().getMessage(player, "prefix");

		String targetName = StringArgumentType.getString(ctx, "player");
		Player onlineTarget = Bukkit.getPlayerExact(targetName);

		if (onlineTarget != null) {
			player.openInventory(onlineTarget.getInventory());
			player.sendMessage(ApiUtils.formatText(VaultPlugin.resolvePlayer(
				ApiUtils.formatTextString(plugin.getLocaleManager().getMessage(player, "admin.invsee.opened"),
					"%prefix%", prefix),
				onlineTarget)));
			return SINGLE_SUCCESS;
		}

		OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetName);
		if (offlineTarget == null || !offlineTarget.hasPlayedBefore()) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.invsee.not-found"),
				"%prefix%", prefix
			);
			return SINGLE_SUCCESS;
		}

		ItemStack[] contents = OfflinePlayerDataUtil.loadInventory(offlineTarget.getUniqueId());
		if (contents == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.invsee.no-data"),
				"%prefix%", prefix
			);
			return SINGLE_SUCCESS;
		}

		String displayName = offlineTarget.getName() != null ? offlineTarget.getName() : targetName;
		String titleRaw = plugin.getLocaleManager().getMessage(player, "admin.invsee.title")
			.replace("%player_altname%", displayName).replace("%player_prefix%", "").replace("%player_suffix%", "");

		InvseeHolder holder = new InvseeHolder(offlineTarget.getUniqueId());
		Inventory inv = Bukkit.createInventory(holder, 36, ApiUtils.getMM().deserialize(titleRaw));
		holder.setInventory(inv);

		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null) inv.setItem(i, contents[i]);
		}

		player.openInventory(inv);

		player.sendMessage(ApiUtils.formatText(ApiUtils.formatTextString(
			plugin.getLocaleManager().getMessage(player, "admin.invsee.opened"),
			"%prefix%", prefix,
			"%player_altname%", displayName,
			"%player_prefix%", "",
			"%player_suffix%", "")));
		return SINGLE_SUCCESS;
	}
}
