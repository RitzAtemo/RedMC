package red.aviora.redmc.chat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;

public class SayCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) {
		CommandSender sender = ctx.getSource().getSender();
		if (!(sender instanceof Player player)) return SINGLE_SUCCESS;

		String message = StringArgumentType.getString(ctx, "message");
		ChatPlugin plugin = JavaPlugin.getPlugin(ChatPlugin.class);

		if (!player.hasPermission("redmc.chat.global")) {
			ApiUtils.sendCommandSenderMessageArgs(sender,
				plugin.getLocaleManager().getMessage(sender, "chat.no-permission"),
				"%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
			return SINGLE_SUCCESS;
		}

		plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
			plugin.getChatManager().broadcastGlobal(player, message));
		return SINGLE_SUCCESS;
	}
}
