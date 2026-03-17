package red.aviora.redmc.cosmetics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.gui.ChatInputManager;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        plugin.getTemplateManager().loadForPlayer(event.getPlayer().getUniqueId());
        plugin.getPlayerCosmeticsManager().onJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        plugin.getPlayerCosmeticsManager().onQuit(event.getPlayer());
        plugin.getCosmeticRenderer().getTrailTracker().remove(event.getPlayer());
        plugin.getTemplateManager().unloadForPlayer(event.getPlayer().getUniqueId());
        ChatInputManager.cancel(event.getPlayer().getUniqueId());
    }
}
