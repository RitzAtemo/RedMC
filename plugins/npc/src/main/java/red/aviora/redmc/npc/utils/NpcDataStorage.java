package red.aviora.redmc.npc.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.models.NpcCommand;
import red.aviora.redmc.npc.models.NpcData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcDataStorage {

	private final File npcsFile;

	public NpcDataStorage() {
		this.npcsFile = new File(NpcPlugin.getInstance().getDataFolder(), "npcs.yml");
	}

	public Map<String, NpcData> loadNpcs() {
		Map<String, NpcData> npcs = new HashMap<>();

		if (!npcsFile.exists()) {
			return npcs;
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(npcsFile);
		ConfigurationSection section = config.getConfigurationSection("npcs");

		if (section == null) {
			return npcs;
		}

		for (String id : section.getKeys(false)) {
			ConfigurationSection npcSec = section.getConfigurationSection(id);
			if (npcSec == null) continue;

			String displayName = npcSec.getString("display-name", id);
			String world = npcSec.getString("world", "world");
			double x = npcSec.getDouble("x", 0);
			double y = npcSec.getDouble("y", 64);
			double z = npcSec.getDouble("z", 0);
			float yaw = (float) npcSec.getDouble("yaw", 0);
			float pitch = (float) npcSec.getDouble("pitch", 0);

			NpcData data = new NpcData(id, displayName, world, x, y, z, yaw, pitch);
			data.setSkinOwner(npcSec.getString("skin-owner", null));
			data.setSkinTexture(npcSec.getString("skin-texture", null));
			data.setSkinSignature(npcSec.getString("skin-signature", null));

			loadCommands(npcSec, "left-click-commands", data.getLeftClickCommands());
			loadCommands(npcSec, "right-click-commands", data.getRightClickCommands());

			ConfigurationSection equipSec = npcSec.getConfigurationSection("equipment");
			if (equipSec != null) {
				for (String slot : equipSec.getKeys(false)) {
					String itemId = equipSec.getString(slot);
					if (itemId != null) data.getEquipment().put(slot, itemId);
				}
			}

			npcs.put(id, data);
		}

		return npcs;
	}

	private void loadCommands(ConfigurationSection npcSec, String key, List<NpcCommand> list) {
		List<String> entries = npcSec.getStringList(key);
		for (String entry : entries) {
			int colon = entry.indexOf(':');
			if (colon < 0) continue;
			String typeStr = entry.substring(0, colon).toUpperCase();
			String cmd = entry.substring(colon + 1);
			try {
				list.add(new NpcCommand(NpcCommand.Type.valueOf(typeStr), cmd));
			} catch (IllegalArgumentException ignored) {}
		}
	}

	public void saveNpcs(Map<String, NpcData> npcs) {
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection section = config.createSection("npcs");

		for (NpcData data : npcs.values()) {
			ConfigurationSection npcSec = section.createSection(data.getId());
			npcSec.set("display-name", data.getDisplayName());
			npcSec.set("world", data.getWorld());
			npcSec.set("x", data.getX());
			npcSec.set("y", data.getY());
			npcSec.set("z", data.getZ());
			npcSec.set("yaw", data.getYaw());
			npcSec.set("pitch", data.getPitch());
			if (data.getSkinOwner() != null) npcSec.set("skin-owner", data.getSkinOwner());
			if (data.getSkinTexture() != null) npcSec.set("skin-texture", data.getSkinTexture());
			if (data.getSkinSignature() != null) npcSec.set("skin-signature", data.getSkinSignature());

			npcSec.set("left-click-commands", serializeCommands(data.getLeftClickCommands()));
			npcSec.set("right-click-commands", serializeCommands(data.getRightClickCommands()));

			if (!data.getEquipment().isEmpty()) {
				ConfigurationSection equipSec = npcSec.createSection("equipment");
				data.getEquipment().forEach(equipSec::set);
			}
		}

		try {
			config.save(npcsFile);
		} catch (IOException e) {
			ApiUtils.log("Failed to save npcs.yml: " + e.getMessage());
		}
	}

	private List<String> serializeCommands(List<NpcCommand> commands) {
		return commands.stream()
			.map(c -> c.getType().name() + ":" + c.getCommand())
			.toList();
	}
}
