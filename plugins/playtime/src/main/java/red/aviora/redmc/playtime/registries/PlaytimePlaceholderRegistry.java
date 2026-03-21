package red.aviora.redmc.playtime.registries;

import red.aviora.redmc.placeholders.utils.PlaceholderRegistry;
import red.aviora.redmc.playtime.PlaytimePlugin;
import red.aviora.redmc.playtime.utils.PlaytimeFormatter;

public class PlaytimePlaceholderRegistry {

    public static PlaceholderRegistry generate() {
        PlaceholderRegistry registry = new PlaceholderRegistry();
        PlaytimePlugin plugin = PlaytimePlugin.getInstance();

        registry.set("PlayerPlaytime", player -> {
            if (player == null) return "";
            long seconds = plugin.getPlaytimeManager().getTotalPlaytimeSeconds(player.getUniqueId());
            return PlaytimeFormatter.format(seconds);
        });

        registry.set("PlayerPlaytimeSeconds", player -> {
            if (player == null) return "0";
            return String.valueOf(plugin.getPlaytimeManager().getTotalPlaytimeSeconds(player.getUniqueId()));
        });

        registry.set("PlayerAFK", player -> {
            if (player == null) return "false";
            return String.valueOf(plugin.getAfkManager().isAfk(player.getUniqueId()));
        });

        return registry;
    }
}
