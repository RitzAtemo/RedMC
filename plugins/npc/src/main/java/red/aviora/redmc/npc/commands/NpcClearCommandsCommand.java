package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import red.aviora.redmc.npc.NpcPlugin;

public class NpcClearCommandsCommand implements Command<CommandSourceStack> {

	private final boolean leftClick;

	public NpcClearCommandsCommand(boolean leftClick) {
		this.leftClick = leftClick;
	}

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		String id = StringArgumentType.getString(ctx, "id");

		boolean cleared = NpcPlugin.getInstance().getNpcManager().clearCommands(id, leftClick);

		if (!cleared) {
			throw new SimpleCommandExceptionType(net.minecraft.network.chat.Component.literal(
				"NPC not found: " + id)).create();
		}

		ctx.getSource().getSender().sendMessage(Component.text(
			"Cleared " + (leftClick ? "left" : "right") + "-click commands for NPC " + id));
		return 1;
	}
}
