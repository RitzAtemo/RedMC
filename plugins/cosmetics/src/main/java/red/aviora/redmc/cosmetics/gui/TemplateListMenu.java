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
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TemplateListMenu implements InventoryHolder {

    private static final int SIZE = 54;
    static final int PAGE_SIZE = 28;
    static final int[] TEMPLATE_POSITIONS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };
    private static final int BACK_POSITION = 45;
    private static final int PREV_POSITION = 46;
    private static final int CREATE_POSITION = 48;
    private static final int IMPORT_POSITION = 50;
    private static final int NEXT_POSITION = 52;

    private final UUID playerUuid;
    private int page;
    private final Inventory inventory;

    public TemplateListMenu(Player player, int page) {
        this.playerUuid = player.getUniqueId();
        this.page = page;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-list-title"));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        List<CosmeticTemplate> templates = new ArrayList<>(plugin.getTemplateManager().getAll(playerUuid));
        templates.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        int totalPages = Math.max(1, (int) Math.ceil((double) templates.size() / PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, templates.size());
        for (int i = start; i < end; i++) {
            inventory.setItem(TEMPLATE_POSITIONS[i - start], buildTemplateItem(player, templates.get(i)));
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));

        if (player.hasPermission("redmc.cosmetics.create")) {
            Component createName = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-list-create-name"));
            List<Component> createLore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-list-create-lore")));
            inventory.setItem(CREATE_POSITION, MainMenu.buildItem(Material.EMERALD, createName, createLore));
        }

        if (player.hasPermission("redmc.cosmetics.import")) {
            Component importName = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-list-import-name"));
            List<Component> importLore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-list-import-lore")));
            inventory.setItem(IMPORT_POSITION, MainMenu.buildItem(Material.HOPPER, importName, importLore));
        }

        if (page > 0) {
            Component prevName = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.prev-page-name"));
            List<Component> prevLore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.page-lore")
                    .replace("%page%", String.valueOf(page + 1))
                    .replace("%total%", String.valueOf(totalPages))));
            inventory.setItem(PREV_POSITION, MainMenu.buildItem(Material.ARROW, prevName, prevLore));
        }

        if (page < totalPages - 1) {
            Component nextName = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.next-page-name"));
            List<Component> nextLore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.page-lore")
                    .replace("%page%", String.valueOf(page + 1))
                    .replace("%total%", String.valueOf(totalPages))));
            inventory.setItem(NEXT_POSITION, MainMenu.buildItem(Material.ARROW, nextName, nextLore));
        }
    }

    private ItemStack buildTemplateItem(Player player, CosmeticTemplate template) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String slotName = plugin.getLocaleManager().getMessage(player,
            "cosmetics.slot." + template.getSlot().name().toLowerCase());
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-name")
                .replace("%name%", template.getName()));
        List<Component> lore = new ArrayList<>();
        if (!template.getDescription().isEmpty()) {
            lore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-description")
                    .replace("%description%", template.getDescription())));
        }
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-info-lore-slot")
                .replace("%slot%", slotName)));
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-lore-layers")
                .replace("%count%", String.valueOf(template.getLayers().size()))));
        lore.add(Component.empty());
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-list-click")));
        return MainMenu.buildItem(Material.BOOK, name, lore);
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public int getPage() { return page; }

    @Override
    public Inventory getInventory() { return inventory; }
}
