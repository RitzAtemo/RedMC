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
import red.aviora.redmc.perks.inventory.EcSeeHolder;
import red.aviora.redmc.perks.util.OfflinePlayerDataUtil;

public class EcSeeCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();
		String prefix = plugin.getLocaleManager().getMessage(player, "prefix");

		String targetName = StringArgumentType.getString(ctx, "player");
		Player onlineTarget = Bukkit.getPlayerExact(targetName);

		if (onlineTarget != null) {
			player.openInventory(onlineTarget.getEnderChest());
			player.sendMessage(ApiUtils.formatText(VaultPlugin.resolvePlayer(
				ApiUtils.formatTextString(plugin.getLocaleManager().getMessage(player, "admin.ecsee.opened"),
					"%prefix%", prefix),
				onlineTarget)));
			return SINGLE_SUCCESS;
		}

		OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetName);
		if (offlineTarget == null || !offlineTarget.hasPlayedBefore()) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.ecsee.not-found"),
				"%prefix%", prefix
			);
			return SINGLE_SUCCESS;
		}

		ItemStack[] contents = OfflinePlayerDataUtil.loadEnderChest(offlineTarget.getUniqueId());
		if (contents == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.ecsee.no-data"),
				"%prefix%", prefix
			);
			return SINGLE_SUCCESS;
		}

		String displayName = offlineTarget.getName() != null ? offlineTarget.getName() : targetName;
		String titleRaw = plugin.getLocaleManager().getMessage(player, "admin.ecsee.title")
			.replace("%player_altname%", displayName).replace("%player_prefix%", "").replace("%player_suffix%", "");

		EcSeeHolder holder = new EcSeeHolder(offlineTarget.getUniqueId());
		Inventory inv = Bukkit.createInventory(holder, 27, ApiUtils.getMM().deserialize(titleRaw));
		holder.setInventory(inv);
		inv.setContents(contents);
		player.openInventory(inv);

		player.sendMessage(ApiUtils.formatText(ApiUtils.formatTextString(
			plugin.getLocaleManager().getMessage(player, "admin.ecsee.opened"),
			"%prefix%", prefix,
			"%player_altname%", displayName,
			"%player_prefix%", "",
			"%player_suffix%", "")));
		return SINGLE_SUCCESS;
	}
}
