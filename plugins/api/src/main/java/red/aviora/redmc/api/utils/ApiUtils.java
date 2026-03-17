package red.aviora.redmc.api.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ApiUtils {

	public static MiniMessage getMM() {
		return MiniMessage.miniMessage();
	}

	public static void broadcastMessage(String message) {
		broadcastMessageArgs(message);
	}

	public static void broadcastMessageArgs(String message, String... args) {
		Bukkit.broadcast(formatText(message, args));
	}

	public static void sendPlayerMessage(Player player, String message) {
		sendPlayerMessageArgs(player, message);
	}

	public static void sendPlayerMessageArgs(Player player, String message, String... args) {
		player.sendMessage(formatText(message, args));
	}

	public static void sendCommandSenderMessage(CommandSender sender, String message) {
		sendCommandSenderMessageArgs(sender, message);
	}

	public static void sendCommandSenderMessageArgs(CommandSender sender, String message, String... args) {
		sender.sendMessage(formatText(message, args));
	}

	public static void log(String message) {
		logArgs(message);
	}

	public static void logArgs(String message, String... args) {
		Bukkit.getConsoleSender().sendMessage(formatText(message, args));
	}

	public static String formatTextString(String raw, String... args) {
		String formatted = raw;

		if (!formatted.isBlank() && args.length % 2 == 0) {
			for (int i = 0; i < args.length; i += 2) {
				formatted = formatted.replace(args[i], args[i + 1]);
			}
		}

		return formatted;
	}

	public static Component formatText(String raw, String... args) {
		String formatted = formatTextString(raw, args);
		return getMM().deserialize(formatted);
	}

	public static CommandSyntaxException noPermissionException(LocaleManager locale, CommandSender sender) {
		return new SimpleCommandExceptionType(
			MessageComponentSerializer.message().serialize(
				formatText(locale.getMessage(sender, "error.no-permission"),
					"%prefix%", locale.getMessage(sender, "prefix"))
			)
		).create();
	}
}
