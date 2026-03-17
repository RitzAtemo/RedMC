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

public class TemplateMenu implements InventoryHolder {

    private static final int SIZE = 54;
    static final int INFO_POSITION = 13;
    static final int EDIT_POSITION = 20;
    static final int DELETE_POSITION = 22;
    static final int EXPORT_POSITION = 24;
    static final int BACK_POSITION = 45;

    private final UUID playerUuid;
    private final String templateName;
    private final Inventory inventory;

    public TemplateMenu(Player player, String templateName) {
        this.playerUuid = player.getUniqueId();
        this.templateName = templateName;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-title")
                .replace("%name%", templateName));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        CosmeticTemplate template = plugin.getTemplateManager().get(playerUuid, templateName);
        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        if (template != null) {
            String slotName = plugin.getLocaleManager().getMessage(player,
                "cosmetics.slot." + template.getSlot().name().toLowerCase());
            Component infoName = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-info-name"));
            List<Component> infoLore = new ArrayList<>();
            infoLore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-info-lore-slot")
                    .replace("%slot%", slotName)));
            infoLore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-info-lore-author")
                    .replace("%author%", template.getAuthor())));
            infoLore.add(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-info-lore-layers")
                    .replace("%count%", String.valueOf(template.getLayers().size()))));
            inventory.setItem(INFO_POSITION, MainMenu.buildItem(Material.PAPER, infoName, infoLore));

            if (player.hasPermission("redmc.cosmetics.edit")) {
                Component editName = ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-edit-name"));
                List<Component> editLore = List.of(ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-edit-lore")));
                inventory.setItem(EDIT_POSITION, MainMenu.buildItem(Material.WRITABLE_BOOK, editName, editLore));
            }

            if (player.hasPermission("redmc.cosmetics.delete")) {
                Component deleteName = ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-delete-name"));
                List<Component> deleteLore = List.of(ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-delete-lore")));
                inventory.setItem(DELETE_POSITION, MainMenu.buildItem(Material.BARRIER, deleteName, deleteLore));
            }

            if (player.hasPermission("redmc.cosmetics.export")) {
                Component exportName = ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-export-name"));
                List<Component> exportLore = List.of(ApiUtils.formatText(
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.template-menu-export-lore")));
                inventory.setItem(EXPORT_POSITION, MainMenu.buildItem(Material.ENDER_CHEST, exportName, exportLore));
            }
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));
    }

    public String getTemplateName() { return templateName; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
