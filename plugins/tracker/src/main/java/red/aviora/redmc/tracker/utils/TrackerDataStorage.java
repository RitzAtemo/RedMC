package red.aviora.redmc.tracker.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.tracker.models.PlayerData;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TrackerDataStorage {

    private static final String FILE = "tracker.yml";
    private final ConfigManager configManager;

    public TrackerDataStorage(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public PlayerData load(UUID uuid) {
        String path = "players." + uuid;

        String world = configManager.getString(FILE, path + ".world", null);
        if (world == null) return null;

        double x = configManager.getDouble(FILE, path + ".x", 0.0);
        double y = configManager.getDouble(FILE, path + ".y", 64.0);
        double z = configManager.getDouble(FILE, path + ".z", 0.0);
        float yaw = (float) configManager.getDouble(FILE, path + ".yaw", 0.0);
        float pitch = (float) configManager.getDouble(FILE, path + ".pitch", 0.0);

        return new PlayerData(uuid, world, x, y, z, yaw, pitch);
    }

    public void save(PlayerData data) {
        String path = "players." + data.getUuid();

        YamlConfiguration config = configManager.getConfig(FILE);
        if (config == null) return;

        config.set(path + ".world", data.getWorld());
        config.set(path + ".x", data.getX());
        config.set(path + ".y", data.getY());
        config.set(path + ".z", data.getZ());
        config.set(path + ".yaw", (double) data.getYaw());
        config.set(path + ".pitch", (double) data.getPitch());

        File file = new File(configManager.getCurrentPlugin().getDataFolder(), FILE);
        try {
            config.save(file);
        } catch (IOException e) {
            ApiUtils.logArgs("Failed to save tracker data: %error%", "%error%", e.getMessage());
        }
    }
}
