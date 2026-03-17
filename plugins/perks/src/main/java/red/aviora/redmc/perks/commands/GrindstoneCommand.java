package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class GrindstoneCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksCommandHelper.checkCooldown(player, "grindstone");
		player.openInventory(Bukkit.createInventory(null, InventoryType.GRINDSTONE));
		PerksCommandHelper.applyCooldown(player, "grindstone");
		return SINGLE_SUCCESS;
	}
}
