package red.aviora.redmc.perks;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import red.aviora.redmc.perks.commands.*;

public class PerksBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
			var commands = event.registrar();

			commands.register(simplePlayerCommand("workbench", "redmc.perks.workbench", new WorkbenchCommand()).build(), java.util.List.of("wb"));
			commands.register(simplePlayerCommand("anvil", "redmc.perks.anvil", new AnvilCommand()).build());
			commands.register(simplePlayerCommand("enchant", "redmc.perks.enchant", new EnchantCommand()).build());
			commands.register(simplePlayerCommand("grindstone", "redmc.perks.grindstone", new GrindstoneCommand()).build());
			commands.register(simplePlayerCommand("stonecutter", "redmc.perks.stonecutter", new StonecutterCommand()).build());
			commands.register(simplePlayerCommand("smithing", "redmc.perks.smithing", new SmithingCommand()).build());
			commands.register(simplePlayerCommand("loom", "redmc.perks.loom", new LoomCommand()).build());
			commands.register(simplePlayerCommand("cartography", "redmc.perks.cartography", new CartographyCommand()).build());
			commands.register(simplePlayerCommand("enderchest", "redmc.perks.enderchest", new EnderChestCommand()).build(), java.util.List.of("ec"));
			commands.register(simplePlayerCommand("dispose", "redmc.perks.dispose", new DisposeCommand()).build(), java.util.List.of("trash"));
			commands.register(buildRepairCommand().build());
			commands.register(simplePlayerCommand("hat", "redmc.perks.hat", new HatCommand()).build());
			commands.register(simplePlayerCommand("backpack", "redmc.perks.backpack", new BackpackCommand()).build(), java.util.List.of("bp"));
			commands.register(buildRenameCommand().build());
			commands.register(simplePlayerCommand("fly", "redmc.perks.fly", new FlyCommand()).build());
			commands.register(buildSpeedCommand().build());
			commands.register(simplePlayerCommand("feed", "redmc.perks.feed", new FeedCommand()).build());
			commands.register(simplePlayerCommand("heal", "redmc.perks.heal", new HealCommand()).build());
			commands.register(simplePlayerCommand("nofall", "redmc.perks.nofall", new NoFallCommand()).build());
			commands.register(buildBroadcastCommand().build(), java.util.List.of("bc"));
			commands.register(buildPerksCommand().build());
			commands.register(buildInvseeCommand().build());
			commands.register(simplePlayerCommand("vanish", "redmc.perks.admin.vanish", new VanishCommand()).build(), java.util.List.of("v"));
			commands.register(simplePlayerCommand("god", "redmc.perks.admin.god", new GodCommand()).build());
			commands.register(buildFreezeCommand().build());
			commands.register(buildTpoCommand().build());
			commands.register(buildTpoHereCommand().build());
			commands.register(buildSudoCommand().build());
		});
	}

	private LiteralArgumentBuilder<CommandSourceStack> simplePlayerCommand(
		String label, String permission, com.mojang.brigadier.Command<CommandSourceStack> executor
	) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission(permission))
			.executes(executor);
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildRepairCommand() {
		return Commands.literal("repair")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.repair"))
			.executes(new RepairCommand())
			.then(Commands.literal("all")
				.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.repair.all"))
				.executes(new RepairAllCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildRenameCommand() {
		return Commands.literal("rename")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.rename"))
			.then(Commands.argument("name", StringArgumentType.greedyString())
				.executes(new RenameCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildSpeedCommand() {
		return Commands.literal("speed")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.speed"))
			.then(Commands.argument("level", IntegerArgumentType.integer(1, 5))
				.executes(new SpeedCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildInvseeCommand() {
		return Commands.literal("invsee")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.admin.invsee"))
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((ctx, builder) -> {
					for (var p : org.bukkit.Bukkit.getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
							builder.suggest(p.getName());
					}
					return builder.buildFuture();
				})
				.executes(new InvseeCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildFreezeCommand() {
		return Commands.literal("freeze")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.admin.freeze"))
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((ctx, builder) -> {
					for (var p : org.bukkit.Bukkit.getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
							builder.suggest(p.getName());
					}
					return builder.buildFuture();
				})
				.executes(new FreezeCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildTpoCommand() {
		return Commands.literal("tpo")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.admin.tpo"))
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((ctx, builder) -> {
					for (var p : org.bukkit.Bukkit.getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
							builder.suggest(p.getName());
					}
					return builder.buildFuture();
				})
				.executes(new TpoCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildTpoHereCommand() {
		return Commands.literal("tpohere")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.admin.tpohere"))
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((ctx, builder) -> {
					for (var p : org.bukkit.Bukkit.getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
							builder.suggest(p.getName());
					}
					return builder.buildFuture();
				})
				.executes(new TpoHereCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildSudoCommand() {
		return Commands.literal("sudo")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.admin.sudo"))
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((ctx, builder) -> {
					for (var p : org.bukkit.Bukkit.getOnlinePlayers()) {
						if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
							builder.suggest(p.getName());
					}
					return builder.buildFuture();
				})
				.then(Commands.argument("command", StringArgumentType.greedyString())
					.executes(new SudoCommand())));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildBroadcastCommand() {
		return Commands.literal("broadcast")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.broadcast"))
			.then(Commands.argument("message", StringArgumentType.greedyString())
				.executes(new BroadcastCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildPerksCommand() {
		return Commands.literal("perks")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.perks.reload"))
			.then(makeSetJoinNode())
			.then(makeSetQuitNode())
			.then(makeReloadNode());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeSetJoinNode() {
		return Commands.literal("setjoin")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.setjoin"))
			.then(Commands.literal("reset").executes(new PerksSetJoinResetCommand()))
			.then(Commands.argument("message", StringArgumentType.greedyString()).executes(new PerksSetJoinCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeSetQuitNode() {
		return Commands.literal("setquit")
			.requires(ctx -> ctx.getSender() instanceof Player && ctx.getSender().hasPermission("redmc.perks.setquit"))
			.then(Commands.literal("reset").executes(new PerksSetQuitResetCommand()))
			.then(Commands.argument("message", StringArgumentType.greedyString()).executes(new PerksSetQuitCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.perks.reload"))
			.then(Commands.literal("config")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.perks.reload.config"))
				.executes(new PerksReloadConfigCommand()))
			.then(Commands.literal("data")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.perks.reload.data"))
				.executes(new PerksReloadDataCommand()))
			.then(Commands.literal("all")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.perks.reload.all"))
				.executes(new PerksReloadAllCommand()));
	}
}
