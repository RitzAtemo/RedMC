package red.aviora.redmc.motd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import red.aviora.redmc.motd.commands.ReloadAllCommand;

public class MotdBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> event.registrar().register(buildMotdCommand("motd").build())
		);
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildMotdCommand(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.motd"))
			.then(makeReloadNode());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.motd.reload"))
			.then(Commands.literal("all")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.motd.reload.all"))
				.executes(new ReloadAllCommand()));
	}
}
