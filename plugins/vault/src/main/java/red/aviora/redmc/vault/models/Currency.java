package red.aviora.redmc.vault.models;

import java.util.*;

public class Currency {
	private final String id;
	private final String displayName;
	private final String symbol;
	private final double startingBalance;
	private final boolean ranksEnabled;
	private final TreeMap<Double, String> rankTiers;

	public Currency(String id, String displayName, String symbol, double startingBalance,
			boolean ranksEnabled, Map<Double, String> rankTiers) {
		this.id = id.toLowerCase();
		this.displayName = displayName;
		this.symbol = symbol;
		this.startingBalance = startingBalance;
		this.ranksEnabled = ranksEnabled;
		this.rankTiers = new TreeMap<>(rankTiers != null ? rankTiers : new HashMap<>());
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSymbol() {
		return symbol;
	}

	public double getStartingBalance() {
		return startingBalance;
	}

	public boolean isRanksEnabled() {
		return ranksEnabled;
	}

	public String getRank(double balance) {
		if (!ranksEnabled || rankTiers.isEmpty()) {
			return null;
		}

		// Find the highest tier that the balance qualifies for
		Double tierThreshold = null;
		for (Double threshold : rankTiers.descendingKeySet()) {
			if (balance >= threshold) {
				tierThreshold = threshold;
				break;
			}
		}

		return tierThreshold != null ? rankTiers.get(tierThreshold) : null;
	}

	@Override
	public String toString() {
		return displayName + " (" + symbol + ")";
	}
}
