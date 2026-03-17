package red.aviora.redmc.teleport.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.Home;
import red.aviora.redmc.teleport.models.SerializableLocation;
import red.aviora.redmc.teleport.models.Warp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportDataStorage {

    private final File dataFolder;

    public TeleportDataStorage() {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        this.dataFolder = plugin.getDataFolder();
        dataFolder.mkdirs();
    }

    private File warpsFile() { return new File(dataFolder, "warps.yml"); }
    private File homesFile() { return new File(dataFolder, "homes.yml"); }
    private File spawnsFile() { return new File(dataFolder, "spawns.yml"); }

    public Map<String, Warp> loadWarps() {
        Map<String, Warp> warps = new HashMap<>();
        File file = warpsFile();
        if (!file.exists()) return warps;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("warps");
        if (section == null) return warps;
        for (String id : section.getKeys(false)) {
            ConfigurationSection ws = section.getConfigurationSection(id);
            if (ws == null) continue;
            SerializableLocation loc = new SerializableLocation(
                ws.getString("world"),
                ws.getDouble("x"),
                ws.getDouble("y"),
                ws.getDouble("z"),
                (float) ws.getDouble("yaw"),
                (float) ws.getDouble("pitch")
            );
            warps.put(id, new Warp(id, loc));
        }
        return warps;
    }

    public void saveWarps(Map<String, Warp> warps) {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, Warp> entry : warps.entrySet()) {
            String id = entry.getKey();
            SerializableLocation loc = entry.getValue().getLocation();
            config.set("warps." + id + ".world", loc.getWorld());
            config.set("warps." + id + ".x", loc.getX());
            config.set("warps." + id + ".y", loc.getY());
            config.set("warps." + id + ".z", loc.getZ());
            config.set("warps." + id + ".yaw", loc.getYaw());
            config.set("warps." + id + ".pitch", loc.getPitch());
        }
        try {
            config.save(warpsFile());
        } catch (IOException e) {
            ApiUtils.log("Failed to save warps.yml: " + e.getMessage());
        }
    }

    public Map<UUID, List<Home>> loadHomes() {
        Map<UUID, List<Home>> homes = new HashMap<>();
        File file = homesFile();
        if (!file.exists()) return homes;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection playersSection = config.getConfigurationSection("homes");
        if (playersSection == null) return homes;
        for (String uuidStr : playersSection.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidStr);
            if (playerSection == null) continue;
            List<Home> playerHomes = new ArrayList<>();
            for (String name : playerSection.getKeys(false)) {
                ConfigurationSection hs = playerSection.getConfigurationSection(name);
                if (hs == null) continue;
                SerializableLocation loc = new SerializableLocation(
                    hs.getString("world"),
                    hs.getDouble("x"),
                    hs.getDouble("y"),
                    hs.getDouble("z"),
                    (float) hs.getDouble("yaw"),
                    (float) hs.getDouble("pitch")
                );
                playerHomes.add(new Home(uuid, name, loc));
            }
            homes.put(uuid, playerHomes);
        }
        return homes;
    }

    public void saveHomes(Map<UUID, List<Home>> homes) {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, List<Home>> entry : homes.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Home home : entry.getValue()) {
                SerializableLocation loc = home.getLocation();
                String base = "homes." + uuidStr + "." + home.getName();
                config.set(base + ".world", loc.getWorld());
                config.set(base + ".x", loc.getX());
                config.set(base + ".y", loc.getY());
                config.set(base + ".z", loc.getZ());
                config.set(base + ".yaw", loc.getYaw());
                config.set(base + ".pitch", loc.getPitch());
            }
        }
        try {
            config.save(homesFile());
        } catch (IOException e) {
            ApiUtils.log("Failed to save homes.yml: " + e.getMessage());
        }
    }

    public SerializableLocation loadSpawn() {
        File file = spawnsFile();
        if (!file.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isConfigurationSection("spawn")) return null;
        ConfigurationSection s = config.getConfigurationSection("spawn");
        if (s == null || s.getString("world") == null) return null;
        return new SerializableLocation(
            s.getString("world"),
            s.getDouble("x"),
            s.getDouble("y"),
            s.getDouble("z"),
            (float) s.getDouble("yaw"),
            (float) s.getDouble("pitch")
        );
    }

    public SerializableLocation loadNewbieSpawn() {
        File file = spawnsFile();
        if (!file.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isConfigurationSection("newbie-spawn")) return null;
        ConfigurationSection s = config.getConfigurationSection("newbie-spawn");
        if (s == null || s.getString("world") == null) return null;
        return new SerializableLocation(
            s.getString("world"),
            s.getDouble("x"),
            s.getDouble("y"),
            s.getDouble("z"),
            (float) s.getDouble("yaw"),
            (float) s.getDouble("pitch")
        );
    }

    public void saveSpawn(SerializableLocation loc) {
        File file = spawnsFile();
        YamlConfiguration config = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        config.set("spawn.world", loc.getWorld());
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", loc.getYaw());
        config.set("spawn.pitch", loc.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            ApiUtils.log("Failed to save spawns.yml: " + e.getMessage());
        }
    }

    public void saveNewbieSpawn(SerializableLocation loc) {
        File file = spawnsFile();
        YamlConfiguration config = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        config.set("newbie-spawn.world", loc.getWorld());
        config.set("newbie-spawn.x", loc.getX());
        config.set("newbie-spawn.y", loc.getY());
        config.set("newbie-spawn.z", loc.getZ());
        config.set("newbie-spawn.yaw", loc.getYaw());
        config.set("newbie-spawn.pitch", loc.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            ApiUtils.log("Failed to save spawns.yml: " + e.getMessage());
        }
    }
}
