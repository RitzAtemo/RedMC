package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import red.aviora.redmc.npc.NpcPlugin;

public class NpcSetEquipmentCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		String id = StringArgumentType.getString(ctx, "id");
		String slot = StringArgumentType.getString(ctx, "slot");
		String item = StringArgumentType.getString(ctx, "item");

		boolean ok = NpcPlugin.getInstance().getNpcManager().setEquipment(id, slot, item);
		if (!ok) {
			throw new SimpleCommandExceptionType(net.minecraft.network.chat.Component.literal(
				"NPC not found or invalid slot: " + id)).create();
		}

		String display = item.equalsIgnoreCase("air") || item.equalsIgnoreCase("minecraft:air")
			? "cleared" : item;
		ctx.getSource().getSender().sendMessage(Component.text(
			"Equipment " + slot + " for NPC " + id + " set to " + display));
		return 1;
	}
}
