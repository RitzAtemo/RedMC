package red.aviora.redmc.vault.commands.player;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.permissions.models.PermissionEntry;
import red.aviora.redmc.permissions.models.PlayerData;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import red.aviora.redmc.vault.utils.VaultManager;
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

public class PlayerAltnameSetCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();

		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();
		VaultManager vaultManager = JavaPlugin.getPlugin(VaultPlugin.class).getVaultManager();

		String playerName = StringArgumentType.getString(context, "player");
		String altName = StringArgumentType.getString(context, "name");
		int weight = IntegerArgumentType.getInteger(context, "weight");

		VaultPlayerData playerData = vaultManager.getPlayerByName(playerName);
		if (playerData == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "player-not-found"),
						"%name%", playerName,
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

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
		PlayerData pData = permissionManager.getOrCreatePlayer(playerData.getUuid(), playerName);

		var permissions = pData.getPermissions();
		permissions.removeIf(p -> p.getName().startsWith("vault.altname."));
		pData.addPermission(new PermissionEntry("vault.altname." + altName, weight, true));
		permissionManager.reloadAll();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "success"),
			"%prefix%", localeManager.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
