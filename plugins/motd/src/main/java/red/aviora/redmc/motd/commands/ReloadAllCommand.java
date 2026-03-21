package red.aviora.redmc.motd.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.motd.MotdPlugin;

public class ReloadAllCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) {
		CommandSender sender = ctx.getSource().getSender();
		MotdPlugin plugin = JavaPlugin.getPlugin(MotdPlugin.class);
		LocaleManager locale = plugin.getLocaleManager();
		plugin.getConfigManager().reload();
		plugin.getMotdManager().loadTemplates();
		plugin.getMotdManager().loadIcons();
		ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "reload.all-success"),
			"%prefix%", locale.getMessage(sender, "prefix"));
		return SINGLE_SUCCESS;
	}
}
