package red.aviora.redmc.holograms.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.holograms.HologramsPlugin;
import red.aviora.redmc.holograms.models.HologramData;
import red.aviora.redmc.holograms.utils.HologramManager;

import java.util.Map;

public class HologramReadCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(HologramsPlugin.class).getLocaleManager();
		HologramManager manager = JavaPlugin.getPlugin(HologramsPlugin.class).getHologramManager();

		Map<String, HologramData> all = manager.getAllHolograms();

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "hologram-read-header"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%count%", String.valueOf(all.size()));

		String entryTemplate = locale.getMessage(sender, "hologram-read-entry");
		for (HologramData data : all.values()) {
			String line = entryTemplate
				.replace("%id%", data.getId())
				.replace("%name%", data.getName())
				.replace("%world%", data.getWorld())
				.replace("%.1f%x%", String.format("%.1f", data.getX()))
				.replace("%.1f%y%", String.format("%.1f", data.getY()))
				.replace("%.1f%z%", String.format("%.1f", data.getZ()))
				.replace("%lines%", String.valueOf(data.getLines().size()));
			ApiUtils.sendCommandSenderMessage(sender, line);
		}

		return Command.SINGLE_SUCCESS;
	}
}
