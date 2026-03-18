package red.aviora.redmc.holograms.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.holograms.HologramsPlugin;
import red.aviora.redmc.holograms.models.HologramData;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;
import red.aviora.redmc.placeholders.utils.PlaceholderParser;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager {

	private final Map<String, HologramData> holograms = new ConcurrentHashMap<>();
	private final HologramDataStorage storage = new HologramDataStorage();

	public void loadAll() {
		holograms.clear();
		holograms.putAll(storage.loadHolograms());
		for (HologramData data : holograms.values()) {
			spawnHologram(data);
		}
	}

	public void saveAll() {
		storage.saveHolograms(holograms);
	}

	public void reloadAll() {
		despawnAll();
		holograms.clear();
		holograms.putAll(storage.loadHolograms());
		for (HologramData data : holograms.values()) {
			spawnHologram(data);
		}
	}

	public void despawnAll() {
		for (HologramData data : holograms.values()) {
			despawnHologram(data);
		}
	}

	public HologramData createHologram(String id, String name, String world, double x, double y, double z) {
		HologramData data = new HologramData(id, name, world, x, y, z);
		holograms.put(id, data);
		spawnHologram(data);
		saveAll();
		return data;
	}

	public void deleteHologram(String id) {
		HologramData data = holograms.remove(id);
		if (data != null) {
			despawnHologram(data);
			saveAll();
		}
	}

	public void moveHologram(HologramData data, String world, double x, double y, double z) {
		despawnHologram(data);
		data.setWorld(world);
		data.setX(x);
		data.setY(y);
		data.setZ(z);
		spawnHologram(data);
		saveAll();
	}

	public void refreshLines(HologramData data) {
		despawnHologram(data);
		spawnHologram(data);
		saveAll();
	}

	public void spawnHologram(HologramData data) {
		World world = Bukkit.getWorld(data.getWorld());
		if (world == null) return;

		double lineSpacing = HologramsPlugin.getInstance().getConfigManager()
			.getDouble("config.yml", "line-spacing", 0.3);

		int lineCount = data.getLines().size();
		for (int i = 0; i < lineCount; i++) {
			double yOffset = (lineCount - 1 - i) * lineSpacing;
			Location loc = new Location(world, data.getX(), data.getY() + yOffset, data.getZ());
			String raw = data.getLines().get(i);

			Bukkit.getRegionScheduler().run(HologramsPlugin.getInstance(), loc, t -> {
				TextDisplay display = world.spawn(loc, TextDisplay.class, entity -> {
					entity.setBillboard(Display.Billboard.CENTER);
					entity.setSeeThrough(false);
					entity.setPersistent(false);
					entity.text(ApiUtils.formatText(raw));
				});
				data.getEntityIds().add(display.getUniqueId());
			});
		}
	}

	public void despawnHologram(HologramData data) {
		for (UUID uid : data.getEntityIds()) {
			org.bukkit.entity.Entity entity = Bukkit.getEntity(uid);
			if (entity != null) {
				Location loc = entity.getLocation();
				Bukkit.getRegionScheduler().run(HologramsPlugin.getInstance(), loc, t -> entity.remove());
			}
		}
		data.getEntityIds().clear();
	}

	public void refreshAllForPlayer(Player player) {
		PlaceholderParser parser = PlaceholdersPlugin.getInstance().getPlaceholderResolver();
		for (HologramData data : holograms.values()) {
			for (int i = 0; i < data.getEntityIds().size(); i++) {
				UUID uid = data.getEntityIds().get(i);
				org.bukkit.entity.Entity entity = Bukkit.getEntity(uid);
				if (entity == null) continue;

				String raw = data.getLines().get(i);
				Component text = ApiUtils.formatText(parser.parseString(raw, player));
				Location loc = entity.getLocation();
				final UUID finalUid = uid;
				final Component finalText = text;
				Bukkit.getRegionScheduler().run(HologramsPlugin.getInstance(), loc, t -> {
					org.bukkit.entity.Entity e = Bukkit.getEntity(finalUid);
					if (e instanceof TextDisplay display) {
						display.text(finalText);
					}
				});
			}
		}
	}

	public HologramData getHologram(String id) {
		return holograms.get(id);
	}

	public Map<String, HologramData> getAllHolograms() {
		return holograms;
	}

	public Set<String> getHologramIds() {
		return holograms.keySet();
	}
}
