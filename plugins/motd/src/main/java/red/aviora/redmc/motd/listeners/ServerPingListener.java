package red.aviora.redmc.motd.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.ConfigManager;
import red.aviora.redmc.motd.MotdPlugin;
import red.aviora.redmc.motd.utils.MotdManager;

import java.util.List;
import java.util.UUID;

public class ServerPingListener implements Listener {

	@EventHandler
	public void onServerListPing(PaperServerListPingEvent event) {
		MotdPlugin plugin = JavaPlugin.getPlugin(MotdPlugin.class);
		MotdManager manager = plugin.getMotdManager();
		ConfigManager config = plugin.getConfigManager();

		List<String> lines = manager.getTemplate();
		List<Component> lineComponents = lines.stream()
			.map(line -> ApiUtils.formatText(applyPlaceholders(line, event)))
			.toList();
		event.motd(Component.join(JoinConfiguration.newlines(), lineComponents));

		if (config.getBoolean("config.yml", "icons.enabled", false)) {
			CachedServerIcon icon = manager.getIcon();
			if (icon != null) {
				event.setServerIcon(icon);
			}
		}

		if (config.getBoolean("config.yml", "players.override-count", false)) {
			int online = config.getInt("config.yml", "players.online", -1);
			int max = config.getInt("config.yml", "players.max", -1);
			if (online >= 0) event.setNumPlayers(online);
			if (max >= 0) event.setMaxPlayers(max);
		}

		if (config.getBoolean("config.yml", "players.sample.enabled", false)) {
			List<?> sampleLines = config.getList("config.yml", "players.sample.lines");
			if (sampleLines != null) {
				List<PaperServerListPingEvent.ListedPlayerInfo> listedPlayers = event.getListedPlayers();
				listedPlayers.clear();
				for (Object rawLine : sampleLines) {
					if (rawLine instanceof String line) {
						String resolved = applyPlaceholders(line, event);
						String legacy = LegacyComponentSerializer.legacySection()
							.serialize(ApiUtils.formatText(resolved));
						listedPlayers.add(new PaperServerListPingEvent.ListedPlayerInfo(legacy, UUID.randomUUID()));
					}
				}
			}
		}

		if (config.getBoolean("config.yml", "version.override", false)) {
			String versionText = config.getString("config.yml", "version.text", "");
			if (versionText != null && !versionText.isEmpty()) {
				String resolved = applyPlaceholders(versionText, event);
				String legacy = LegacyComponentSerializer.legacySection()
					.serialize(ApiUtils.formatText(resolved));
				event.setVersion(legacy);
			}
			int protocol = config.getInt("config.yml", "version.protocol", -1);
			if (protocol >= 0) event.setProtocolVersion(protocol);
		}

		if (config.getBoolean("config.yml", "ping-logging.enabled", false)) {
			String format = config.getString("config.yml", "ping-logging.format", "%address%");
			ApiUtils.logArgs(format, "%address%", event.getClient().getAddress().getHostString());
		}
	}

	private String applyPlaceholders(String text, PaperServerListPingEvent event) {
		Plugin placeholdersPlugin = Bukkit.getPluginManager().getPlugin("RedMC-Placeholders");
		if (placeholdersPlugin != null && placeholdersPlugin.isEnabled()) {
			try {
				red.aviora.redmc.placeholders.PlaceholdersPlugin pp =
					(red.aviora.redmc.placeholders.PlaceholdersPlugin) placeholdersPlugin;
				text = pp.getPlaceholderResolver().parseString(text, null);
			} catch (Exception ignored) {}
		}
		text = text
			.replace("%online%", String.valueOf(event.getNumPlayers()))
			.replace("%max%", String.valueOf(event.getMaxPlayers()))
			.replace("%version%", event.getVersion())
			.replace("%address%", event.getClient().getAddress().getHostString());
		return text;
	}
}
