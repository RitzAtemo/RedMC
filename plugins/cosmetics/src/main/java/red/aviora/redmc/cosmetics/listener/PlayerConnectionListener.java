package red.aviora.redmc.cosmetics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CosmeticsPlugin.getInstance().getPlayerCosmeticsManager().onJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CosmeticsPlugin.getInstance().getPlayerCosmeticsManager().onQuit(event.getPlayer());
        CosmeticsPlugin.getInstance().getCosmeticRenderer().getTrailTracker().remove(event.getPlayer());
    }
}
