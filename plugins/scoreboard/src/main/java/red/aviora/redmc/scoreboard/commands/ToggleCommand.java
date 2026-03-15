package red.aviora.redmc.scoreboard.commands;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.scoreboard.ScoreboardPlugin;
import red.aviora.redmc.scoreboard.utils.ScoreboardManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ToggleCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();

		if (!(sender instanceof Player player)) {
			LocaleManager localeManager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getLocaleManager();
			ApiUtils.sendCommandSenderMessageArgs(sender,
				localeManager.getMessage(sender, "players-only"),
				"%prefix%", localeManager.getMessage(sender, "prefix"));
			return Command.SINGLE_SUCCESS;
		}

		LocaleManager localeManager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getLocaleManager();
		ScoreboardManager scoreboardManager = JavaPlugin.getPlugin(ScoreboardPlugin.class).getScoreboardManager();

		boolean nowVisible = scoreboardManager.toggleVisibility(player);

		String messageKey = nowVisible ? "toggle-shown" : "toggle-hidden";
		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, messageKey),
			"%prefix%", localeManager.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
