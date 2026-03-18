package red.aviora.redmc.holograms.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.holograms.HologramsPlugin;
import red.aviora.redmc.holograms.models.HologramData;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;
import red.aviora.redmc.placeholders.utils.PlaceholderParser;

import java.util.UUID;

public class HologramRefreshTask {

	private ScheduledTask task;

	public void start() {
		long intervalTicks = HologramsPlugin.getInstance().getConfigManager()
			.getInt("config.yml", "refresh-interval-ticks", 20);

		task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
			HologramsPlugin.getInstance(),
			t -> tick(),
			intervalTicks,
			intervalTicks
		);
	}

	public void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private void tick() {
		HologramManager manager = HologramsPlugin.getInstance().getHologramManager();
		PlaceholderParser parser = PlaceholdersPlugin.getInstance().getPlaceholderResolver();

		for (HologramData data : manager.getAllHolograms().values()) {
			World world = Bukkit.getWorld(data.getWorld());
			if (world == null) continue;

			Player context = null;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getWorld().getName().equals(data.getWorld())) {
					context = p;
					break;
				}
			}

			for (int i = 0; i < data.getEntityIds().size(); i++) {
				UUID uid = data.getEntityIds().get(i);
				org.bukkit.entity.Entity entity = Bukkit.getEntity(uid);
				if (entity == null) continue;

				String raw = data.getLines().get(i);
				Component text = context != null
					? ApiUtils.formatText(parser.parseString(raw, context))
					: ApiUtils.formatText(raw);

				Location loc = entity.getLocation();
				final Component finalText = text;
				final UUID finalUid = uid;
				Bukkit.getRegionScheduler().run(HologramsPlugin.getInstance(), loc, t2 -> {
					org.bukkit.entity.Entity e = Bukkit.getEntity(finalUid);
					if (e instanceof TextDisplay display) {
						display.text(finalText);
					}
				});
			}
		}
	}
}
