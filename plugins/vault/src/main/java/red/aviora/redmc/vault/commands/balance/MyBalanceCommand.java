package red.aviora.redmc.vault.commands.balance;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import red.aviora.redmc.vault.utils.VaultManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class MyBalanceCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		CommandSender sender = context.getSource().getSender();

		if (!(sender instanceof Player)) {
			LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "player-only"),
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		Player player = (Player) sender;
		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();
		VaultManager vaultManager = JavaPlugin.getPlugin(VaultPlugin.class).getVaultManager();

		VaultPlayerData playerData = vaultManager.getPlayerByUuid(player.getUniqueId());
		if (playerData == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "player-not-found"),
						"%name%", player.getName(),
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		var currencyManager = vaultManager.getCurrencyManager();
		var currency = currencyManager.getDefaultCurrency();

		// Try to get currency from argument if provided
		try {
			String currencyId = StringArgumentType.getString(context, "currency");
			var customCurrency = currencyManager.getCurrency(currencyId);
			if (customCurrency != null) {
				currency = customCurrency;
			}
		} catch (IllegalArgumentException ignored) {
			// Currency argument not provided, use default
		}

		double balance = playerData.getBalance(currency.getId());

		// Get rank if enabled for this currency
		String rank = currency.getRank(balance);
		String rankText = rank != null ? " " + rank : "";

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "my-balance"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%balance%", String.valueOf(balance),
			"%currency%", currency.getDisplayName(),
			"%symbol%", currency.getSymbol(),
			"%rank%", rankText);

		return Command.SINGLE_SUCCESS;
	}
}
