package red.aviora.redmc.moderation.models;

import java.util.UUID;

public class TicketReply {

    private final UUID staffUuid;
    private final String staffName;
    private final String message;
    private final long timestamp;

    public TicketReply(UUID staffUuid, String staffName, String message, long timestamp) {
        this.staffUuid = staffUuid;
        this.staffName = staffName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public UUID getStaffUuid() { return staffUuid; }
    public String getStaffName() { return staffName; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
