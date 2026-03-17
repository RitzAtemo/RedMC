package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.perks.inventory.InvseeHolder;
import red.aviora.redmc.perks.util.OfflinePlayerDataUtil;

import java.util.UUID;

public class InvseeAsOfflineCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();
		String prefix = plugin.getLocaleManager().getMessage(player, "prefix");

		String targetName = StringArgumentType.getString(ctx, "player");

		UUID targetUuid;
		String displayName;

		Player onlineTarget = Bukkit.getPlayerExact(targetName);
		if (onlineTarget != null) {
			targetUuid = onlineTarget.getUniqueId();
			displayName = onlineTarget.getName();
		} else {
			OfflinePlayer offlineTarget = Bukkit.getOfflinePlayerIfCached(targetName);
			if (offlineTarget == null || !offlineTarget.hasPlayedBefore()) {
				ApiUtils.sendCommandSenderMessageArgs(player,
					plugin.getLocaleManager().getMessage(player, "admin.invsee.not-found"),
					"%prefix%", prefix
				);
				return SINGLE_SUCCESS;
			}
			targetUuid = offlineTarget.getUniqueId();
			displayName = offlineTarget.getName() != null ? offlineTarget.getName() : targetName;
		}

		ItemStack[] contents = OfflinePlayerDataUtil.loadInventory(targetUuid);
		if (contents == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.invsee.no-data"),
				"%prefix%", prefix
			);
			return SINGLE_SUCCESS;
		}

		String titleRaw = plugin.getLocaleManager().getMessage(player, "admin.invsee.title")
			.replace("%player%", displayName);

		InvseeHolder holder = new InvseeHolder(targetUuid);
		Inventory inv = Bukkit.createInventory(holder, 36, MiniMessage.miniMessage().deserialize(titleRaw));
		holder.setInventory(inv);

		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != null) inv.setItem(i, contents[i]);
		}

		player.openInventory(inv);

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "admin.invsee.opened"),
			"%prefix%", prefix,
			"%player%", displayName
		);
		return SINGLE_SUCCESS;
	}
}
