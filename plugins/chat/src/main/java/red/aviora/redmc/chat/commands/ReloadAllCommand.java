package red.aviora.redmc.chat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.chat.ChatPlugin;

public class ReloadAllCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(ChatPlugin.class).getLocaleManager();
		ChatPlugin plugin = JavaPlugin.getPlugin(ChatPlugin.class);

		plugin.getConfigManager().reload();
		plugin.getChatManager().loadAll();
		plugin.getAlertManager().reload();

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "reload.all-success"),
			"%prefix%", locale.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
