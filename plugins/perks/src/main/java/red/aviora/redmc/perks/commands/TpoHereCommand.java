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
import red.aviora.redmc.vault.VaultPlugin;

public class TpoHereCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		String targetName = StringArgumentType.getString(ctx, "player");
		Player target = Bukkit.getPlayerExact(targetName);

		if (target == null) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "admin.tpohere.not-found"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		target.teleportAsync(player.getLocation()).thenAccept(success -> {
			if (success) {
				player.sendMessage(ApiUtils.formatText(VaultPlugin.resolvePlayer(
					ApiUtils.formatTextString(plugin.getLocaleManager().getMessage(player, "admin.tpohere.teleported"),
						"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")),
					target)));
				ApiUtils.sendCommandSenderMessageArgs(target,
					plugin.getLocaleManager().getMessage(target, "admin.tpohere.message"),
					"%prefix%", plugin.getLocaleManager().getMessage(target, "prefix")
				);
			}
		});
		return SINGLE_SUCCESS;
	}
}
