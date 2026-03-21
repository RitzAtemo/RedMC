package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.vault.VaultPlugin;

public class BroadcastCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "broadcast");

		String message = StringArgumentType.getString(ctx, "message");
		String format = plugin.getLocaleManager().getMessage(player, "broadcast.format");
		ApiUtils.broadcastMessage(VaultPlugin.resolvePlayer(
			ApiUtils.formatTextString(format, "%message%", message), player));
		PerksCommandHelper.applyCooldown(player, "broadcast");

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "broadcast.sent"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
