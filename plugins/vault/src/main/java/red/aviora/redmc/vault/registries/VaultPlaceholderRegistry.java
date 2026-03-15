package red.aviora.redmc.vault.registries;

import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.placeholders.models.PlaceholderContext;
import red.aviora.redmc.placeholders.utils.PlaceholderRegistry;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import red.aviora.redmc.vault.utils.VaultManager;
import red.aviora.redmc.vault.utils.VaultMetaResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class VaultPlaceholderRegistry {

	public static PlaceholderRegistry generate() {
		PlaceholderRegistry registry = new PlaceholderRegistry();
		VaultManager vaultManager = VaultPlugin.getInstance().getVaultManager();

		registry.set("PlayerPrefix", (PlaceholderContext) player -> {
			try {
				PermissionsPlugin permPlugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("RedMC-Permissions");
				if (permPlugin == null) return "";
				return VaultMetaResolver.getPrefix(player.getUniqueId(), permPlugin.getPermissionManager());
			} catch (Exception e) {
				return "";
			}
		});

		registry.set("PlayerSuffix", (PlaceholderContext) player -> {
			try {
				PermissionsPlugin permPlugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("RedMC-Permissions");
				if (permPlugin == null) return "";
				return VaultMetaResolver.getSuffix(player.getUniqueId(), permPlugin.getPermissionManager());
			} catch (Exception e) {
				return "";
			}
		});

		registry.set("PlayerAltName", (PlaceholderContext) player -> {
			try {
				PermissionsPlugin permPlugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("RedMC-Permissions");
				if (permPlugin == null) return player.getName();
				String altName = VaultMetaResolver.getAltName(player.getUniqueId(), permPlugin.getPermissionManager());
				return altName.isEmpty() ? player.getName() : altName;
			} catch (Exception e) {
				return player.getName();
			}
		});

		registry.set("PlayerBalance", (PlaceholderContext) player -> {
			VaultPlayerData data = vaultManager.getOrCreatePlayer(player.getUniqueId(), player.getName());
			String defaultCurrency = VaultPlugin.getInstance().getConfigManager()
				.getString("config.yml", "currencies.default", "credits");
			double balance = data.getBalance(defaultCurrency);
			return String.format("%.2f", balance);
		});

		registry.setPatternHandler((key, player) -> {
			if (key.startsWith("PlayerBalance_")) {
				String currencyId = key.substring("PlayerBalance_".length());
				VaultPlayerData data = vaultManager.getOrCreatePlayer(player.getUniqueId(), player.getName());
				double balance = data.getBalance(currencyId.toLowerCase());
				return String.format("%.2f", balance);
			}
			return null;
		});

		return registry;
	}
}
