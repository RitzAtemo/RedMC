package red.aviora.redmc.chat.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.chat.ChatPlugin;
import red.aviora.redmc.placeholders.PlaceholdersPlugin;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChatManager {

	private boolean globalEnabled;
	private String globalPrefix;
	private String globalFormat;

	private boolean localEnabled;
	private String localFormat;
	private int localDefaultRadius;
	private Map<String, Integer> worldRadii = new HashMap<>();

	private boolean privateEnabled;

	private boolean deathEnabled;

	private final Random random = new Random();

	public void loadAll() {
		var cfg = ChatPlugin.getInstance().getConfigManager();
		var config = cfg.getConfig("config.yml");

		globalEnabled = cfg.getBoolean("config.yml", "chat.global.enabled", true);
		globalPrefix = cfg.getString("config.yml", "chat.global.prefix", "!");
		globalFormat = cfg.getString("config.yml", "chat.global.format",
			"<aqua>[G]</aqua> <white>%player%</white> <gray>»</gray> <white>%message%</white>");

		localEnabled = cfg.getBoolean("config.yml", "chat.local.enabled", true);
		localFormat = cfg.getString("config.yml", "chat.local.format",
			"<white>%player%</white> <gray>»</gray> <white>%message%</white>");
		localDefaultRadius = cfg.getInt("config.yml", "chat.local.default-radius", 100);

		worldRadii.clear();
		var worldsSection = config.getConfigurationSection("chat.local.worlds");
		if (worldsSection != null) {
			for (String world : worldsSection.getKeys(false)) {
				var worldSection = worldsSection.getConfigurationSection(world);
				if (worldSection != null) {
					worldRadii.put(world, worldSection.getInt("radius", localDefaultRadius));
				}
			}
		}

		privateEnabled = cfg.getBoolean("config.yml", "chat.private.enabled", true);
		deathEnabled = cfg.getBoolean("config.yml", "death.enabled", true);
	}

	public void processChat(Player player, String message) {
		ChatPlugin.getInstance().getLogger().info("[" + player.getName() + ": say " + message + "]");
		if (globalEnabled && !globalPrefix.isEmpty() && message.startsWith(globalPrefix)) {
			if (!player.hasPermission("redmc.chat.global")) {
				ApiUtils.sendPlayerMessage(player,
					ChatPlugin.getInstance().getLocaleManager().getMessage(player, "chat.no-permission"));
				return;
			}
			String cleanMsg = message.substring(globalPrefix.length()).trim();
			String formatted = formatChatMessage(globalFormat, player, cleanMsg);
			Component component = ApiUtils.formatText(formatted);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(component);
			}
		} else if (localEnabled) {
			sendToLocal(player, message);
		}
	}

	public void broadcastGlobal(Player player, String message) {
		String formatted = formatChatMessage(globalFormat, player, message);
		Component component = ApiUtils.formatText(formatted);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(component);
		}
	}

	private void sendToLocal(Player player, String message) {
		String formatted = formatChatMessage(localFormat, player, message);
		Component component = ApiUtils.formatText(formatted);
		for (Player target : getLocalReceivers(player)) {
			target.sendMessage(component);
		}
	}

	public void sendPrivateMessage(Player sender, Player receiver, String message, String quotedMessage) {
		if (!privateEnabled) return;

		boolean isReply = quotedMessage != null;
		String escaped = MiniMessage.miniMessage().escapeTags(message);
		String escapedQuote = isReply ? MiniMessage.miniMessage().escapeTags(truncate(quotedMessage, 40)) : "";

		String senderKey = isReply ? "chat.reply-sent" : "chat.msg-sent";
		String receiverKey = isReply ? "chat.reply-received" : "chat.msg-received";

		String senderFormat = ChatPlugin.getInstance().getLocaleManager().getMessage(sender, senderKey);
		senderFormat = senderFormat
			.replace("%target%", receiver.getName())
			.replace("%sender%", sender.getName())
			.replace("%message%", escaped)
			.replace("%quoted%", escapedQuote);
		sender.sendMessage(ApiUtils.formatText(senderFormat));

		String receiverFormat = ChatPlugin.getInstance().getLocaleManager().getMessage(receiver, receiverKey);
		receiverFormat = receiverFormat
			.replace("%target%", receiver.getName())
			.replace("%sender%", sender.getName())
			.replace("%message%", escaped)
			.replace("%quoted%", escapedQuote);
		receiver.sendMessage(ApiUtils.formatText(receiverFormat));

		ChatPlugin.getInstance().getSessionManager().recordReceived(receiver.getUniqueId(), sender.getUniqueId(), message);
	}

	public void broadcastDeathMessage(Player victim, Player killer, Entity lastDamager, String groupId, ItemStack weapon) {
		if (!deathEnabled) return;

		Component killerComponent;
		if (killer != null) {
			killerComponent = Component.text(killer.getName());
		} else if (lastDamager instanceof LivingEntity le) {
			Component customName = le.customName();
			killerComponent = customName != null ? customName : Component.translatable(le.getType().translationKey());
		} else {
			killerComponent = Component.text("???");
		}

		Component weaponComponent = Component.empty();
		if (weapon != null && !weapon.getType().isAir()) {
			ItemMeta meta = weapon.getItemMeta();
			weaponComponent = (meta != null && meta.hasDisplayName())
				? meta.displayName()
				: Component.translatable(weapon.getType().translationKey());
		}

		for (Player receiver : getLocalReceivers(victim)) {
			String template = getLocalizedDeathMessage(receiver, groupId);
			if (template == null) continue;
			String withPlayer = template.replace("%player%", MiniMessage.miniMessage().escapeTags(victim.getName()));
			withPlayer = resolvePlaceholders(withPlayer, victim);

			var replacements = new LinkedHashMap<String, Component>();
			replacements.put("%killer%", killerComponent);
			replacements.put("%weapon%", weaponComponent);

			receiver.sendMessage(buildTemplateComponent(withPlayer, replacements));
		}
	}

	private Component buildTemplateComponent(String template, LinkedHashMap<String, Component> replacements) {
		List<Object> parts = new ArrayList<>();
		parts.add(template);
		for (var entry : replacements.entrySet()) {
			List<Object> next = new ArrayList<>();
			for (Object part : parts) {
				if (part instanceof String s) {
					String[] split = s.split(Pattern.quote(entry.getKey()), -1);
					for (int i = 0; i < split.length; i++) {
						next.add(split[i]);
						if (i < split.length - 1) next.add(entry.getValue());
					}
				} else {
					next.add(part);
				}
			}
			parts = next;
		}
		Component result = Component.empty();
		for (Object part : parts) {
			if (part instanceof String s) result = result.append(MiniMessage.miniMessage().deserialize(s));
			else if (part instanceof Component c) result = result.append(c);
		}
		return result;
	}

	private List<Player> getLocalReceivers(Player origin) {
		String worldName = origin.getWorld().getName();
		int radius = worldRadii.getOrDefault(worldName, localDefaultRadius);
		Location loc = origin.getLocation();
		List<Player> result = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!p.getWorld().equals(origin.getWorld())) continue;
			if (radius > 0 && p.getLocation().distance(loc) > radius) continue;
			result.add(p);
		}
		return result;
	}

	private String getLocalizedDeathMessage(Player player, String groupId) {
		List<String> messages = getLocalizedList(player, "death.groups." + groupId + ".messages");
		if (messages.isEmpty() && !groupId.equals("default")) {
			messages = getLocalizedList(player, "death.groups.default.messages");
		}
		if (messages.isEmpty()) return null;
		return messages.get(random.nextInt(messages.size()));
	}

	public List<String> getLocalizedList(Player player, String key) {
		String locale = player.locale().toString();
		var cfg = ChatPlugin.getInstance().getConfigManager();

		var config = cfg.getConfig("lang/" + locale + ".yml");
		if (config != null) {
			List<?> list = config.getList(key);
			if (list != null && !list.isEmpty()) {
				return list.stream().map(Object::toString).toList();
			}
		}

		config = cfg.getConfig("lang/en_US.yml");
		if (config != null) {
			List<?> list = config.getList(key);
			if (list != null) {
				return list.stream().map(Object::toString).toList();
			}
		}
		return List.of();
	}

	private String formatChatMessage(String template, Player player, String message) {
		String escaped = MiniMessage.miniMessage().escapeTags(message);
		String result = template
			.replace("%player%", player.getName())
			.replace("%message%", escaped)
			.replace("%world%", player.getWorld().getName());
		return resolvePlaceholders(result, player);
	}

	private String resolvePlaceholders(String text, Player player) {
		try {
			PlaceholdersPlugin placeholdersPlugin = JavaPlugin.getPlugin(PlaceholdersPlugin.class);
			if (placeholdersPlugin != null) {
				return placeholdersPlugin.getPlaceholderResolver().parseString(text, player);
			}
		} catch (Exception ignored) {
		}
		return text;
	}

	private String truncate(String text, int maxLen) {
		if (text.length() <= maxLen) return text;
		return text.substring(0, maxLen - 3) + "...";
	}

	public boolean isDeathEnabled() { return deathEnabled; }
	public Map<String, Integer> getWorldRadii() { return worldRadii; }
	public int getLocalDefaultRadius() { return localDefaultRadius; }
}
