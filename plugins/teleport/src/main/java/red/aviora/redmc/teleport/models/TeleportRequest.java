package red.aviora.redmc.teleport.models;

import java.util.UUID;

public class TeleportRequest {

    private final UUID requesterUuid;
    private final UUID targetUuid;
    private final boolean toRequester;
    private final long createdAt;

    public TeleportRequest(UUID requesterUuid, UUID targetUuid, boolean toRequester, long createdAt) {
        this.requesterUuid = requesterUuid;
        this.targetUuid = targetUuid;
        this.toRequester = toRequester;
        this.createdAt = createdAt;
    }

    public UUID getRequesterUuid() { return requesterUuid; }
    public UUID getTargetUuid() { return targetUuid; }
    public boolean isToRequester() { return toRequester; }
    public long getCreatedAt() { return createdAt; }
}
