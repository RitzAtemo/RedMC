package red.aviora.redmc.chat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.aviora.redmc.chat.ChatPlugin;

import java.util.UUID;

public class ReplyCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();
		ChatPlugin plugin = ChatPlugin.getInstance();

		if (!(sender instanceof Player player)) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					MiniMessage.miniMessage().deserialize(plugin.getLocaleManager().getMessage(sender, "chat.only-players"))
				)
			).create();
		}

		String message = StringArgumentType.getString(context, "message");
		UUID lastSenderId = plugin.getSessionManager().getLastSender(player.getUniqueId());

		if (lastSenderId == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					MiniMessage.miniMessage().deserialize(plugin.getLocaleManager().getMessage(player, "chat.no-reply-target"))
				)
			).create();
		}

		Player target = Bukkit.getPlayer(lastSenderId);
		if (target == null || !target.isOnline()) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					MiniMessage.miniMessage().deserialize(plugin.getLocaleManager().getMessage(player, "chat.no-target"))
				)
			).create();
		}

		String quotedMessage = plugin.getSessionManager().getLastReceivedMessage(player.getUniqueId());
		plugin.getChatManager().sendPrivateMessage(player, target, message, quotedMessage);
		return Command.SINGLE_SUCCESS;
	}
}
