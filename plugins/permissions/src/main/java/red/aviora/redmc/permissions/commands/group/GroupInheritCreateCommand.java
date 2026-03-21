package red.aviora.redmc.permissions.commands.group;

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
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.permissions.models.Group;
import red.aviora.redmc.permissions.utils.PermissionManager;

public class GroupInheritCreateCommand implements Command<CommandSourceStack> {

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();

		LocaleManager localeManager = JavaPlugin.getPlugin(PermissionsPlugin.class).getLocaleManager();
		PermissionManager permissionManager = JavaPlugin.getPlugin(PermissionsPlugin.class).getPermissionManager();

		String groupId = StringArgumentType.getString(context, "<group>").toLowerCase();
		String parentId = StringArgumentType.getString(context, "<parent>").toLowerCase();

		if (!permissionManager.getGroups().containsKey(groupId)) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "not-found"),
						"%name%", groupId,
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		if (!permissionManager.getGroups().containsKey(parentId)) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "not-found"),
						"%name%", parentId,
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		Group group = permissionManager.getGroups().get(groupId);
		group.addInherit(parentId);

		permissionManager.reloadAll();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "success"),
			"%prefix%", localeManager.getMessage(sender, "prefix")
		);

		return Command.SINGLE_SUCCESS;
	}
}
