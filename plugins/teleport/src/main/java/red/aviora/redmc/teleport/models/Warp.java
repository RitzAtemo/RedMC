package red.aviora.redmc.teleport.models;

public class Warp {

    private final String id;
    private final SerializableLocation location;

    public Warp(String id, SerializableLocation location) {
        this.id = id;
        this.location = location;
    }

    public String getId() { return id; }
    public SerializableLocation getLocation() { return location; }
}
