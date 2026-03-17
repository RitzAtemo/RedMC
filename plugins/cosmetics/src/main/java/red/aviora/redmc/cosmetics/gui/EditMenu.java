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
import red.aviora.redmc.cosmetics.model.ParticleLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditMenu implements InventoryHolder {

    private static final int SIZE = 54;
    static final int[] LAYER_POSITIONS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };
    static final int BACK_POSITION = 45;
    static final int ADD_LAYER_POSITION = 53;

    private final UUID playerUuid;
    private final String templateName;
    private final Inventory inventory;

    public EditMenu(Player player, String templateName) {
        this.playerUuid = player.getUniqueId();
        this.templateName = templateName;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-title")
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
            List<ParticleLayer> layers = template.getLayers();
            int count = Math.min(layers.size(), LAYER_POSITIONS.length);
            for (int i = 0; i < count; i++) {
                inventory.setItem(LAYER_POSITIONS[i], buildLayerItem(player, layers.get(i), i));
            }
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));

        Component addName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-add-layer-name"));
        List<Component> addLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-add-layer-lore")));
        inventory.setItem(ADD_LAYER_POSITION, MainMenu.buildItem(Material.LIME_DYE, addName, addLore));
    }

    private ItemStack buildLayerItem(Player player, ParticleLayer layer, int index) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-layer-name")
                .replace("%index%", String.valueOf(index)));
        List<Component> lore = new ArrayList<>();
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-layer-lore-particle")
                .replace("%particle%", layer.getParticle())));
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-layer-lore-shape")
                .replace("%shape%", layer.getShape().name())));
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-layer-lore-count")
                .replace("%count%", String.valueOf(layer.getCount()))));
        lore.add(Component.empty());
        lore.add(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.edit-layer-click")));
        return MainMenu.buildItem(Material.FIREWORK_STAR, name, lore);
    }

    public String getTemplateName() { return templateName; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
