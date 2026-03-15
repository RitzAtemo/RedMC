package red.aviora.redmc.vault.commands.group;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.permissions.models.Group;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.utils.VaultMetaResolver;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupSuffixGetCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();

		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();

		String groupId = StringArgumentType.getString(context, "group");

		var permPlugin = JavaPlugin.getPlugin(PermissionsPlugin.class);
		if (permPlugin == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "error"),
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		var permissionManager = permPlugin.getPermissionManager();
		Group group = permissionManager.getGroups().get(groupId.toLowerCase());
		if (group == null) {
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

		String suffixValue = VaultMetaResolver.getGroupSuffix(groupId, permissionManager);
		String displayValue = suffixValue.isEmpty() ? "" : suffixValue;

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "group-suffix-value"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%value%", displayValue);

		return Command.SINGLE_SUCCESS;
	}
}
