package red.aviora.redmc.moderation.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import red.aviora.redmc.moderation.ModerationPlugin;
import red.aviora.redmc.moderation.models.ModerationAction;
import red.aviora.redmc.moderation.models.ModerationActionType;
import red.aviora.redmc.moderation.utils.ModerationDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MuteManager {

    private final ModerationDataStorage storage;
    private Map<UUID, List<ModerationAction>> actionsMap;
    private final Map<UUID, ModerationAction> activeMutes = new HashMap<>();

    public MuteManager(ModerationDataStorage storage, Map<UUID, List<ModerationAction>> actionsMap) {
        this.storage = storage;
        this.actionsMap = actionsMap;
    }

    public void load() {
        activeMutes.clear();
        for (Map.Entry<UUID, List<ModerationAction>> entry : actionsMap.entrySet()) {
            for (ModerationAction action : entry.getValue()) {
                if (action.getType() == ModerationActionType.MUTE && action.isActive() && !action.isExpired()) {
                    activeMutes.put(entry.getKey(), action);
                }
            }
        }
    }

    public void mute(UUID target, UUID staffUuid, String staffName, String reason, long durationSeconds) {
        // Deactivate existing mutes
        List<ModerationAction> actions = actionsMap.computeIfAbsent(target, k -> new ArrayList<>());
        for (ModerationAction action : actions) {
            if (action.getType() == ModerationActionType.MUTE && action.isActive()) {
                action.setActive(false);
            }
        }
        activeMutes.remove(target);

        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        ModerationAction action = new ModerationAction(id, ModerationActionType.MUTE, staffUuid, staffName, reason, now, durationSeconds, true);
        actions.add(action);
        activeMutes.put(target, action);
        storage.saveActions(actionsMap);
        applyMeta(target);
    }

    public boolean unmute(UUID target) {
        List<ModerationAction> actions = actionsMap.get(target);
        if (actions == null) return false;
        boolean found = false;
        for (ModerationAction action : actions) {
            if (action.getType() == ModerationActionType.MUTE && action.isActive()) {
                action.setActive(false);
                found = true;
            }
        }
        activeMutes.remove(target);
        clearMeta(target);
        if (found) storage.saveActions(actionsMap);
        return found;
    }

    public boolean isMuted(UUID target) {
        ModerationAction action = activeMutes.get(target);
        if (action == null) return false;
        if (action.isExpired()) {
            autoUnmute(target, action);
            return false;
        }
        return true;
    }

    public ModerationAction getActiveMute(UUID target) {
        return activeMutes.get(target);
    }

    private void autoUnmute(UUID target, ModerationAction action) {
        action.setActive(false);
        activeMutes.remove(target);
        clearMeta(target);
        storage.saveActions(actionsMap);
    }

    public void applyMeta(UUID target) {
        Player online = Bukkit.getPlayer(target);
        if (online != null) {
            online.setMetadata("redmc:muted", new FixedMetadataValue(ModerationPlugin.getInstance(), true));
        }
    }

    private void clearMeta(UUID target) {
        Player online = Bukkit.getPlayer(target);
        if (online != null) {
            online.removeMetadata("redmc:muted", ModerationPlugin.getInstance());
        }
    }

    public void setActionsMap(Map<UUID, List<ModerationAction>> actionsMap) {
        this.actionsMap = actionsMap;
        load();
    }
}
