package red.aviora.redmc.teleport.models;

import java.util.UUID;

public class Home {

    private final UUID ownerUuid;
    private final String name;
    private final SerializableLocation location;

    public Home(UUID ownerUuid, String name, SerializableLocation location) {
        this.ownerUuid = ownerUuid;
        this.name = name;
        this.location = location;
    }

    public UUID getOwnerUuid() { return ownerUuid; }
    public String getName() { return name; }
    public SerializableLocation getLocation() { return location; }
}
