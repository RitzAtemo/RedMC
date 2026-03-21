package red.aviora.redmc.vault;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.permissions.PermissionsPlugin;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;
import red.aviora.redmc.vault.utils.VaultMetaResolver;

import java.util.UUID;
import red.aviora.redmc.vault.listeners.PlayerJoinListener;
import red.aviora.redmc.vault.registries.VaultPlaceholderRegistry;
import red.aviora.redmc.vault.utils.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultPlugin extends JavaPlugin {

	private static VaultPlugin instance;

	private ConfigManager configManager;
	private LocaleManager localeManager;
	private VaultManager vaultManager;

	@Override
	public void onEnable() {
		instance = this;

		configManager = new ConfigManager(this, "config.yml", "vault_players.yml");
		localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");

		vaultManager = new VaultManager();
		vaultManager.loadAll();

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

		registerPlaceholders();
	}

	private void registerPlaceholders() {
		PlaceholdersPlugin placeholdersPlugin = JavaPlugin.getPlugin(PlaceholdersPlugin.class);
		if (placeholdersPlugin != null) {
			placeholdersPlugin.getRegistryManager().addRegistry(
				VaultPlaceholderRegistry.generate(),
				10
			);
		}
	}

	public static String resolvePlayerPlaceholders(String text, Player player) {
		try {
			PlaceholdersPlugin pp = JavaPlugin.getPlugin(PlaceholdersPlugin.class);
			if (pp != null) return pp.getPlaceholderResolver().parseString(text, player);
		} catch (Exception ignored) {}
		return text;
	}

	public static String resolvePlayer(String text, Player target) {
		if (target != null) {
			String altName = resolvePlayerPlaceholders("##PlayerAltName##", target);
			String prefix  = resolvePlayerPlaceholders("##PlayerPrefix##",  target);
			String suffix  = resolvePlayerPlaceholders("##PlayerSuffix##",  target);
			text = text
					.replace("%sender_altname%", altName)
					.replace("%sender_prefix%",  prefix)
					.replace("%sender_suffix%",  suffix)
					.replace("%target_altname%", altName)
					.replace("%target_prefix%",  prefix)
					.replace("%target_suffix%",  suffix)
					.replace("%player_altname%", altName)
					.replace("%player_prefix%",  prefix)
					.replace("%player_suffix%",  suffix);
		}
		return text;
	}

	public static String resolvePlayerByUuid(String text, UUID uuid) {
		try {
			PermissionsPlugin permPlugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("RedMC-Permissions");
			if (permPlugin != null) {
				var pm = permPlugin.getPermissionManager();
				String altName = VaultMetaResolver.getAltName(uuid, pm);
				String prefix  = VaultMetaResolver.getPrefix(uuid, pm);
				String suffix  = VaultMetaResolver.getSuffix(uuid, pm);
				if (altName.isEmpty()) {
					var vd = getInstance().getVaultManager().getPlayerByUuid(uuid);
					if (vd != null) altName = vd.getName();
				}
				text = text
					.replace("%sender_altname%", altName).replace("%sender_prefix%", prefix).replace("%sender_suffix%", suffix)
					.replace("%target_altname%", altName).replace("%target_prefix%", prefix).replace("%target_suffix%", suffix)
					.replace("%player_altname%", altName).replace("%player_prefix%", prefix).replace("%player_suffix%", suffix);
			}
		} catch (Exception ignored) {}
		return text;
	}

	public static String resolveTwoPlayers(String text, Player sender, Player target) {
		if (sender != null) {
			text = text
				.replace("%sender_altname%", resolvePlayerPlaceholders("##PlayerAltName##", sender))
				.replace("%sender_prefix%", resolvePlayerPlaceholders("##PlayerPrefix##", sender))
				.replace("%sender_suffix%", resolvePlayerPlaceholders("##PlayerSuffix##", sender));
		}
		if (target != null) {
			text = text
				.replace("%target_altname%", resolvePlayerPlaceholders("##PlayerAltName##", target))
				.replace("%target_prefix%", resolvePlayerPlaceholders("##PlayerPrefix##", target))
				.replace("%target_suffix%", resolvePlayerPlaceholders("##PlayerSuffix##", target));
		}
		return text;
	}

	public static String resolveTwoPlayers(String text, Player sender, UUID targetUuid) {
		if (sender != null) {
			text = text
				.replace("%sender_altname%", resolvePlayerPlaceholders("##PlayerAltName##", sender))
				.replace("%sender_prefix%", resolvePlayerPlaceholders("##PlayerPrefix##", sender))
				.replace("%sender_suffix%", resolvePlayerPlaceholders("##PlayerSuffix##", sender));
		}
		try {
			PermissionsPlugin permPlugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("RedMC-Permissions");
			if (permPlugin != null) {
				var pm = permPlugin.getPermissionManager();
				String altName = VaultMetaResolver.getAltName(targetUuid, pm);
				if (altName.isEmpty()) {
					var vd = getInstance().getVaultManager().getPlayerByUuid(targetUuid);
					if (vd != null) altName = vd.getName();
				}
				text = text
					.replace("%target_altname%", altName)
					.replace("%target_prefix%", VaultMetaResolver.getPrefix(targetUuid, pm))
					.replace("%target_suffix%", VaultMetaResolver.getSuffix(targetUuid, pm));
			}
		} catch (Exception ignored) {}
		return text;
	}

	public static VaultPlugin getInstance() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public LocaleManager getLocaleManager() {
		return localeManager;
	}

	public VaultManager getVaultManager() {
		return vaultManager;
	}
}
