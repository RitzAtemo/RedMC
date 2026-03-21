package red.aviora.redmc.playtime.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.playtime.models.PlayerPlaytimeData;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlaytimeDataStorage {

    private static final String FILE = "playtime.yml";
    private final ConfigManager configManager;

    public PlaytimeDataStorage(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public PlayerPlaytimeData load(UUID uuid) {
        String path = "players." + uuid;

        long playtime = configManager.getInt(FILE, path + ".playtime-seconds", 0);
        long firstJoin = (long) configManager.getDouble(FILE, path + ".first-join", System.currentTimeMillis() / 1000L);
        long lastSeen = (long) configManager.getDouble(FILE, path + ".last-seen", System.currentTimeMillis() / 1000L);

        return new PlayerPlaytimeData(uuid, playtime, firstJoin, lastSeen);
    }

    public void save(PlayerPlaytimeData data) {
        String path = "players." + data.getUuid();

        YamlConfiguration config = configManager.getConfig(FILE);
        if (config == null) return;

        config.set(path + ".playtime-seconds", data.getPlaytimeSeconds());
        config.set(path + ".first-join", data.getFirstJoin());
        config.set(path + ".last-seen", data.getLastSeen());

        File file = new File(configManager.getCurrentPlugin().getDataFolder(), FILE);
        try {
            config.save(file);
        } catch (IOException e) {
            ApiUtils.logArgs("Failed to save playtime data: %error%", "%error%", e.getMessage());
        }
    }
}
