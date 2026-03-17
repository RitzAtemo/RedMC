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

public class TpoCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		String targetName = StringArgumentType.getString(ctx, "player");
		Player target = Bukkit.getPlayerExact(targetName);

		if (target == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.tpo.not-found"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		player.teleportAsync(target.getLocation()).thenAccept(success -> {
			if (success) {
				ApiUtils.sendCommandSenderMessageArgs(player,
					plugin.getLocaleManager().getMessage(player, "admin.tpo.teleported"),
					"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
					"%player%", target.getName()
				);
			}
		});
		return SINGLE_SUCCESS;
	}
}
