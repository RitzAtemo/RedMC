package red.aviora.redmc.permissions.models;

import java.util.ArrayList;
import java.util.List;

public class Group {

	private final String id;
	private String displayName;
	private final List<String> inherits = new ArrayList<>();
	private final List<PermissionEntry> permissions = new ArrayList<>();

	public Group(String id, String displayName) {
		this.id = id.toLowerCase();
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getInherits() {
		return inherits;
	}

	public void addInherit(String groupId) {
		String lower = groupId.toLowerCase();
		if (!inherits.contains(lower)) {
			inherits.add(lower);
		}
	}

	public void removeInherit(String groupId) {
		inherits.remove(groupId.toLowerCase());
	}

	public List<PermissionEntry> getPermissions() {
		return permissions;
	}

	public void addPermission(PermissionEntry entry) {
		permissions.add(entry);
	}

	public void removePermission(String permission) {
		permissions.removeIf(entry -> entry.getName().equalsIgnoreCase(permission));
	}
}
