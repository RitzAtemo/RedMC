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
import red.aviora.redmc.perks.inventory.EcSeeHolder;
import red.aviora.redmc.perks.util.OfflinePlayerDataUtil;

import java.util.UUID;

public class EcSeeAsOfflineCommand implements Command<CommandSourceStack> {

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
					plugin.getLocaleManager().getMessage(player, "admin.ecsee.not-found"),
					"%prefix%", prefix
				);
				return SINGLE_SUCCESS;
			}
			targetUuid = offlineTarget.getUniqueId();
			displayName = offlineTarget.getName() != null ? offlineTarget.getName() : targetName;
		}

		ItemStack[] contents = OfflinePlayerDataUtil.loadEnderChest(targetUuid);
		if (contents == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.ecsee.no-data"),
				"%prefix%", prefix
			);
			return SINGLE_SUCCESS;
		}

		String titleRaw = plugin.getLocaleManager().getMessage(player, "admin.ecsee.title")
			.replace("%player%", displayName);

		EcSeeHolder holder = new EcSeeHolder(targetUuid);
		Inventory inv = Bukkit.createInventory(holder, 27, MiniMessage.miniMessage().deserialize(titleRaw));
		holder.setInventory(inv);
		inv.setContents(contents);
		player.openInventory(inv);

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "admin.ecsee.opened"),
			"%prefix%", prefix,
			"%player%", displayName
		);
		return SINGLE_SUCCESS;
	}
}
