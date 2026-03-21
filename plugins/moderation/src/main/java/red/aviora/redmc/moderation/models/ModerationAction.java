package red.aviora.redmc.moderation.models;

import java.util.UUID;

public class ModerationAction {

    private final String id;
    private final ModerationActionType type;
    private final UUID staffUuid;
    private final String staffName;
    private final String targetName;
    private final String reason;
    private final long timestamp;
    private final long duration; // seconds, -1 = permanent
    private boolean active;

    public ModerationAction(String id, ModerationActionType type, UUID staffUuid, String staffName,
                            String targetName, String reason, long timestamp, long duration, boolean active) {
        this.id = id;
        this.type = type;
        this.staffUuid = staffUuid;
        this.staffName = staffName;
        this.targetName = targetName;
        this.reason = reason;
        this.timestamp = timestamp;
        this.duration = duration;
        this.active = active;
    }

    public String getId() { return id; }
    public ModerationActionType getType() { return type; }
    public UUID getStaffUuid() { return staffUuid; }
    public String getStaffName() { return staffName; }
    public String getTargetName() { return targetName; }
    public String getReason() { return reason; }
    public long getTimestamp() { return timestamp; }
    public long getDuration() { return duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isExpired() {
        if (duration == -1) return false;
        return timestamp + duration * 1000L <= System.currentTimeMillis();
    }

    public boolean isPermanent() {
        return duration == -1;
    }
}
