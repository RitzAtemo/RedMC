package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class FreezeCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		String targetName = StringArgumentType.getString(ctx, "player");
		Player target = Bukkit.getPlayerExact(targetName);

		if (target == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.freeze.not-found"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		boolean frozen = plugin.getFreezeManager().toggle(target.getUniqueId());

		String senderKey = frozen ? "admin.freeze.frozen" : "admin.freeze.unfrozen";
		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, senderKey),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
			"%player%", target.getName()
		);

		String targetKey = frozen ? "admin.freeze.message" : "admin.freeze.unmessage";
		ApiUtils.sendCommandSenderMessageArgs(target,
			plugin.getLocaleManager().getMessage(target, targetKey),
			"%prefix%", plugin.getLocaleManager().getMessage(target, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
