package red.aviora.redmc.moderation.managers;

import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.models.ModerationActionType;
import red.aviora.redmc.moderation.utils.ModerationDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarnManager {

    private final ModerationDataStorage storage;
    private Map<UUID, List<ModerationAction>> actionsMap = new HashMap<>();

    public WarnManager(ModerationDataStorage storage) {
        this.storage = storage;
    }

    public void load() {
        actionsMap = storage.loadActions();
    }

    public void warn(UUID target, UUID staffUuid, String staffName, String reason) {
        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        ModerationAction action = new ModerationAction(id, ModerationActionType.WARN, staffUuid, staffName, "", reason, now, 0, true);
        actionsMap.computeIfAbsent(target, k -> new ArrayList<>()).add(action);
        storage.saveActions(actionsMap);
    }

    public List<ModerationAction> getHistory(UUID target) {
        return actionsMap.getOrDefault(target, new ArrayList<>());
    }

    public Map<UUID, List<ModerationAction>> getActionsMap() {
        return actionsMap;
    }

    public void setActionsMap(Map<UUID, List<ModerationAction>> actionsMap) {
        this.actionsMap = actionsMap;
    }
}
