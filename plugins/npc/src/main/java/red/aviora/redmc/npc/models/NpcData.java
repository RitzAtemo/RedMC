package red.aviora.redmc.npc.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcData {

	private final String id;
	private String displayName;
	private String skinOwner;
	private String skinTexture;
	private String skinSignature;
	private String world;
	private double x, y, z;
	private float yaw, pitch;
	private final List<NpcCommand> leftClickCommands = new ArrayList<>();
	private final List<NpcCommand> rightClickCommands = new ArrayList<>();
	private final Map<String, String> equipment = new HashMap<>();

	public NpcData(String id, String displayName, String world, double x, double y, double z, float yaw, float pitch) {
		this.id = id;
		this.displayName = displayName;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public String getId() { return id; }
	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }
	public String getSkinOwner() { return skinOwner; }
	public void setSkinOwner(String skinOwner) { this.skinOwner = skinOwner; }
	public String getSkinTexture() { return skinTexture; }
	public void setSkinTexture(String skinTexture) { this.skinTexture = skinTexture; }
	public String getSkinSignature() { return skinSignature; }
	public void setSkinSignature(String skinSignature) { this.skinSignature = skinSignature; }
	public boolean hasSkin() { return skinTexture != null && !skinTexture.isEmpty(); }
	public String getWorld() { return world; }
	public void setWorld(String world) { this.world = world; }
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	public double getZ() { return z; }
	public void setZ(double z) { this.z = z; }
	public float getYaw() { return yaw; }
	public void setYaw(float yaw) { this.yaw = yaw; }
	public float getPitch() { return pitch; }
	public void setPitch(float pitch) { this.pitch = pitch; }
	public List<NpcCommand> getLeftClickCommands() { return leftClickCommands; }
	public List<NpcCommand> getRightClickCommands() { return rightClickCommands; }
	public Map<String, String> getEquipment() { return equipment; }
}
