package red.aviora.redmc.perks.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.perks.PerksPlugin;

public class SpeedCommand implements Command<CommandSourceStack> {

	private static final float[] WALK_SPEEDS = { 0.2f, 0.4f, 0.6f, 0.8f, 1.0f };
	private static final float[] FLY_SPEEDS = { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f };

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Player player = (Player) ctx.getSource().getSender();
		PerksPlugin plugin = PerksPlugin.getInstance();

		PerksCommandHelper.checkCooldown(player, "speed");

		int level = IntegerArgumentType.getInteger(ctx, "level");
		if (level < 1 || level > 5) {
			ApiUtils.sendCommandSenderMessageArgs(player,
				plugin.getLocaleManager().getMessage(player, "speed.invalid"),
				"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix")
			);
			return SINGLE_SUCCESS;
		}

		int index = level - 1;
		player.setWalkSpeed(WALK_SPEEDS[index]);
		player.setFlySpeed(FLY_SPEEDS[index]);
		PerksCommandHelper.applyCooldown(player, "speed");

		ApiUtils.sendCommandSenderMessageArgs(player,
			plugin.getLocaleManager().getMessage(player, "speed.set"),
			"%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
			"%level%", String.valueOf(level)
		);
		return SINGLE_SUCCESS;
	}
}
