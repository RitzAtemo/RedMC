package red.aviora.redmc.chat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;

public class SayCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		if (!(sender instanceof Player player)) return SINGLE_SUCCESS;

		ChatPlugin plugin = JavaPlugin.getPlugin(ChatPlugin.class);

		if (!player.hasPermission("redmc.chat.global")) {
			throw ApiUtils.noPermissionException(plugin.getLocaleManager(), sender);
		}

		String message = StringArgumentType.getString(ctx, "message");
		plugin.getServer().getGlobalRegionScheduler().run(plugin, task ->
			plugin.getChatManager().broadcastGlobal(player, message));
		return SINGLE_SUCCESS;
	}
}
