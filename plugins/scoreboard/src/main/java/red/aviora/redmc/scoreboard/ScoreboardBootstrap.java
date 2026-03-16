package red.aviora.redmc.scoreboard;

import red.aviora.redmc.scoreboard.commands.ReloadAllCommand;
import red.aviora.redmc.scoreboard.commands.ReloadConfigCommand;
import red.aviora.redmc.scoreboard.commands.ToggleCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class ScoreboardBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> {
				var commands = event.registrar();
				commands.register(makeScoreboardCommands().build());
			}
		);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeScoreboardCommands() {
		return Commands.literal("sb")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.scoreboard"))
			.then(Commands.literal("reload")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.scoreboard.reload"))
				.then(Commands.literal("config")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.scoreboard.reload.config"))
					.executes(new ReloadConfigCommand()))
				.then(Commands.literal("all")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.scoreboard.reload.all"))
					.executes(new ReloadAllCommand())))
			.then(Commands.literal("toggle")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.scoreboard.toggle"))
				.executes(new ToggleCommand()));
	}
}
