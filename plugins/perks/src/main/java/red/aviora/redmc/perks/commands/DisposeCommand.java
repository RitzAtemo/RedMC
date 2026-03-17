package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.perks.inventory.DisposeHolder;

public class DisposeCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "dispose");

		DisposeHolder holder = new DisposeHolder();
		var inv = Bukkit.createInventory(
			holder,
			27,
			ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "dispose.title"))
		);
		player.openInventory(inv);
		PerksCommandHelper.applyCooldown(player, "dispose");
		return SINGLE_SUCCESS;
	}
}
