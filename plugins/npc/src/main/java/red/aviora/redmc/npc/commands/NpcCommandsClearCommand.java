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

public class NpcCommandsClearCommand implements Command<CommandSourceStack> {

	private final boolean leftClick;

	public NpcCommandsClearCommand(boolean leftClick) {
		this.leftClick = leftClick;
	}

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(NpcPlugin.class).getLocaleManager();

		String id = StringArgumentType.getString(ctx, "id");

		boolean cleared = NpcPlugin.getInstance().getNpcManager().clearCommands(id, leftClick);

		if (!cleared) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "npc-not-found"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%id%", id)
			)).create();
		}

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-commands-cleared"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%id%", id,
			"%click%", leftClick ? "LEFT" : "RIGHT");

		return Command.SINGLE_SUCCESS;
	}
}
