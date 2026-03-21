package red.aviora.redmc.moderation.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ticket {

    private final String id;
    private final UUID authorUuid;
    private final String authorName;
    private final String message;
    private TicketStatus status;
    private final long timestamp;
    private final List<TicketReply> replies;

    public Ticket(String id, UUID authorUuid, String authorName, String message,
                  TicketStatus status, long timestamp, List<TicketReply> replies) {
        this.id = id;
        this.authorUuid = authorUuid;
        this.authorName = authorName;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.replies = replies != null ? replies : new ArrayList<>();
    }

    public String getId() { return id; }
    public UUID getAuthorUuid() { return authorUuid; }
    public String getAuthorName() { return authorName; }
    public String getMessage() { return message; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
    public List<TicketReply> getReplies() { return replies; }

    public String getShortId() {
        return id.length() >= 8 ? id.substring(0, 8) : id;
    }
}
