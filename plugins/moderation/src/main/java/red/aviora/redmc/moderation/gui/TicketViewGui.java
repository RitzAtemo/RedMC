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
import red.aviora.redmc.moderation.models.Ticket;
import red.aviora.redmc.moderation.models.TicketReply;
import red.aviora.redmc.moderation.models.TicketStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketViewGui {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void open(Player viewer, Ticket ticket) {
        ModerationPlugin plugin = ModerationPlugin.getInstance();
        LocaleManager locale = plugin.getLocaleManager();

        String titleRaw = locale.getMessage(viewer, "gui.tickets.title-view").replace("%id%", ticket.getShortId());
        Component title = ApiUtils.getMM().deserialize(titleRaw);

        TicketViewHolder holder = new TicketViewHolder(ticket.getId());
        Inventory inv = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inv);

        ItemStack filler = TicketListGui.buildFiller();
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, filler.clone());
        }

        // Slot 4: ticket info item
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.displayName(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.info-name")));
        List<Component> infoLore = new ArrayList<>();
        infoLore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-author").replace("%author%", ticket.getAuthorName())));
        infoLore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-message").replace("%message%", ticket.getMessage())));
        infoLore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-date").replace("%date%", DATE_FORMAT.format(new Date(ticket.getTimestamp())))));
        infoLore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-status").replace("%status%", ticket.getStatus().name())));
        infoMeta.lore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inv.setItem(4, infoItem);

        // Slots 9-35: up to 18 replies
        List<TicketReply> replies = ticket.getReplies();
        int maxReplies = Math.min(replies.size(), 18);
        for (int i = 0; i < maxReplies; i++) {
            int slot = 9 + i;
            inv.setItem(slot, buildReplyItem(viewer, replies.get(i), locale));
        }

        // Slot 45: back button
        inv.setItem(45, TicketListGui.buildNavItem(Material.ARROW, locale.getMessage(viewer, "gui.tickets.back")));

        // Slot 49: close ticket button
        boolean isOpen = ticket.getStatus() == TicketStatus.OPEN;
        if (isOpen && viewer.hasPermission("redmc.tickets")) {
            inv.setItem(49, TicketListGui.buildNavItem(Material.BARRIER, locale.getMessage(viewer, "gui.tickets.close-ticket")));
        } else {
            inv.setItem(49, TicketListGui.buildNavItem(Material.RED_STAINED_GLASS_PANE, locale.getMessage(viewer, "gui.tickets.already-closed")));
        }

        // Slot 53: reply hint
        ItemStack hintItem = new ItemStack(Material.FEATHER);
        ItemMeta hintMeta = hintItem.getItemMeta();
        hintMeta.displayName(Component.empty());
        String hintLoreRaw = locale.getMessage(viewer, "gui.tickets.reply-hint").replace("%id%", ticket.getShortId());
        hintMeta.lore(List.of(ApiUtils.getMM().deserialize(hintLoreRaw)));
        hintItem.setItemMeta(hintMeta);
        inv.setItem(53, hintItem);

        viewer.openInventory(inv);
    }

    private static ItemStack buildReplyItem(Player viewer, TicketReply reply, LocaleManager locale) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        String nameRaw = locale.getMessage(viewer, "gui.tickets.reply-name").replace("%staff%", reply.getStaffName());
        meta.displayName(ApiUtils.getMM().deserialize(nameRaw));

        List<Component> lore = new ArrayList<>();
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.reply-lore-message").replace("%message%", reply.getMessage())));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.reply-lore-date").replace("%date%", DATE_FORMAT.format(new Date(reply.getTimestamp())))));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
