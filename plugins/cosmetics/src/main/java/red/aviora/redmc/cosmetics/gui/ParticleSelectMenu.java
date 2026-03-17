package red.aviora.redmc.cosmetics.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ParticleSelectMenu implements InventoryHolder {

    public enum Mode { ADD_LAYER, SET_PARTICLE }

    private static final int SIZE = 54;
    static final int PAGE_SIZE = 45;
    static final int BACK_POSITION = 45;
    static final int PREV_POSITION = 46;
    static final int NEXT_POSITION = 52;

    static final List<Particle> PARTICLES = Arrays.asList(Particle.values());

    private final UUID playerUuid;
    private final String templateName;
    private final int layerIndex;
    private final Mode mode;
    private int page;
    private final Inventory inventory;

    public ParticleSelectMenu(Player player, String templateName, int layerIndex, Mode mode, int page) {
        this.playerUuid = player.getUniqueId();
        this.templateName = templateName;
        this.layerIndex = layerIndex;
        this.mode = mode;
        this.page = page;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.particle-select-title"));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        int totalPages = Math.max(1, (int) Math.ceil((double) PARTICLES.size() / PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));

        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, PARTICLES.size());
        for (int i = start; i < end; i++) {
            Particle p = PARTICLES.get(i);
            Component name = ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.select-item-name")
                    .replace("%value%", p.name()));
            List<Component> lore = List.of(ApiUtils.formatText(
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.select-item-lore")));
            inventory.setItem(i - start, MainMenu.buildItem(particleMaterial(p), name, lore));
        }

        Component backName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-name"));
        List<Component> backLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.back-lore")));
        inventory.setItem(BACK_POSITION, MainMenu.buildItem(Material.ARROW, backName, backLore));

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

    static Material particleMaterial(Particle p) {
        return switch (p.name()) {
            case "FLAME", "SOUL_FIRE_FLAME", "SMALL_FLAME" -> Material.BLAZE_POWDER;
            case "DUST", "DUST_COLOR_TRANSITION" -> Material.REDSTONE;
            case "SNOWFLAKE", "SNOW_SHOVEL" -> Material.SNOWBALL;
            case "DRIPPING_WATER", "FALLING_WATER", "LANDING_WATER",
                 "SPLASH", "BUBBLE_POP", "BUBBLE_COLUMN_UP" -> Material.WATER_BUCKET;
            case "EXPLOSION_NORMAL", "EXPLOSION_LARGE", "EXPLOSION_HUGE",
                 "FLASH", "EXPLOSION", "LARGE_EXPLOSION", "HUGE_EXPLOSION" -> Material.FIRE_CHARGE;
            case "HEART" -> Material.POPPY;
            case "HAPPY_VILLAGER", "VILLAGER_HAPPY" -> Material.EMERALD;
            case "ANGRY_VILLAGER", "VILLAGER_ANGRY" -> Material.RED_MUSHROOM;
            case "WITCH" -> Material.POTION;
            case "MAGIC_CRIT", "CRIT", "ENCHANTED_HIT" -> Material.NETHER_STAR;
            case "SMOKE_NORMAL", "SMOKE_LARGE", "CAMPFIRE_COSY_SMOKE",
                 "CAMPFIRE_SIGNAL_SMOKE", "SMALL_SMOKE" -> Material.COAL;
            case "NOTE" -> Material.NOTE_BLOCK;
            case "LAVA" -> Material.LAVA_BUCKET;
            case "PORTAL" -> Material.ENDER_EYE;
            case "END_ROD", "GLOW" -> Material.END_ROD;
            case "TOTEM_OF_UNDYING" -> Material.TOTEM_OF_UNDYING;
            case "CLOUD" -> Material.WHITE_WOOL;
            case "ENCHANTMENT_TABLE", "ENCHANTING_GLYPHS" -> Material.ENCHANTING_TABLE;
            case "DRIPPING_LAVA", "FALLING_LAVA", "LANDING_LAVA" -> Material.LAVA_BUCKET;
            case "FALLING_HONEY", "DRIPPING_HONEY", "LANDING_HONEY" -> Material.HONEY_BOTTLE;
            case "ASH" -> Material.GRAY_DYE;
            case "CHERRY_LEAVES" -> Material.CHERRY_LEAVES;
            case "EGG_CRACK" -> Material.EGG;
            default -> Material.GLOWSTONE_DUST;
        };
    }

    public String getTemplateName() { return templateName; }
    public int getLayerIndex() { return layerIndex; }
    public Mode getMode() { return mode; }
    public int getPage() { return page; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
