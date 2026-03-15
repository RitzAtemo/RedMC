package red.aviora.redmc.chat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;

public class ChatReloadCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();
		ChatPlugin plugin = ChatPlugin.getInstance();

		plugin.getConfigManager().reload();
		plugin.getChatManager().loadAll();
		plugin.getAlertManager().reload();

		ApiUtils.sendCommandSenderMessage(sender, plugin.getLocaleManager().getMessage(sender, "chat.reload-success"));
		return Command.SINGLE_SUCCESS;
	}
}
