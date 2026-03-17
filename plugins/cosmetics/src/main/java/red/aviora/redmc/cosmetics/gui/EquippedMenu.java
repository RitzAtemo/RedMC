package red.aviora.redmc.cosmetics.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EquippedMenu implements InventoryHolder {

    private static final int SIZE = 54;
    static final int[] SLOT_POSITIONS = {10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23};
    static final CosmeticSlot[] SLOT_ORDER = {
        CosmeticSlot.TRAIL, CosmeticSlot.HEAD, CosmeticSlot.BACK,
        CosmeticSlot.FEET, CosmeticSlot.ORBIT, CosmeticSlot.AURA,
        CosmeticSlot.WINGS, CosmeticSlot.CROWN, CosmeticSlot.HALO,
        CosmeticSlot.SHOULDER_LEFT, CosmeticSlot.SHOULDER_RIGHT
    };
    private static final int BACK_POSITION = 45;

    private final UUID playerUuid;
    private final Inventory inventory;

    public EquippedMenu(Player player) {
        this.playerUuid = player.getUniqueId();
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.equipped-title"));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        PlayerCosmetics cosmetics = plugin.getPlayerCosmeticsManager().get(player);
        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        for (int i = 0; i < SLOT_ORDER.length; i++) {
            CosmeticSlot slot = SLOT_ORDER[i];
            String equipped = cosmetics != null ? cosmetics.getEquipped(slot) : null;
            String slotName = plugin.getLocaleManager().getMessage(player,
                "cosmetics.slot." + slot.name().toLowerCase());

            String nameKey = equipped != null
                ? "cosmetics.gui.equipped-slot-equipped-name"
                : "cosmetics.gui.equipped-slot-empty-name";
            Component name = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, nameKey).replace("%slot%", slotName));

            List<Component> lore = new ArrayList<>();
            if (equipped != null) {
                lore.add(ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.equipped-slot-equipped-lore")
                        .replace("%template%", equipped)));
            } else {
                lore.add(ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.equipped-slot-empty-lore")));
            }
            lore.add(Component.empty());
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.equipped-slot-click")));

            ItemStack item = MainMenu.buildItem(MainMenu.slotMaterial(slot), name, lore);
            if (equipped != null) item.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
            inventory.setItem(SLOT_POSITIONS[i], item);
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));
    }

    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
