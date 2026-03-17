package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class PerksReloadDataCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		plugin.getDataStorage().saveAll();
		plugin.getDataStorage().loadAll();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			plugin.getLocaleManager().getMessage(sender, "perks.reload.data"),
			"%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix")
		);
		return SINGLE_SUCCESS;
	}
}
