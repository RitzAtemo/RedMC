package red.aviora.redmc.tab;

import red.aviora.redmc.tab.commands.ReloadAllCommand;
import red.aviora.redmc.tab.commands.ReloadConfigCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class TabBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> {
				var commands = event.registrar();
				commands.register(makeTabCommands().build());
			}
		);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeTabCommands() {
		return Commands.literal("tab")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.tab"))
			.then(Commands.literal("reload")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.tab.reload"))
				.then(Commands.literal("config")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.tab.reload.config"))
					.executes(new ReloadConfigCommand()))
				.then(Commands.literal("all")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.tab.reload.all"))
					.executes(new ReloadAllCommand())));
	}
}
