package red.aviora.redmc.cosmetics.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainMenu implements InventoryHolder {

    private static final int SIZE = 54;

    private static final int[] SLOT_POSITIONS = {10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23};
    private static final CosmeticSlot[] SLOT_ORDER = {
        CosmeticSlot.TRAIL, CosmeticSlot.HEAD, CosmeticSlot.BACK,
        CosmeticSlot.FEET, CosmeticSlot.ORBIT, CosmeticSlot.AURA,
        CosmeticSlot.WINGS, CosmeticSlot.CROWN, CosmeticSlot.HALO,
        CosmeticSlot.SHOULDER_LEFT, CosmeticSlot.SHOULDER_RIGHT
    };
    private static final int TOGGLE_POSITION = 49;
    private static final int EQUIPPED_POSITION = 47;
    private static final int MANAGE_POSITION = 51;

    private final UUID playerUuid;
    private final Inventory inventory;

    public MainMenu(Player player) {
        this.playerUuid = player.getUniqueId();
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

        Component title = ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "cosmetics.gui.main-title"));
        this.inventory = Bukkit.createInventory(this, SIZE, title);

        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        PlayerCosmetics cosmetics = plugin.getPlayerCosmeticsManager().get(player);
        ItemStack border = buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());

        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, border);
        }

        for (int i = 0; i < SLOT_ORDER.length; i++) {
            CosmeticSlot slot = SLOT_ORDER[i];
            String equipped = cosmetics != null ? cosmetics.getEquipped(slot) : null;
            inventory.setItem(SLOT_POSITIONS[i], buildSlotItem(player, slot, equipped));
        }

        inventory.setItem(TOGGLE_POSITION, buildToggleItem(player, cosmetics));

        Component equippedName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.equipped-button-name"));
        List<Component> equippedLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.equipped-button-lore")));
        inventory.setItem(EQUIPPED_POSITION, buildItem(Material.EMERALD, equippedName, equippedLore));

        if (player.hasPermission("redmc.cosmetics.create")
                || player.hasPermission("redmc.cosmetics.edit")
                || player.hasPermission("redmc.cosmetics.delete")
                || player.hasPermission("redmc.cosmetics.export")
                || player.hasPermission("redmc.cosmetics.import")) {
            Component manageName = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.manage-button-name"));
            List<Component> manageLore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.manage-button-lore")));
            inventory.setItem(MANAGE_POSITION, buildItem(Material.ANVIL, manageName, manageLore));
        }
    }

    private ItemStack buildSlotItem(Player player, CosmeticSlot slot, String equippedTemplate) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String slotDisplayName = plugin.getLocaleManager().getMessage(player,
            "cosmetics.slot." + slot.name().toLowerCase());

        boolean isEquipped = equippedTemplate != null;

        String nameKey = isEquipped ? "cosmetics.gui.slot-name-equipped" : "cosmetics.gui.slot-name-empty";
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, nameKey)
                .replace("%slot%", slotDisplayName));

        List<Component> lore = new ArrayList<>();
        if (isEquipped) {
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.slot-lore-equipped")
                    .replace("%template%", equippedTemplate)));
        } else {
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.slot-lore-empty")));
        }
        lore.add(Component.empty());
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.slot-lore-click")));

        ItemStack item = buildItem(slotMaterial(slot), name, lore);
        if (isEquipped) {
            item.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
        }
        return item;
    }

    private ItemStack buildToggleItem(Player player, PlayerCosmetics cosmetics) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        boolean visible = cosmetics == null || cosmetics.isVisible();

        String nameKey = visible ? "cosmetics.gui.toggle-on-name" : "cosmetics.gui.toggle-off-name";
        Component name = ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, nameKey));
        List<Component> lore = List.of(
            ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "cosmetics.gui.toggle-lore"))
        );

        Material mat = visible ? Material.LIME_DYE : Material.GRAY_DYE;
        ItemStack item = buildItem(mat, name, lore);
        if (visible) {
            item.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
        }
        return item;
    }

    static ItemStack buildItem(Material material, Component name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            meta.displayName(name);
            if (!lore.isEmpty()) meta.lore(lore);
            meta.addItemFlags(ItemFlag.values());
        });
        return item;
    }

    static Material slotMaterial(CosmeticSlot slot) {
        return switch (slot) {
            case TRAIL -> Material.FEATHER;
            case HEAD -> Material.CARVED_PUMPKIN;
            case BACK -> Material.ELYTRA;
            case FEET -> Material.LEATHER_BOOTS;
            case ORBIT -> Material.ENDER_EYE;
            case AURA -> Material.NETHER_STAR;
            case WINGS -> Material.PHANTOM_MEMBRANE;
            case CROWN -> Material.GOLDEN_HELMET;
            case HALO -> Material.SUNFLOWER;
            case SHOULDER_LEFT -> Material.FIREWORK_ROCKET;
            case SHOULDER_RIGHT -> Material.FIREWORK_ROCKET;
        };
    }

    public static int getEquippedPosition() { return EQUIPPED_POSITION; }
    public static int getManagePosition() { return MANAGE_POSITION; }
    public static int getTogglePosition() { return TOGGLE_POSITION; }
    public static int[] getSlotPositions() { return SLOT_POSITIONS; }
    public static CosmeticSlot[] getSlotOrder() { return SLOT_ORDER; }

    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
