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

import java.util.List;
import java.util.UUID;

public class CreateSlotSelectMenu implements InventoryHolder {

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
    private final String pendingName;
    private final Inventory inventory;

    public CreateSlotSelectMenu(Player player, String pendingName) {
        this.playerUuid = player.getUniqueId();
        this.pendingName = pendingName;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.create-slot-title"));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        for (int i = 0; i < SLOT_ORDER.length; i++) {
            CosmeticSlot slot = SLOT_ORDER[i];
            String slotName = plugin.getLocaleManager().getMessage(player,
                "cosmetics.slot." + slot.name().toLowerCase());
            Component name = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.create-slot-item-name")
                    .replace("%slot%", slotName));
            List<Component> lore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.create-slot-item-lore")));
            inventory.setItem(SLOT_POSITIONS[i], MainMenu.buildItem(MainMenu.slotMaterial(slot), name, lore));
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));
    }

    public String getPendingName() { return pendingName; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
