package red.aviora.redmc.vault.commands.player;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import red.aviora.redmc.vault.utils.VaultManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerEconomyBalanceCommand implements Command<CommandSourceStack> {

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

		var currencyManager = vaultManager.getCurrencyManager();
		var currency = currencyManager.getDefaultCurrency();

		try {
			String currencyId = StringArgumentType.getString(context, "currency");
			var customCurrency = currencyManager.getCurrency(currencyId);
			if (customCurrency != null) {
				currency = customCurrency;
			}
		} catch (IllegalArgumentException ignored) {
		}

		double balance = playerData.getBalance(currency.getId());

		String msg = ApiUtils.formatTextString(localeManager.getMessage(sender, "economy-balance"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%balance%", String.valueOf(balance),
			"%currency%", currency.getDisplayName(),
			"%symbol%", currency.getSymbol());
		Player onlinePlayer = Bukkit.getPlayerExact(playerName);
		msg = onlinePlayer != null
			? VaultPlugin.resolvePlayer(msg, onlinePlayer)
			: VaultPlugin.resolvePlayerByUuid(msg, playerData.getUuid());
		sender.sendMessage(ApiUtils.formatText(msg));

		return Command.SINGLE_SUCCESS;
	}
}
