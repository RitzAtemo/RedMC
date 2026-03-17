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

public class SudoCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		String targetName = StringArgumentType.getString(ctx, "player");
		String command = StringArgumentType.getString(ctx, "command");

		Player target = Bukkit.getPlayerExact(targetName);
		if (target == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.sudo.not-found"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		plugin.getServer().getRegionScheduler().run(plugin, target.getLocation(), task -> {
			target.performCommand(command);
		});

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "admin.sudo.executed"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
			"%player%", target.getName(),
			"%command%", command
		);
		return SINGLE_SUCCESS;
	}
}
