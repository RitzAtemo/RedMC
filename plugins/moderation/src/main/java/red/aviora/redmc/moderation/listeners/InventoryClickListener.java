package red.aviora.redmc.moderation.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import red.aviora.redmc.moderation.gui.HistoryGui;
import red.aviora.redmc.moderation.gui.HistoryHolder;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof HistoryHolder historyHolder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 45) {
                int newPage = historyHolder.getPage() - 1;
                if (newPage >= 0) {
                    player.closeInventory();
                    HistoryGui.open(player, historyHolder.getTargetUuid(), newPage);
                }
            } else if (slot == 49) {
                player.closeInventory();
            } else if (slot == 53) {
                int newPage = historyHolder.getPage() + 1;
                player.closeInventory();
                HistoryGui.open(player, historyHolder.getTargetUuid(), newPage);
            }
        }
    }
}
