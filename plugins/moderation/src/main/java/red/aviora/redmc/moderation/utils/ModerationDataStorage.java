package red.aviora.redmc.moderation.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.models.ModerationActionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModerationDataStorage {

    private static final String ACTIONS_FILE = "actions.yml";

    private final ConfigManager configManager;

    public ModerationDataStorage(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public Map<UUID, List<ModerationAction>> loadActions() {
        Map<UUID, List<ModerationAction>> result = new HashMap<>();
        YamlConfiguration config = configManager.getConfig(ACTIONS_FILE);
        if (config == null) return result;

        ConfigurationSection actionsSection = config.getConfigurationSection("actions");
        if (actionsSection == null) return result;

        for (String uuidStr : actionsSection.getKeys(false)) {
            UUID targetUuid;
            try {
                targetUuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }

            List<?> rawList = actionsSection.getList(uuidStr);
            if (rawList == null) continue;

            List<ModerationAction> actions = new ArrayList<>();
            for (Object obj : rawList) {
                if (!(obj instanceof Map<?, ?> map)) continue;
                try {
                    String id = (String) map.get("id");
                    ModerationActionType type = ModerationActionType.valueOf((String) map.get("type"));
                    UUID staffUuid = UUID.fromString((String) map.get("staff-uuid"));
                    String staffName = (String) map.get("staff-name");
                    String targetName = map.containsKey("target-name") ? (String) map.get("target-name") : "";
                    String reason = (String) map.get("reason");
                    long timestamp = toLong(map.get("timestamp"));
                    long duration = toLong(map.get("duration"));
                    boolean active = (boolean) map.get("active");
                    actions.add(new ModerationAction(id, type, staffUuid, staffName, targetName, reason, timestamp, duration, active));
                } catch (Exception e) {
                    ApiUtils.logArgs("Failed to parse action entry: %error%", "%error%", e.getMessage());
                }
            }
            result.put(targetUuid, actions);
        }
        return result;
    }

    public void saveActions(Map<UUID, List<ModerationAction>> actionsMap) {
        YamlConfiguration config = configManager.getConfig(ACTIONS_FILE);
        if (config == null) return;

        config.set("actions", null);

        for (Map.Entry<UUID, List<ModerationAction>> entry : actionsMap.entrySet()) {
            String uuidPath = "actions." + entry.getKey().toString();
            List<Map<String, Object>> list = new ArrayList<>();
            for (ModerationAction action : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", action.getId());
                map.put("type", action.getType().name());
                map.put("staff-uuid", action.getStaffUuid().toString());
                map.put("staff-name", action.getStaffName());
                map.put("target-name", action.getTargetName());
                map.put("reason", action.getReason());
                map.put("timestamp", action.getTimestamp());
                map.put("duration", action.getDuration());
                map.put("active", action.isActive());
                list.add(map);
            }
            config.set(uuidPath, list);
        }

        saveConfigToDisk(ACTIONS_FILE, config);
    }

    private void saveConfigToDisk(String fileName, YamlConfiguration config) {
        File file = new File(configManager.getCurrentPlugin().getDataFolder(), fileName);
        try {
            config.save(file);
        } catch (IOException e) {
            ApiUtils.logArgs("Failed to save %file%: %error%", "%file%", fileName, "%error%", e.getMessage());
        }
    }

    private long toLong(Object obj) {
        if (obj instanceof Long l) return l;
        if (obj instanceof Integer i) return i.longValue();
        if (obj instanceof Number n) return n.longValue();
        return 0L;
    }
}
