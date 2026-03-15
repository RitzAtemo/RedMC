package red.aviora.redmc.placeholders.utils;

import org.bukkit.entity.Player;
import red.aviora.redmc.placeholders.models.PlaceholderContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PlaceholderRegistry {

	private final Map<String, PlaceholderContext> placeholders = new HashMap<>();
	private BiFunction<String, Player, String> patternHandler;

	public void set(String key, PlaceholderContext context) {
		placeholders.put(key, context);
	}

	public void setPatternHandler(BiFunction<String, Player, String> handler) {
		this.patternHandler = handler;
	}

	public String get(String key, Player player) {
		PlaceholderContext context = placeholders.get(key);
		if (context != null) {
			return context.getValue(player);
		}

		if (patternHandler != null) {
			return patternHandler.apply(key, player);
		}

		return null;
	}
}
