package red.aviora.redmc.teleport.managers;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.SerializableLocation;
import red.aviora.redmc.teleport.models.Warp;
import red.aviora.redmc.teleport.utils.TeleportDataStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WarpManager {

    private final Map<String, Warp> warps = new HashMap<>();

    public void loadAll() {
        warps.clear();
        warps.putAll(JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage().loadWarps());
    }

    public void saveAll() {
        JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage().saveWarps(warps);
    }

    public void createWarp(String id, Location location) {
        warps.put(id, new Warp(id, SerializableLocation.fromBukkitLocation(location)));
        saveAll();
    }

    public void deleteWarp(String id) {
        warps.remove(id);
        saveAll();
    }

    public Warp getWarp(String id) {
        return warps.get(id);
    }

    public Collection<Warp> getWarps() {
        return Collections.unmodifiableCollection(warps.values());
    }

    public boolean exists(String id) {
        return warps.containsKey(id);
    }
}
