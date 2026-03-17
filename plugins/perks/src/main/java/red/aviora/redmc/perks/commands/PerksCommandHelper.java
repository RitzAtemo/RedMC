package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.perks.PerksPlugin;
import red.aviora.redmc.perks.manager.CooldownManager;

public class PerksCommandHelper {

	public static void checkCooldown(Player player, String feature) throws CommandSyntaxException {
		PerksPlugin plugin = PerksPlugin.getInstance();
		CooldownManager cooldowns = plugin.getCooldownManager();
		LocaleManager locale = plugin.getLocaleManager();

		if (cooldowns.isOnCooldown(player.getUniqueId(), feature)) {
			long remaining = cooldowns.getRemainingSeconds(player.getUniqueId(), feature);
			throw new SimpleCommandExceptionType(
				MessageComponentSerializer.message().serialize(
					ApiUtils.formatText(
						locale.getMessage(player, "error.cooldown"),
						"%prefix%", locale.getMessage(player, "prefix"),
						"%seconds%", String.valueOf(remaining)
					)
				)
			).create();
		}
	}

	public static void applyCooldown(Player player, String feature) {
		PerksPlugin.getInstance().getCooldownManager().setCooldown(player.getUniqueId(), feature);
	}
}
