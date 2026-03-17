package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class StonecutterCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksCommandHelper.checkCooldown(player, "stonecutter");
		player.openInventory(Bukkit.createInventory(null, InventoryType.STONECUTTER));
		PerksCommandHelper.applyCooldown(player, "stonecutter");
		return SINGLE_SUCCESS;
	}
}
