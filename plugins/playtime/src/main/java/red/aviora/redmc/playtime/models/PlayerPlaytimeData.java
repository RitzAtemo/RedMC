package red.aviora.redmc.playtime.models;

import java.util.UUID;

public class PlayerPlaytimeData {

    private final UUID uuid;
    private long playtimeSeconds;
    private long firstJoin;
    private long lastSeen;

    public PlayerPlaytimeData(UUID uuid, long playtimeSeconds, long firstJoin, long lastSeen) {
        this.uuid = uuid;
        this.playtimeSeconds = playtimeSeconds;
        this.firstJoin = firstJoin;
        this.lastSeen = lastSeen;
    }

    public UUID getUuid() { return uuid; }

    public long getPlaytimeSeconds() { return playtimeSeconds; }
    public void addSeconds(long seconds) { this.playtimeSeconds += seconds; }
    public void setPlaytimeSeconds(long playtimeSeconds) { this.playtimeSeconds = playtimeSeconds; }

    public long getFirstJoin() { return firstJoin; }
    public void setFirstJoin(long firstJoin) { this.firstJoin = firstJoin; }

    public long getLastSeen() { return lastSeen; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
}
