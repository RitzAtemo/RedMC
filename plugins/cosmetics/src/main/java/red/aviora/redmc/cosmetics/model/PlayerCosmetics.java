package red.aviora.redmc.cosmetics.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCosmetics {

    private final UUID uuid;
    private final Map<CosmeticSlot, String> equipped = new EnumMap<>(CosmeticSlot.class);
    private boolean visible = true;

    public PlayerCosmetics(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }

    public void equip(CosmeticSlot slot, String templateName) {
        equipped.put(slot, templateName);
    }

    public void unequip(CosmeticSlot slot) {
        equipped.remove(slot);
    }

    public void unequipAll() {
        equipped.clear();
    }

    public String getEquipped(CosmeticSlot slot) {
        return equipped.get(slot);
    }

    public boolean hasEquipped(CosmeticSlot slot) {
        return equipped.containsKey(slot);
    }

    public Map<CosmeticSlot, String> getEquippedMap() {
        return Collections.unmodifiableMap(equipped);
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
