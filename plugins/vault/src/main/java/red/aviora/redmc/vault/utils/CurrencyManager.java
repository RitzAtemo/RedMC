package red.aviora.redmc.vault.utils;

import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.Currency;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class CurrencyManager {
	private final Map<String, Currency> currencies = new HashMap<>();
	private String defaultCurrency;

	public CurrencyManager() {
		loadCurrencies();
	}

	public void reload() {
		currencies.clear();
		loadCurrencies();
	}

	private void loadCurrencies() {
		ConfigManager configManager = VaultPlugin.getInstance().getConfigManager();

		// Get default currency
		defaultCurrency = configManager.getString("config.yml", "currencies.default", "credits");

		// Load currency definitions
		ConfigurationSection currenciesSection = configManager.getConfig("config.yml")
			.getConfigurationSection("currencies.definitions");

		if (currenciesSection != null) {
			for (String currencyId : currenciesSection.getKeys(false)) {
				ConfigurationSection currencySec = currenciesSection.getConfigurationSection(currencyId);
				if (currencySec != null) {
					String displayName = currencySec.getString("display-name", currencyId);
					String symbol = currencySec.getString("symbol", "$");
					double startingBalance = currencySec.getDouble("starting-balance", 0.0);

					// Load rank configuration
					boolean ranksEnabled = false;
					Map<Double, String> rankTiers = new HashMap<>();

					ConfigurationSection ranksSection = currencySec.getConfigurationSection("ranks");
					if (ranksSection != null) {
						ranksEnabled = ranksSection.getBoolean("enabled", false);
						ConfigurationSection tiersSection = ranksSection.getConfigurationSection("tiers");
						if (tiersSection != null && ranksEnabled) {
							for (String tierKey : tiersSection.getKeys(false)) {
								try {
									double threshold = Double.parseDouble(tierKey);
									String rankName = tiersSection.getString(tierKey, "");
									rankTiers.put(threshold, rankName);
								} catch (NumberFormatException ignored) {
									// Skip invalid tier keys
								}
							}
						}
					}

					Currency currency = new Currency(currencyId, displayName, symbol, startingBalance, ranksEnabled, rankTiers);
					currencies.put(currency.getId(), currency);
				}
			}
		}

		// Ensure default currency exists
		if (!currencies.containsKey(defaultCurrency)) {
			Currency defaultCurr = new Currency(defaultCurrency, "Default", "$", 0.0, false, new HashMap<>());
			currencies.put(defaultCurrency, defaultCurr);
		}
	}

	public Currency getCurrency(String currencyId) {
		return currencies.getOrDefault(currencyId.toLowerCase(), currencies.get(defaultCurrency));
	}

	public Currency getDefaultCurrency() {
		return currencies.get(defaultCurrency);
	}

	public String getDefaultCurrencyId() {
		return defaultCurrency;
	}

	public Map<String, Currency> getAllCurrencies() {
		return new HashMap<>(currencies);
	}

	public boolean currencyExists(String currencyId) {
		return currencies.containsKey(currencyId.toLowerCase());
	}
}
