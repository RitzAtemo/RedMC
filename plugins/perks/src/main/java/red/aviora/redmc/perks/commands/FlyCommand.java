package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class FlyCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "fly");

		boolean enabled = plugin.getFlyManager().toggle(player);
		PerksCommandHelper.applyCooldown(player, "fly");

		String key = enabled ? "fly.enabled" : "fly.disabled";
		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, key),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
