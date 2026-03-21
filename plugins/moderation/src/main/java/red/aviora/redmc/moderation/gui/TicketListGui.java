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
import red.aviora.redmc.moderation.models.TicketStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketListGui {

    private static final int PAGE_SIZE = 45;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void open(Player viewer, int page, boolean showOnlyOpen) {
        ModerationPlugin plugin = ModerationPlugin.getInstance();
        LocaleManager locale = plugin.getLocaleManager();

        List<Ticket> allTickets = showOnlyOpen ? plugin.getTicketManager().getOpen() : plugin.getTicketManager().getAll();

        int totalPages = Math.max(1, (int) Math.ceil(allTickets.size() / (double) PAGE_SIZE));
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        Component title = ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.title"));

        TicketListHolder holder = new TicketListHolder(page, showOnlyOpen);
        Inventory inv = Bukkit.createInventory(holder, 54, title);
        holder.setInventory(inv);

        ItemStack filler = buildFiller();
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, filler.clone());
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allTickets.size());
        for (int i = start; i < end; i++) {
            int slot = i - start;
            inv.setItem(slot, buildTicketItem(viewer, allTickets.get(i), locale));
        }

        // Prev page slot 45
        if (page > 0) {
            inv.setItem(45, buildNavItem(Material.ARROW, locale.getMessage(viewer, "gui.tickets.prev-page")));
        }

        // Filter toggle slot 49
        String filterLabel = showOnlyOpen ? "OPEN" : "ALL";
        String filterRaw = locale.getMessage(viewer, "gui.tickets.filter").replace("%filter%", filterLabel);
        inv.setItem(49, buildNavItem(Material.HOPPER, filterRaw));

        // Next page slot 53
        if (page < totalPages - 1) {
            inv.setItem(53, buildNavItem(Material.ARROW, locale.getMessage(viewer, "gui.tickets.next-page")));
        }

        viewer.openInventory(inv);
    }

    public static ItemStack buildTicketItem(Player viewer, Ticket ticket, LocaleManager locale) {
        boolean isOpen = ticket.getStatus() == TicketStatus.OPEN;
        Material mat = isOpen ? Material.WRITABLE_BOOK : Material.WRITTEN_BOOK;
        String nameKey = isOpen ? "gui.tickets.open-name" : "gui.tickets.closed-name";
        String nameRaw = locale.getMessage(viewer, nameKey).replace("%id%", ticket.getShortId());

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ApiUtils.getMM().deserialize(nameRaw));

        String msgSnippet = ticket.getMessage().length() > 50
            ? ticket.getMessage().substring(0, 50) + "..."
            : ticket.getMessage();

        List<Component> lore = new ArrayList<>();
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-author").replace("%author%", ticket.getAuthorName())));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-message").replace("%message%", msgSnippet)));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-date").replace("%date%", DATE_FORMAT.format(new Date(ticket.getTimestamp())))));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-status").replace("%status%", ticket.getStatus().name())));
        lore.add(ApiUtils.getMM().deserialize(locale.getMessage(viewer, "gui.tickets.lore-replies").replace("%count%", String.valueOf(ticket.getReplies().size()))));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack buildFiller() {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.displayName(Component.empty());
        filler.setItemMeta(meta);
        return filler;
    }

    static ItemStack buildNavItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ApiUtils.getMM().deserialize(name));
        item.setItemMeta(meta);
        return item;
    }
}
