package red.aviora.redmc.chat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.chat.ChatPlugin;

public class MsgCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();
		ChatPlugin plugin = JavaPlugin.getPlugin(ChatPlugin.class);
		LocaleManager locale = plugin.getLocaleManager();

		if (!(sender instanceof Player player)) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(locale.getMessage(sender, "error.only-players"),
						"%prefix%", locale.getMessage(sender, "prefix"))
				)
			).create();
		}

		String targetName = StringArgumentType.getString(context, "player");
		String message = StringArgumentType.getString(context, "message");

		Player target = Bukkit.getPlayerExact(targetName);
		if (target == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(locale.getMessage(player, "error.player-not-found"),
						"%prefix%", locale.getMessage(player, "prefix"))
				)
			).create();
		}

		plugin.getChatManager().sendPrivateMessage(player, target, message, null);
		return Command.SINGLE_SUCCESS;
	}
}
