package red.aviora.redmc.cosmetics.manager;

import org.bukkit.entity.Player;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;
import red.aviora.redmc.cosmetics.storage.PlayerCosmeticsStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCosmeticsManager {

    private final PlayerCosmeticsStorage storage;
    private final Map<UUID, PlayerCosmetics> cache = new ConcurrentHashMap<>();

    public PlayerCosmeticsManager(PlayerCosmeticsStorage storage) {
        this.storage = storage;
    }

    public void onJoin(Player player) {
        cache.put(player.getUniqueId(), storage.load(player.getUniqueId()));
    }

    public void onQuit(Player player) {
        PlayerCosmetics cosmetics = cache.remove(player.getUniqueId());
        if (cosmetics != null) storage.save(cosmetics);
    }

    public PlayerCosmetics get(Player player) {
        return cache.get(player.getUniqueId());
    }

    public void equip(Player player, CosmeticSlot slot, String templateName) {
        PlayerCosmetics cosmetics = getOrCreate(player);
        cosmetics.equip(slot, templateName);
        storage.save(cosmetics);
    }

    public boolean unequip(Player player, CosmeticSlot slot) {
        PlayerCosmetics cosmetics = get(player);
        if (cosmetics == null || !cosmetics.hasEquipped(slot)) return false;
        cosmetics.unequip(slot);
        storage.save(cosmetics);
        return true;
    }

    public void unequipAll(Player player) {
        PlayerCosmetics cosmetics = getOrCreate(player);
        cosmetics.unequipAll();
        storage.save(cosmetics);
    }

    public boolean toggleVisibility(Player player) {
        PlayerCosmetics cosmetics = getOrCreate(player);
        cosmetics.setVisible(!cosmetics.isVisible());
        storage.save(cosmetics);
        return cosmetics.isVisible();
    }

    public void reset(Player player) {
        cache.remove(player.getUniqueId());
        storage.delete(player.getUniqueId());
        cache.put(player.getUniqueId(), new PlayerCosmetics(player.getUniqueId()));
    }

    public void saveAll() {
        for (PlayerCosmetics cosmetics : cache.values()) {
            storage.save(cosmetics);
        }
    }

    private PlayerCosmetics getOrCreate(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), uuid -> storage.load(uuid));
    }
}
