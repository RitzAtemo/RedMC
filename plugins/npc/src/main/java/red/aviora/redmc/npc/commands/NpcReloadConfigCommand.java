package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.npc.NpcPlugin;

public class NpcReloadConfigCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(NpcPlugin.class).getLocaleManager();

		JavaPlugin.getPlugin(NpcPlugin.class).getConfigManager().reload();

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "reload.config-success"),
			"%prefix%", locale.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
