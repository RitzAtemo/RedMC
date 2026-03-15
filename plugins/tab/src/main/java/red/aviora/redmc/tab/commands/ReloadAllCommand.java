package red.aviora.redmc.tab.commands;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.tab.TabPlugin;
import red.aviora.redmc.tab.utils.TabManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadAllCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();

		ConfigManager configManager = JavaPlugin.getPlugin(TabPlugin.class).getConfigManager();
		LocaleManager localeManager = JavaPlugin.getPlugin(TabPlugin.class).getLocaleManager();
		TabManager tabManager = JavaPlugin.getPlugin(TabPlugin.class).getTabManager();

		tabManager.stopAnimations();
		configManager.reload();
		tabManager.reloadAll();
		tabManager.startAnimations();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "reload-all"),
			"%prefix%", localeManager.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
