package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.utils.NpcManager;

public class NpcCreateCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(NpcPlugin.class).getLocaleManager();
		NpcManager manager = JavaPlugin.getPlugin(NpcPlugin.class).getNpcManager();

		if (!(sender instanceof Player player)) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "players-only"),
					"%prefix%", locale.getMessage(sender, "prefix"))
			)).create();
		}

		String id = StringArgumentType.getString(context, "id");

		if (manager.getNpc(id) != null) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "npc-already-exists"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%id%", id)
			)).create();
		}

		manager.createNpc(id, id,
			player.getWorld().getName(),
			player.getX(), player.getY(), player.getZ(),
			player.getYaw(), player.getPitch());

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-created"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%id%", id);

		return Command.SINGLE_SUCCESS;
	}
}
