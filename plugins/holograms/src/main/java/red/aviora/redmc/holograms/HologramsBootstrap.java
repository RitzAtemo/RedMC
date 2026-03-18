package red.aviora.redmc.holograms;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import red.aviora.redmc.holograms.commands.HologramCreateCommand;
import red.aviora.redmc.holograms.commands.HologramDeleteCommand;
import red.aviora.redmc.holograms.commands.HologramLineClearCommand;
import red.aviora.redmc.holograms.commands.HologramLineAddCommand;
import red.aviora.redmc.holograms.commands.HologramLineRemoveCommand;
import red.aviora.redmc.holograms.commands.HologramLineSetCommand;
import red.aviora.redmc.holograms.commands.HologramMoveCommand;
import red.aviora.redmc.holograms.commands.HologramReadCommand;
import red.aviora.redmc.holograms.commands.HologramReloadAllCommand;
import red.aviora.redmc.holograms.commands.HologramReloadConfigCommand;
import red.aviora.redmc.holograms.commands.HologramReloadDataCommand;
import red.aviora.redmc.holograms.commands.HologramSetNameCommand;

import java.util.Locale;

public class HologramsBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> event.registrar().register(buildHologramsCommand("holograms").build())
		);
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildHologramsCommand(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms"))
			.then(makeCreateNode())
			.then(makeReadNode())
			.then(makeDeleteNode())
			.then(makeReloadNode())
			.then(makeUpdateNode());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeCreateNode() {
		return Commands.literal("create")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.create"))
			.then(Commands.argument("id", StringArgumentType.word())
				.executes(new HologramCreateCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReadNode() {
		return Commands.literal("read")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.read"))
			.executes(new HologramReadCommand());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeDeleteNode() {
		return Commands.literal("delete")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.delete"))
			.then(Commands.argument("id", StringArgumentType.word())
				.suggests((ctx, builder) -> suggestHolograms(builder))
				.executes(new HologramDeleteCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.reload"))
			.then(Commands.literal("config")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.reload.config"))
				.executes(new HologramReloadConfigCommand()))
			.then(Commands.literal("data")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.reload.data"))
				.executes(new HologramReloadDataCommand()))
			.then(Commands.literal("all")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.reload.all"))
				.executes(new HologramReloadAllCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeUpdateNode() {
		return Commands.literal("update")
			.then(Commands.argument("id", StringArgumentType.word())
				.suggests((ctx, builder) -> suggestHolograms(builder))
				.then(Commands.literal("name")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.update.name"))
					.then(Commands.argument("name", StringArgumentType.greedyString())
						.executes(new HologramSetNameCommand())))
				.then(Commands.literal("location")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.update.location"))
					.executes(new HologramMoveCommand()))
				.then(makeLinesNode()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeLinesNode() {
		return Commands.literal("lines")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.holograms.update.lines"))
			.then(Commands.literal("add")
				.then(Commands.argument("text", StringArgumentType.greedyString())
					.executes(new HologramLineAddCommand())))
			.then(Commands.literal("set")
				.then(Commands.argument("index", IntegerArgumentType.integer(0))
					.then(Commands.argument("text", StringArgumentType.greedyString())
						.executes(new HologramLineSetCommand()))))
			.then(Commands.literal("remove")
				.then(Commands.argument("index", IntegerArgumentType.integer(0))
					.executes(new HologramLineRemoveCommand())))
			.then(Commands.literal("clear")
				.executes(new HologramLineClearCommand()));
	}

	private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestHolograms(
			com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
		HologramsPlugin plugin = HologramsPlugin.getInstance();
		if (plugin != null) {
			for (String id : plugin.getHologramManager().getHologramIds()) {
				if (id.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) {
					builder.suggest(id);
				}
			}
		}
		return builder.buildFuture();
	}
}
