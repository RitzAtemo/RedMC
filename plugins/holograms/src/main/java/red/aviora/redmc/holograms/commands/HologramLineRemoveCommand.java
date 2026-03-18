package red.aviora.redmc.holograms.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import red.aviora.redmc.holograms.HologramsPlugin;
import red.aviora.redmc.holograms.models.HologramData;
import red.aviora.redmc.holograms.utils.HologramManager;

public class HologramLineRemoveCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(HologramsPlugin.class).getLocaleManager();
		HologramManager manager = JavaPlugin.getPlugin(HologramsPlugin.class).getHologramManager();

		String id = StringArgumentType.getString(context, "id");
		HologramData data = manager.getHologram(id);

		if (data == null) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "hologram-not-found"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%id%", id)
			)).create();
		}

		int index = IntegerArgumentType.getInteger(context, "index");

		if (index < 0 || index >= data.getLines().size()) {
			throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
				ApiUtils.formatText(locale.getMessage(sender, "invalid-line-index"),
					"%prefix%", locale.getMessage(sender, "prefix"),
					"%index%", String.valueOf(index),
					"%max%", String.valueOf(data.getLines().size() - 1))
			)).create();
		}

		data.getLines().remove(index);
		manager.refreshLines(data);

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "hologram-line-removed"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%id%", id,
			"%index%", String.valueOf(index));

		return Command.SINGLE_SUCCESS;
	}
}
