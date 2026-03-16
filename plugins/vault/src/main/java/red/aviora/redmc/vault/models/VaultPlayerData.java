package red.aviora.redmc.vault.models;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class VaultPlayerData {
	private final UUID uuid;
	private String name;
	private Map<String, Double> balances = new HashMap<>();

	public VaultPlayerData(UUID uuid, String name, double startingBalance) {
		this.uuid = uuid;
		this.name = name;
	}

	public void initializeCurrency(String currencyId, double startingBalance) {
		balances.putIfAbsent(currencyId, startingBalance);
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBalance(String currencyId) {
		return balances.getOrDefault(currencyId, 0.0);
	}

	public void setBalance(String currencyId, double balance) {
		balances.put(currencyId, balance);
	}

	public void addBalance(String currencyId, double amount) {
		double current = getBalance(currencyId);
		balances.put(currencyId, current + amount);
	}

	public void subtractBalance(String currencyId, double amount) {
		double current = getBalance(currencyId);
		balances.put(currencyId, Math.max(0, current - amount));
	}

	public Map<String, Double> getBalances() {
		return new HashMap<>(balances);
	}

	public void setBalances(Map<String, Double> balances) {
		this.balances = new HashMap<>(balances);
	}
}
