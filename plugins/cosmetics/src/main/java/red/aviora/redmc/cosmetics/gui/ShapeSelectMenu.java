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
import red.aviora.redmc.cosmetics.model.ParticleShape;

import java.util.List;
import java.util.UUID;

public class ShapeSelectMenu implements InventoryHolder {

    public enum Mode { ADD_LAYER, SET_SHAPE }

    private static final int SIZE = 27;
    static final int BACK_POSITION = 18;
    static final int[] SHAPE_POSITIONS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    static final ParticleShape[] SHAPES = ParticleShape.values();

    private final UUID playerUuid;
    private final String templateName;
    private final int layerIndex;
    private final Mode mode;
    private final String pendingParticle;
    private final Inventory inventory;

    public ShapeSelectMenu(Player player, String templateName, int layerIndex, Mode mode, String pendingParticle) {
        this.playerUuid = player.getUniqueId();
        this.templateName = templateName;
        this.layerIndex = layerIndex;
        this.mode = mode;
        this.pendingParticle = pendingParticle;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.shape-select-title"));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        for (int i = 0; i < SHAPES.length && i < SHAPE_POSITIONS.length; i++) {
            ParticleShape shape = SHAPES[i];
            Component name = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.select-item-name")
                    .replace("%value%", shape.name()));
            List<Component> lore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.select-item-lore")));
            inventory.setItem(SHAPE_POSITIONS[i], MainMenu.buildItem(shapeMaterial(shape), name, lore));
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));
    }

    static Material shapeMaterial(ParticleShape shape) {
        return switch (shape) {
            case POINT -> Material.FLINT;
            case RING -> Material.CLOCK;
            case SPHERE -> Material.SLIME_BALL;
            case SPIRAL -> Material.NAUTILUS_SHELL;
            case DOUBLE_HELIX -> Material.TWISTING_VINES;
            case STAR -> Material.NETHER_STAR;
            case WINGS_SHAPE -> Material.PHANTOM_MEMBRANE;
            case CROWN_SHAPE -> Material.GOLDEN_HELMET;
            case HALO_SHAPE -> Material.SUNFLOWER;
            case RANDOM -> Material.DIRT;
        };
    }

    public String getTemplateName() { return templateName; }
    public int getLayerIndex() { return layerIndex; }
    public Mode getMode() { return mode; }
    public String getPendingParticle() { return pendingParticle; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
