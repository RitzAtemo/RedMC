package red.aviora.redmc.teleport.managers;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.Home;
import red.aviora.redmc.teleport.models.SerializableLocation;
import red.aviora.redmc.teleport.utils.TeleportDataStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final Map<UUID, List<Home>> homes = new ConcurrentHashMap<>();

    public void loadAll() {
        homes.clear();
        homes.putAll(JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage().loadHomes());
    }

    public void saveAll() {
        JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage().saveHomes(homes);
    }

    public List<Home> getHomes(UUID uuid) {
        return Collections.unmodifiableList(homes.getOrDefault(uuid, List.of()));
    }

    public Home getHome(UUID uuid, String name) {
        List<Home> list = homes.get(uuid);
        if (list == null) return null;
        for (Home home : list) {
            if (home.getName().equalsIgnoreCase(name)) return home;
        }
        return null;
    }

    public boolean setHome(UUID uuid, String name, Location location, int limit) {
        List<Home> list = homes.computeIfAbsent(uuid, k -> new ArrayList<>());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equalsIgnoreCase(name)) {
                list.set(i, new Home(uuid, name, SerializableLocation.fromBukkitLocation(location)));
                saveAll();
                return true;
            }
        }
        if (limit >= 0 && list.size() >= limit) return false;
        list.add(new Home(uuid, name, SerializableLocation.fromBukkitLocation(location)));
        saveAll();
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        List<Home> list = homes.get(uuid);
        if (list == null) return false;
        boolean removed = list.removeIf(h -> h.getName().equalsIgnoreCase(name));
        if (removed) saveAll();
        return removed;
    }

    public void loadPlayer(UUID uuid) {
        TeleportDataStorage storage = JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage();
        Map<UUID, List<Home>> all = storage.loadHomes();
        if (all.containsKey(uuid)) {
            homes.put(uuid, new ArrayList<>(all.get(uuid)));
        } else {
            homes.putIfAbsent(uuid, new ArrayList<>());
        }
    }

    public void savePlayer(UUID uuid) {
        TeleportDataStorage storage = JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage();
        Map<UUID, List<Home>> all = storage.loadHomes();
        List<Home> playerHomes = homes.get(uuid);
        if (playerHomes != null) {
            all.put(uuid, playerHomes);
        } else {
            all.remove(uuid);
        }
        storage.saveHomes(all);
    }
}
