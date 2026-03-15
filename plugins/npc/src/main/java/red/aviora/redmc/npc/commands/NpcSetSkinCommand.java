package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.utils.NpcManager;
import red.aviora.redmc.npc.utils.SkinFetcher;

public class NpcSetSkinCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(NpcPlugin.class).getLocaleManager();
		NpcManager manager = JavaPlugin.getPlugin(NpcPlugin.class).getNpcManager();

		String id = StringArgumentType.getString(context, "id");
		String playerName = StringArgumentType.getString(context, "player");

		if (manager.getNpc(id) == null) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "npc-not-found"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%id%", id)
			)).create();
		}

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-skin-fetching"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%name%", playerName);

		SkinFetcher.fetchAsync(playerName, (result, error) -> {
			if (result == null) {
				ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-skin-error"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%name%", playerName);
				return;
			}

			manager.setSkin(id, result.texture(), result.signature(), playerName);

			ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-skin-set"),
				"%prefix%", locale.getMessage(sender, "prefix"),
				"%id%", id,
				"%name%", playerName);
		});

		return Command.SINGLE_SUCCESS;
	}
}
