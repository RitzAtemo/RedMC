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
import red.aviora.redmc.npc.models.NpcCommand;

public class NpcCommandsAddCommand implements Command<CommandSourceStack> {

	private final boolean leftClick;

	public NpcCommandsAddCommand(boolean leftClick) {
		this.leftClick = leftClick;
	}

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(NpcPlugin.class).getLocaleManager();

		String id = StringArgumentType.getString(ctx, "id");
		String typeStr = StringArgumentType.getString(ctx, "type").toUpperCase();
		String command = StringArgumentType.getString(ctx, "command");

		NpcCommand.Type type;
		try {
			type = NpcCommand.Type.valueOf(typeStr);
		} catch (IllegalArgumentException e) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "invalid-command-type"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%type%", typeStr)
			)).create();
		}

		boolean added = NpcPlugin.getInstance().getNpcManager()
			.addCommand(id, leftClick, new NpcCommand(type, command));

		if (!added) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "npc-not-found"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%id%", id)
			)).create();
		}

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-command-added"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%id%", id,
			"%click%", leftClick ? "LEFT" : "RIGHT",
			"%command%", command);

		return Command.SINGLE_SUCCESS;
	}
}
