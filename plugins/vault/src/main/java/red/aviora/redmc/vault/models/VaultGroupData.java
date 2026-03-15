package red.aviora.redmc.vault.models;

public class VaultGroupData {
	private final String id;
	private String prefix;
	private String suffix;

	public VaultGroupData(String id) {
		this.id = id.toLowerCase();
		this.prefix = null;
		this.suffix = null;
	}

	public String getId() {
		return id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void removePrefix() {
		this.prefix = null;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void removeSuffix() {
		this.suffix = null;
	}
}
