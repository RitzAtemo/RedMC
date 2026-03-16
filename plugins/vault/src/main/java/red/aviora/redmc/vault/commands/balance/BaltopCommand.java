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
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BaltopCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();

		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();
		VaultManager vaultManager = JavaPlugin.getPlugin(VaultPlugin.class).getVaultManager();

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

		final var finalCurrency = currency;
		List<VaultPlayerData> players = new ArrayList<>(vaultManager.getPlayers().values());
		players.sort((a, b) -> Double.compare(
			b.getBalance(finalCurrency.getId()),
			a.getBalance(finalCurrency.getId())
		));

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "baltop-header"),
			"%prefix%", localeManager.getMessage(sender, "prefix"),
			"%currency%", finalCurrency.getDisplayName());

		int count = Math.min(10, players.size());
		for (int i = 0; i < count; i++) {
			VaultPlayerData player = players.get(i);
			double balance = player.getBalance(finalCurrency.getId());
			ApiUtils.sendCommandSenderMessageArgs(sender,
				localeManager.getMessage(sender, "baltop-entry"),
				"%prefix%", localeManager.getMessage(sender, "prefix"),
				"%rank%", String.valueOf(i + 1),
				"%name%", player.getName(),
				"%balance%", String.valueOf(balance),
				"%symbol%", finalCurrency.getSymbol());
		}

		return Command.SINGLE_SUCCESS;
	}
}
