package red.aviora.redmc.moderation.managers;

import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.models.ModerationActionType;
import red.aviora.redmc.moderation.utils.ModerationDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BanManager {

    private final ModerationDataStorage storage;
    private Map<UUID, List<ModerationAction>> actionsMap;
    private final Map<UUID, ModerationAction> activeBans = new HashMap<>();
    private final Map<UUID, String> targetNames = new HashMap<>();

    public BanManager(ModerationDataStorage storage, Map<UUID, List<ModerationAction>> actionsMap) {
        this.storage = storage;
        this.actionsMap = actionsMap;
    }

    public void load() {
        activeBans.clear();
        targetNames.clear();
        for (Map.Entry<UUID, List<ModerationAction>> entry : actionsMap.entrySet()) {
            for (ModerationAction action : entry.getValue()) {
                if (action.getType() == ModerationActionType.BAN && action.isActive() && !action.isExpired()) {
                    activeBans.put(entry.getKey(), action);
                    if (!action.getTargetName().isEmpty()) {
                        targetNames.put(entry.getKey(), action.getTargetName());
                    }
                }
            }
        }
    }

    public Map<UUID, String> getBannedNames() {
        return targetNames;
    }

    public UUID findBannedByName(String name) {
        for (Map.Entry<UUID, String> entry : targetNames.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) return entry.getKey();
        }
        for (Map.Entry<UUID, ModerationAction> entry : activeBans.entrySet()) {
            if (entry.getValue().getTargetName().equalsIgnoreCase(name)) return entry.getKey();
        }
        return null;
    }

    public void ban(UUID target, String targetName, UUID staffUuid, String staffName, String reason, long durationSeconds) {
        List<ModerationAction> actions = actionsMap.computeIfAbsent(target, k -> new ArrayList<>());
        for (ModerationAction action : actions) {
            if (action.getType() == ModerationActionType.BAN && action.isActive()) {
                action.setActive(false);
            }
        }
        activeBans.remove(target);

        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        ModerationAction action = new ModerationAction(id, ModerationActionType.BAN, staffUuid, staffName, targetName, reason, now, durationSeconds, true);
        actions.add(action);
        activeBans.put(target, action);
        targetNames.put(target, targetName);
        storage.saveActions(actionsMap);
    }

    public boolean unban(UUID target) {
        List<ModerationAction> actions = actionsMap.get(target);
        if (actions == null) return false;
        boolean found = false;
        for (ModerationAction action : actions) {
            if (action.getType() == ModerationActionType.BAN && action.isActive()) {
                action.setActive(false);
                found = true;
            }
        }
        activeBans.remove(target);
        targetNames.remove(target);
        if (found) storage.saveActions(actionsMap);
        return found;
    }

    public boolean isBanned(UUID target) {
        ModerationAction action = activeBans.get(target);
        if (action == null) return false;
        if (action.isExpired()) {
            autoUnban(target, action);
            return false;
        }
        return true;
    }

    public ModerationAction getActiveBan(UUID target) {
        return activeBans.get(target);
    }

    private void autoUnban(UUID target, ModerationAction action) {
        action.setActive(false);
        activeBans.remove(target);
        storage.saveActions(actionsMap);
    }

    public void setActionsMap(Map<UUID, List<ModerationAction>> actionsMap) {
        this.actionsMap = actionsMap;
        load();
    }
}
