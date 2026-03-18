package red.aviora.redmc.holograms.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HologramData {

	private final String id;
	private String name;
	private String world;
	private double x;
	private double y;
	private double z;
	private final List<String> lines;
	private final List<UUID> entityIds;

	public HologramData(String id, String name, String world, double x, double y, double z) {
		this.id = id;
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.lines = new ArrayList<>();
		this.entityIds = new ArrayList<>();
	}

	public String getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getWorld() { return world; }
	public void setWorld(String world) { this.world = world; }
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	public double getZ() { return z; }
	public void setZ(double z) { this.z = z; }
	public List<String> getLines() { return lines; }
	public List<UUID> getEntityIds() { return entityIds; }
}
