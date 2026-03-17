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

import java.util.List;
import java.util.UUID;

public class DeleteConfirmMenu implements InventoryHolder {

    private static final int SIZE = 27;
    static final int CONFIRM_POSITION = 11;
    static final int CANCEL_POSITION = 15;

    private final UUID playerUuid;
    private final String templateName;
    private final Inventory inventory;

    public DeleteConfirmMenu(Player player, String templateName) {
        this.playerUuid = player.getUniqueId();
        this.templateName = templateName;
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Component title = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.delete-confirm-title")
                .replace("%name%", templateName));
        this.inventory = Bukkit.createInventory(this, SIZE, title);
        populate(player);
    }

    private void populate(Player player) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        ItemStack border = MainMenu.buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty(), List.of());
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, border);

        Component yesName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.delete-confirm-yes-name"));
        List<Component> yesLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.delete-confirm-yes-lore")));
        inventory.setItem(CONFIRM_POSITION, MainMenu.buildItem(Material.RED_CONCRETE, yesName, yesLore));

        Component noName = ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.delete-confirm-no-name"));
        List<Component> noLore = List.of(ApiUtils.formatText(
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.delete-confirm-no-lore")));
        inventory.setItem(CANCEL_POSITION, MainMenu.buildItem(Material.GREEN_CONCRETE, noName, noLore));
    }

    public String getTemplateName() { return templateName; }
    public UUID getPlayerUuid() { return playerUuid; }

    @Override
    public Inventory getInventory() { return inventory; }
}
