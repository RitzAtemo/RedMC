package red.aviora.redmc.api.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class LocaleManager {

	private final ConfigManager configManager;
	private final String defaultLocale;
	private final List<String> locales = new ArrayList<>();

	public LocaleManager(ConfigManager configManager, String defaultLocale, String... supportedLocales) {
		this.configManager = configManager;
		this.defaultLocale = defaultLocale;

		for (String locale : supportedLocales) {
			String fileName = "lang/" + locale + ".yml";
			locales.add(locale);
			configManager.registerResources(fileName);
		}

		configManager.load();
	}

	public String getMessage(CommandSender sender, String key) {
		String playerLocale;
		if (sender instanceof Player player) {
			playerLocale = player.locale().toString();
		} else {
			playerLocale = defaultLocale;
		}

		String fileName = "lang/" + playerLocale + ".yml";
		String msg = configManager.getString(fileName, key, null);

		if (msg == null) {
			fileName = "lang/" + defaultLocale + ".yml";
			msg = configManager.getString(fileName, key, null);
		}

		if (msg == null) {
			msg = "Locale not found for this action.";
			ApiUtils.logArgs(
				"Locale for \"%key%\" not found for plugin %plugin%",
				"%key%", key,
				"%plugin%", configManager.getCurrentPlugin().getName()
			);
		}

		return msg;
	}

	public String getMessageArgs(CommandSender sender, String key, String... args) {
		String msg = getMessage(sender, key);
		return ApiUtils.formatTextString(msg, args);
	}
}
