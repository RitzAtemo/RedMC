package red.aviora.redmc.teleport.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        boolean includeDeaths = plugin.getConfigManager().getBoolean("config.yml", "back.include-death", true);
        if (!includeDeaths) return;
        plugin.getBackManager().push(event.getEntity(), event.getEntity().getLocation());
    }
}
