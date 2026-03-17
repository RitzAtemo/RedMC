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
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SlotMenu implements InventoryHolder {

    private static final int SIZE = 54;
    private static final int PAGE_SIZE = 28;

    private static final int[] TEMPLATE_POSITIONS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };
    private static final int BACK_POSITION = 45;
    private static final int PREV_POSITION = 47;
    private static final int UNEQUIP_POSITION = 49;
    private static final int NEXT_POSITION = 51;

    private final UUID playerUuid;
    private final CosmeticSlot slot;
    private int page;
    private final Inventory inventory;

    public SlotMenu(Player player, CosmeticSlot slot, int page) {
        this.playerUuid = player.getUniqueId();
        this.slot = slot;
        this.page = page;

        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String slotName = plugin.getLocaleManager().getMessage(player,
            "cosmetics.slot." + slot.name().toLowerCase());
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.slot-title")
                .replace("%slot%", slotName));

        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        PlayerCosmetics cosmetics = plugin.getPlayerCosmeticsManager().get(player);
        String equippedName = cosmetics != null ? cosmetics.getEquipped(slot) : null;

        List<CosmeticTemplate> templates = plugin.getTemplateManager().getForSlot(playerUuid, slot);
        int totalPages = Math.max(1, (int) Math.ceil((double) templates.size() / PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, border);
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, templates.size());
        for (int i = start; i < end; i++) {
            CosmeticTemplate template = templates.get(i);
            int pos = TEMPLATE_POSITIONS[i - start];
            boolean isEquipped = template.getName().equalsIgnoreCase(equippedName);
            inventory.setItem(pos, buildTemplateItem(player, template, isEquipped));
        }

        inventory.setItem(BACK_POSITION, buildBackItem(player));

        if (equippedName != null) {
            inventory.setItem(UNEQUIP_POSITION, buildUnequipItem(player));
        } else {
            inventory.setItem(UNEQUIP_POSITION, border);
        }

        if (page > 0) {
            inventory.setItem(PREV_POSITION, buildPageItem(player, true, page, totalPages));
        }
        if (page < totalPages - 1) {
            inventory.setItem(NEXT_POSITION, buildPageItem(player, false, page, totalPages));
        }
    }

    private ItemStack buildTemplateItem(Player player, CosmeticTemplate template, boolean isEquipped) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

        String nameKey = isEquipped ? "cosmetics.gui.template-name-equipped" : "cosmetics.gui.template-name";
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, nameKey)
                .replace("%name%", template.getName()));

        List<Component> lore = new ArrayList<>();

        if (!template.getDescription().isEmpty()) {
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-description")
                    .replace("%description%", template.getDescription())));
        }
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-layers")
                .replace("%count%", String.valueOf(template.getLayers().size()))));
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-author")
                .replace("%author%", template.getAuthor())));
        lore.add(Component.empty());
        if (isEquipped) {
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-equipped")));
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-click-unequip")));
        } else {
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-click-equip")));
        }

        ItemStack item = MainMenu.buildItem(Material.BOOK, name, lore);
        if (isEquipped) {
            item.editMeta(meta -> meta.setEnchantmentGlintOverride(true));
        }
        return item;
    }

    private ItemStack buildBackItem(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> lore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        return MainMenu.buildItem(Material.ARROW, name, lore);
    }

    private ItemStack buildUnequipItem(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.unequip-name"));
        List<Component> lore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.unequip-lore")));
        return MainMenu.buildItem(Material.BARRIER, name, lore);
    }

    private ItemStack buildPageItem(Player player, boolean prev, int currentPage, int totalPages) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String nameKey = prev ? "cosmetics.gui.prev-page-name" : "cosmetics.gui.next-page-name";
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, nameKey));
        List<Component> lore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.page-lore")
                .replace("%page%", String.valueOf(currentPage + 1))
                .replace("%total%", String.valueOf(totalPages))));
        return MainMenu.buildItem(Material.ARROW, name, lore);
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public CosmeticSlot getSlot() { return slot; }
    public int getPage() { return page; }

    @Override
    public Inventory getInventory() { return inventory; }
}
