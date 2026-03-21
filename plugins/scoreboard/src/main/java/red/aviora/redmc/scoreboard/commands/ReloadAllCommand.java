package red.aviora.redmc.scoreboard.commands;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.scoreboard.ScoreboardPlugin;
import red.aviora.redmc.scoreboard.utils.ScoreboardManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadAllCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();

		ConfigManager configManager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getConfigManager();
		LocaleManager localeManager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getLocaleManager();
		ScoreboardManager scoreboardManager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getScoreboardManager();

		scoreboardManager.stopAnimations();
		configManager.reload();
		scoreboardManager.reloadAll();
		scoreboardManager.startAnimations();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "reload.all-success"),
			"%prefix%", localeManager.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
