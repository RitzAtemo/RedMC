package red.aviora.redmc.cosmetics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        PlayerCosmetics cosmetics = CosmeticsPlugin.getInstance()
            .getPlayerCosmeticsManager().get(event.getPlayer());
        if (cosmetics == null || !cosmetics.isVisible()) return;
        if (!cosmetics.hasEquipped(CosmeticSlot.TRAIL)) return;
        CosmeticsPlugin.getInstance().getCosmeticRenderer()
            .getTrailTracker().onMove(event.getPlayer(), event.getTo());
    }
}
