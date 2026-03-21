package red.aviora.redmc.vault.commands.player;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import red.aviora.redmc.vault.utils.VaultManager;
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

public class PlayerAltnameGetCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();

		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();
		VaultManager vaultManager = JavaPlugin.getPlugin(VaultPlugin.class).getVaultManager();

		String playerName = StringArgumentType.getString(context, "player");

		VaultPlayerData playerData = vaultManager.getPlayerByName(playerName);
		if (playerData == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "error.player-not-found"),
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
						localeManager.getMessage(sender, "error.generic"),
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		var permissionManager = permPlugin.getPermissionManager();
		String altNameValue = VaultMetaResolver.getAltName(playerData.getUuid(), permissionManager);
		String displayValue = altNameValue.isEmpty() ? playerName : altNameValue;

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "player-altname-value"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%value%", displayValue);

		return Command.SINGLE_SUCCESS;
	}
}
