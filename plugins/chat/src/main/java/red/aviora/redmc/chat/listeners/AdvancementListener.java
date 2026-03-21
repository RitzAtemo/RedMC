package red.aviora.redmc.chat.listeners;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;
import red.aviora.redmc.vault.VaultPlugin;

public class AdvancementListener implements Listener {

	@EventHandler
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		if (!ChatPlugin.getInstance().getConfigManager().getBoolean("config.yml", "advancement.enabled", true)) return;

		AdvancementDisplay display = event.getAdvancement().getDisplay();
		if (display == null || !display.doesAnnounceToChat()) return;

		Player player = event.getPlayer();
		Component titleRaw = display.title();
		Component descRaw = display.description();

		String frameKey = switch (display.frame()) {
			case GOAL -> "advancement.goal";
			case CHALLENGE -> "advancement.challenge";
			default -> "advancement.task";
		};

		for (Player online : Bukkit.getOnlinePlayers()) {
			Component title = GlobalTranslator.render(titleRaw, online.locale());
			Component desc = GlobalTranslator.render(descRaw, online.locale());

			Component hover = Component.text()
				.append(title)
				.appendNewline()
				.append(Component.text("───────────────", NamedTextColor.DARK_GRAY))
				.appendNewline()
				.append(desc.colorIfAbsent(NamedTextColor.GRAY))
				.build();

			Component titleWithHover = Component.text()
				.append(title)
				.hoverEvent(HoverEvent.showText(hover))
				.build();

			String format = ChatPlugin.getInstance().getLocaleManager().getMessage(online, frameKey);
			format = VaultPlugin.resolvePlayer(format, player);
			PlaceholdersPlugin placeholders = JavaPlugin.getPlugin(PlaceholdersPlugin.class);
			if (placeholders != null) format = placeholders.getPlaceholderResolver().parseString(format, player);

			String[] parts = format.split("%title%", 2);
			Component before = ApiUtils.getMM().deserialize(parts[0]);
			Component after = parts.length > 1 ? ApiUtils.getMM().deserialize(parts[1]) : Component.empty();

			online.sendMessage(Component.empty().append(before).append(titleWithHover).append(after));
		}
	}
}
