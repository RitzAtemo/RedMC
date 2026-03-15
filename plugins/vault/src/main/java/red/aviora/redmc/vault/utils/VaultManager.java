package red.aviora.redmc.vault.utils;

import red.aviora.redmc.vault.VaultPlugin;
import red.aviora.redmc.vault.models.VaultPlayerData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class VaultManager {

	private final VaultDataStorage storage;
	private final CurrencyManager currencyManager;
	private final Map<UUID, VaultPlayerData> players = new HashMap<>();

	public VaultManager() {
		this.currencyManager = new CurrencyManager();
		this.storage = new VaultDataStorage();
	}

	public void loadAll() {
		players.clear();
		players.putAll(storage.loadPlayers());
	}

	public void saveAll() {
		storage.savePlayers(players);
	}

	public void reloadAll() {
		saveAll();
		loadAll();
	}

	public VaultPlayerData getOrCreatePlayer(UUID uuid, String name) {
		return players.computeIfAbsent(uuid, key -> {
			VaultPlayerData playerData = new VaultPlayerData(uuid, name, 0.0);
			for (var currency : currencyManager.getAllCurrencies().values()) {
				playerData.initializeCurrency(currency.getId(), currency.getStartingBalance());
			}
			return playerData;
		});
	}

	public VaultPlayerData getPlayerByUuid(UUID uuid) {
		return players.get(uuid);
	}

	public VaultPlayerData getPlayerByName(String name) {
		for (VaultPlayerData player : players.values()) {
			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}

	public Map<UUID, VaultPlayerData> getPlayers() {
		return players;
	}

	public CurrencyManager getCurrencyManager() {
		return currencyManager;
	}
}
