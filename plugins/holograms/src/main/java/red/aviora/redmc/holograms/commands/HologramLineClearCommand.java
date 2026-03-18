package red.aviora.redmc.holograms.commands;

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
import red.aviora.redmc.holograms.HologramsPlugin;
import red.aviora.redmc.holograms.models.HologramData;
import red.aviora.redmc.holograms.utils.HologramManager;

public class HologramLineClearCommand implements Command<CommandSourceStack> {

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

		data.getLines().clear();
		manager.refreshLines(data);

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "hologram-lines-cleared"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%id%", id);

		return Command.SINGLE_SUCCESS;
	}
}
