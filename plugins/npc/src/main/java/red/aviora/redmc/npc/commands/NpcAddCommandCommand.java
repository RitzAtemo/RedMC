package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.models.NpcCommand;

public class NpcAddCommandCommand implements Command<CommandSourceStack> {

	private final boolean leftClick;

	public NpcAddCommandCommand(boolean leftClick) {
		this.leftClick = leftClick;
	}

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		String id = StringArgumentType.getString(ctx, "id");
		String typeStr = StringArgumentType.getString(ctx, "type").toUpperCase();
		String command = StringArgumentType.getString(ctx, "command");

		NpcCommand.Type type;
		try {
			type = NpcCommand.Type.valueOf(typeStr);
		} catch (IllegalArgumentException e) {
			throw new SimpleCommandExceptionType(net.minecraft.network.chat.Component.literal(
				"Invalid type. Use: console or player")).create();
		}

		boolean added = NpcPlugin.getInstance().getNpcManager()
			.addCommand(id, leftClick, new NpcCommand(type, command));

		if (!added) {
			throw new SimpleCommandExceptionType(net.minecraft.network.chat.Component.literal(
				"NPC not found: " + id)).create();
		}

		ctx.getSource().getSender().sendMessage(Component.text(
			"Added " + (leftClick ? "left" : "right") + "-click command (" + type.name().toLowerCase()
				+ ") to NPC " + id + ": " + command));
		return 1;
	}
}
