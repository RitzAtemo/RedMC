package red.aviora.redmc.npc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.models.NpcData;
import red.aviora.redmc.npc.utils.NpcManager;

import java.util.Map;

public class NpcReadCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();
		LocaleManager locale = JavaPlugin.getPlugin(NpcPlugin.class).getLocaleManager();
		NpcManager manager = JavaPlugin.getPlugin(NpcPlugin.class).getNpcManager();

		Map<String, NpcData> all = manager.getAllNpcs();

		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "npc-read-header"),
			"%prefix%", locale.getMessage(sender, "prefix"),
			"%count%", String.valueOf(all.size()));

		String entryTemplate = locale.getMessage(sender, "npc-read-entry");
		for (NpcData data : all.values()) {
			String line = entryTemplate
				.replace("%id%", data.getId())
				.replace("%world%", data.getWorld())
				.replace("%.1f%x%", String.format("%.1f", data.getX()))
				.replace("%.1f%y%", String.format("%.1f", data.getY()))
				.replace("%.1f%z%", String.format("%.1f", data.getZ()));
			ApiUtils.sendCommandSenderMessage(sender, line);
		}

		return Command.SINGLE_SUCCESS;
	}
}
