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

import java.util.List;
import java.util.UUID;

public class LayerMenu implements InventoryHolder {

    private static final int SIZE = 54;
    static final int BACK_POSITION = 45;
    static final int REMOVE_POSITION = 53;

    static final int POS_PARTICLE = 10;
    static final int POS_SHAPE = 11;
    static final int POS_COUNT = 12;
    static final int POS_SPEED = 13;
    static final int POS_TICKRATE = 14;
    static final int POS_YOFFSET = 15;
    static final int POS_RADIUS = 19;
    static final int POS_POINTS = 20;
    static final int POS_OFFSETX = 21;
    static final int POS_OFFSETY = 22;
    static final int POS_OFFSETZ = 23;
    static final int POS_COLOR = 28;
    static final int POS_COLORTO = 29;
    static final int POS_DUSTSIZE = 30;

    public enum Prop {
        PARTICLE(POS_PARTICLE), SHAPE(POS_SHAPE), COUNT(POS_COUNT), SPEED(POS_SPEED),
        TICKRATE(POS_TICKRATE), YOFFSET(POS_YOFFSET), RADIUS(POS_RADIUS), POINTS(POS_POINTS),
        OFFSETX(POS_OFFSETX), OFFSETY(POS_OFFSETY), OFFSETZ(POS_OFFSETZ),
        COLOR(POS_COLOR), COLORTO(POS_COLORTO), DUSTSIZE(POS_DUSTSIZE);

        public final int slot;
        Prop(int slot) { this.slot = slot; }

        public static Prop forSlot(int s) {
            for (Prop p : values()) if (p.slot == s) return p;
            return null;
        }
    }

    private final UUID playerUuid;
    private final String templateName;
    private final int layerIndex;
    private final Inventory inventory;

    public LayerMenu(Player player, String templateName, int layerIndex) {
        this.playerUuid = player.getUniqueId();
        this.templateName = templateName;
        this.layerIndex = layerIndex;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-title")
                .replace("%index%", String.valueOf(layerIndex))
                .replace("%name%", templateName));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        CosmeticTemplate template = plugin.getTemplateManager().get(playerUuid, templateName);
        if (template == null) return;
        ParticleLayer layer = template.getLayer(layerIndex);
        if (layer == null) return;

        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        inventory.setItem(POS_PARTICLE, buildProp(player, "cosmetics.gui.layer-label-particle",
            layer.getParticle(), Material.BLAZE_POWDER, true));
        inventory.setItem(POS_SHAPE, buildProp(player, "cosmetics.gui.layer-label-shape",
            layer.getShape().name(), Material.COMPASS, true));
        inventory.setItem(POS_COUNT, buildProp(player, "cosmetics.gui.layer-label-count",
            String.valueOf(layer.getCount()), Material.REPEATER, false));
        inventory.setItem(POS_SPEED, buildProp(player, "cosmetics.gui.layer-label-speed",
            String.valueOf(layer.getSpeed()), Material.SUGAR, false));
        inventory.setItem(POS_TICKRATE, buildProp(player, "cosmetics.gui.layer-label-tickrate",
            String.valueOf(layer.getTickRate()), Material.CLOCK, false));
        inventory.setItem(POS_YOFFSET, buildProp(player, "cosmetics.gui.layer-label-yoffset",
            String.valueOf(layer.getYOffset()), Material.ARROW, false));
        inventory.setItem(POS_RADIUS, buildProp(player, "cosmetics.gui.layer-label-radius",
            String.valueOf(layer.getShapeRadius()), Material.ENDER_EYE, false));
        inventory.setItem(POS_POINTS, buildProp(player, "cosmetics.gui.layer-label-points",
            String.valueOf(layer.getShapePoints()), Material.MAGMA_CREAM, false));
        inventory.setItem(POS_OFFSETX, buildProp(player, "cosmetics.gui.layer-label-offsetx",
            String.valueOf(layer.getOffsetX()), Material.RED_DYE, false));
        inventory.setItem(POS_OFFSETY, buildProp(player, "cosmetics.gui.layer-label-offsety",
            String.valueOf(layer.getOffsetY()), Material.GREEN_DYE, false));
        inventory.setItem(POS_OFFSETZ, buildProp(player, "cosmetics.gui.layer-label-offsetz",
            String.valueOf(layer.getOffsetZ()), Material.BLUE_DYE, false));
        inventory.setItem(POS_COLOR, buildColorProp(player, "cosmetics.gui.layer-label-color",
            layer.getDustColorR(), layer.getDustColorG(), layer.getDustColorB()));
        inventory.setItem(POS_COLORTO, buildColorProp(player, "cosmetics.gui.layer-label-colorto",
            layer.getDustColorToR(), layer.getDustColorToG(), layer.getDustColorToB()));
        inventory.setItem(POS_DUSTSIZE, buildProp(player, "cosmetics.gui.layer-label-dustsize",
            String.valueOf(layer.getDustSize()), Material.AMETHYST_SHARD, false));

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));

        Component removeName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-remove-name"));
        List<Component> removeLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-remove-lore")));
        inventory.setItem(REMOVE_POSITION, MainMenu.buildItem(Material.BARRIER, removeName, removeLore));
    }

    private ItemStack buildProp(Player player, String labelKey, String value, Material mat, boolean isSelect) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String label = plugin.getLocaleManager().getMessage(player, labelKey);
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-prop-name")
                .replace("%label%", label));
        String clickKey = isSelect ? "cosmetics.gui.layer-prop-lore-select" : "cosmetics.gui.layer-prop-lore-click";
        List<Component> lore = List.of(
            ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-prop-lore-value")
                .replace("%value%", value)),
            ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, clickKey))
        );
        return MainMenu.buildItem(mat, name, lore);
    }

    private ItemStack buildColorProp(Player player, String labelKey, int r, int g, int b) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String label = plugin.getLocaleManager().getMessage(player, labelKey);
        Component name = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-prop-name")
                .replace("%label%", label));
        List<Component> lore = List.of(
            ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-prop-lore-value")
                .replace("%value%", r + ", " + g + ", " + b)),
            ApiUtils.formatText(plugin.getLocaleManager().getMessage(player, "cosmetics.gui.layer-prop-lore-click"))
        );
        return MainMenu.buildItem(Material.PINK_DYE, name, lore);
    }

    public String getTemplateName() { return templateName; }
    public int getLayerIndex() { return layerIndex; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
