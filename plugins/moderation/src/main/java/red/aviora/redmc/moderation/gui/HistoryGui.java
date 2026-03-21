package red.aviora.redmc.moderation.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.models.ModerationActionType;
import red.aviora.redmc.moderation.utils.DurationParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HistoryGui {

    private static final int PAGE_SIZE = 45;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void open(Player viewer, UUID targetUuid, String targetName, int page) {
        ModerationPlugin plugin = ModerationPlugin.getInstance();
        LocaleManager locale = plugin.getLocaleManager();

        List<ModerationAction> history = new ArrayList<>(
            plugin.getWarnManager().getActionsMap().getOrDefault(targetUuid, new ArrayList<>())
        );

        int totalPages = Math.max(1, (int) Math.ceil(history.size() / (double) PAGE_SIZE));
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        String titleRaw = locale.getMessage(viewer, "gui.history.title");
        titleRaw = titleRaw.replace("%player%", targetName);
        Component title = ApiUtils.getMM().deserialize(titleRaw);

        HistoryHolder holder = new HistoryHolder(targetUuid, targetName, page);
        Inventory inv = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inv);

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.empty());
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, filler.clone());
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, history.size());
        for (int i = start; i < end; i++) {
            int slot = i - start;
            inv.setItem(slot, buildActionItem(viewer, history.get(i), locale));
        }

        // Info item slot 48
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        String infoNameRaw = locale.getMessage(viewer, "gui.history.info-name").replace("%player%", targetName);
        infoMeta.displayName(ApiUtils.getMM().deserialize(infoNameRaw));
        String infoLoreRaw = locale.getMessage(viewer, "gui.history.info-lore").replace("%total%", String.valueOf(history.size()));
        infoMeta.lore(List.of(ApiUtils.getMM().deserialize(infoLoreRaw)));
        infoItem.setItemMeta(infoMeta);
        inv.setItem(48, infoItem);

        // Prev page slot 45
        if (page > 0) {
            inv.setItem(45, buildNavItem(Material.ARROW, locale.getMessage(viewer, "gui.history.prev-page")));
        }

        // Close slot 49
        inv.setItem(49, buildNavItem(Material.BARRIER, locale.getMessage(viewer, "gui.history.close")));

        // Next page slot 53
        if (page < totalPages - 1) {
            inv.setItem(53, buildNavItem(Material.ARROW, locale.getMessage(viewer, "gui.history.next-page")));
        }

        viewer.openInventory(inv);
    }

    private static ItemStack buildActionItem(Player viewer, ModerationAction action, LocaleManager locale) {
        Material mat;
        String nameKey;
        switch (action.getType()) {
            case WARN -> { mat = Material.PAPER; nameKey = "gui.history.warn-name"; }
            case MUTE -> { mat = Material.BOOK; nameKey = "gui.history.mute-name"; }
            case BAN -> { mat = Material.BARRIER; nameKey = "gui.history.ban-name"; }
            default -> { mat = Material.PAPER; nameKey = "gui.history.warn-name"; }
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ApiUtils.getMM().deserialize(locale.getMessage(viewer, nameKey)));

        List<Component> lore = new ArrayList<>();
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.history.lore-reason").replace("%reason%", action.getReason())));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.history.lore-staff").replace("%staff%", action.getStaffName())));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.history.lore-date").replace("%date%", DATE_FORMAT.format(new Date(action.getTimestamp())))));

        String durationStr = action.getType() == ModerationActionType.WARN ? "N/A" : DurationParser.format(action.getDuration());
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.history.lore-duration").replace("%duration%", durationStr)));

        String statusKey;
        if (!action.isActive()) {
            statusKey = "gui.history.lore-status-pardoned";
        } else if (action.isExpired()) {
            statusKey = "gui.history.lore-status-expired";
        } else {
            statusKey = "gui.history.lore-status-active";
        }
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, statusKey)));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack buildNavItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ApiUtils.getMM().deserialize(name));
        item.setItemMeta(meta);
        return item;
    }
}
