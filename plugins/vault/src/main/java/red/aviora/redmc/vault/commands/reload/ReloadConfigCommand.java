package red.aviora.redmc.vault.commands.reload;

import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.vault.VaultPlugin;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadConfigCommand implements Command<CommandSourceStack> {

	public int run(CommandContext<CommandSourceStack> context) {
		CommandSender sender = context.getSource().getSender();

		ConfigManager configManager = JavaPlugin.getPlugin(VaultPlugin.class).getConfigManager();
		LocaleManager localeManager = JavaPlugin.getPlugin(VaultPlugin.class).getLocaleManager();
		VaultPlugin vaultPlugin = JavaPlugin.getPlugin(VaultPlugin.class);

		configManager.reload();
		vaultPlugin.getVaultManager().getCurrencyManager().reload();

		ApiUtils.sendCommandSenderMessageArgs(sender,
			localeManager.getMessage(sender, "reload.config-success"),
			"%prefix%", localeManager.getMessage(sender, "prefix"));

		return Command.SINGLE_SUCCESS;
	}
}
