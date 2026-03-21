package red.aviora.redmc.tracker.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final String world;
    private final double x, y, z;
    private final float yaw, pitch;

    public PlayerData(UUID uuid, String world, double x, double y, double z, float yaw, float pitch) {
        this.uuid = uuid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static PlayerData fromLocation(UUID uuid, Location location) {
        return new PlayerData(
                uuid,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public UUID getUuid() { return uuid; }
    public String getWorld() { return world; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}
