package red.aviora.redmc.vault.commands.player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.permissions.models.PlayerData;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.utils.VaultMetaResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSearchCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();
		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();

		String field = StringArgumentType.getString(context, "field");
		String query = StringArgumentType.getString(context, "query").toLowerCase();

		PermissionsPlugin permPlugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("RedMC-Permissions");
		if (permPlugin == null) {
			ApiUtils.sendCommandSenderMessageArgs(sender,
				localeManager.getMessage(sender, "error.generic"),
				"%prefix%", localeManager.getMessage(sender, "prefix"));
			return Command.SINGLE_SUCCESS;
		}

		var pm = permPlugin.getPermissionManager();
		List<PlayerData> results = new ArrayList<>();

		for (var entry : pm.getPlayers().entrySet()) {
			UUID uuid = entry.getKey();
			PlayerData playerData = entry.getValue();
			String value = switch (field) {
				case "prefix" -> VaultMetaResolver.getPrefix(uuid, pm);
				case "suffix" -> VaultMetaResolver.getSuffix(uuid, pm);
				default       -> VaultMetaResolver.getAltName(uuid, pm);
			};
			if (value.toLowerCase().contains(query)) {
				results.add(playerData);
			}
		}

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "search-header"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%query%", query,
			"%field%", field,
			"%count%", String.valueOf(results.size()));

		if (results.isEmpty()) {
			ApiUtils.sendCommandSenderMessageArgs(sender,
				localeManager.getMessage(sender, "search-empty"),
				"%prefix%", localeManager.getMessage(sender, "prefix"),
				"%query%", query);
			return Command.SINGLE_SUCCESS;
		}

		for (PlayerData playerData : results) {
			String entry = ApiUtils.formatTextString(localeManager.getMessage(sender, "search-entry"),
				"%prefix%", localeManager.getMessage(sender, "prefix"),
				"%realname%", playerData.getName());
			entry = VaultPlugin.resolvePlayerByUuid(entry, playerData.getUuid());
			sender.sendMessage(ApiUtils.formatText(entry));
		}

		return Command.SINGLE_SUCCESS;
	}
}
