package red.aviora.redmc.vault.commands.pay;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.Currency;
import red.aviora.redmc.vault.models.VaultPlayerData;
import red.aviora.redmc.vault.utils.VaultManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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

public class PayCommand implements Command<CommandSourceStack> {

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

		String targetName = StringArgumentType.getString(context, "player");
		double amount = DoubleArgumentType.getDouble(context, "amount");

		VaultPlayerData senderData = vaultManager.getPlayerByUuid(player.getUniqueId());
		if (senderData == null) {
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

		VaultPlayerData targetData = vaultManager.getPlayerByName(targetName);
		if (targetData == null) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "player-not-found"),
						"%name%", targetName,
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

		if (senderData.getBalance(currency.getId()) < amount) {
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						localeManager.getMessage(sender, "insufficient-funds"),
						"%balance%", String.valueOf(senderData.getBalance(currency.getId())),
						"%prefix%", localeManager.getMessage(sender, "prefix")
					)
				)
			).create();
		}

		senderData.subtractBalance(currency.getId(), amount);
		targetData.addBalance(currency.getId(), amount);
		vaultManager.reloadAll();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "pay-success"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%target%", targetData.getName(),
			"%amount%", String.valueOf(amount),
			"%symbol%", currency.getSymbol());

		Player targetPlayer = Bukkit.getPlayerExact(targetName);
		if (targetPlayer != null && targetPlayer.isOnline()) {
			ApiUtils.sendCommandSenderMessageArgs(targetPlayer,
				localeManager.getMessage(targetPlayer, "pay-received"),
				"%prefix%", localeManager.getMessage(targetPlayer, "prefix"),
				"%sender%", player.getName(),
				"%amount%", String.valueOf(amount),
				"%symbol%", currency.getSymbol());
		}

		return Command.SINGLE_SUCCESS;
	}
}
