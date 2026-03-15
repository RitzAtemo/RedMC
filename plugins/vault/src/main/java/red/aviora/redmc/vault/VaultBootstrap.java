package red.aviora.redmc.vault;

import red.aviora.redmc.vault.commands.balance.MyBalanceCommand;
import red.aviora.redmc.vault.commands.balance.BaltopCommand;
import red.aviora.redmc.vault.commands.group.*;
import red.aviora.redmc.vault.commands.pay.PayCommand;
import red.aviora.redmc.vault.commands.player.*;
import red.aviora.redmc.vault.commands.reload.*;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.bukkit.Bukkit;

public class VaultBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS,
			event -> {
				var commands = event.registrar();
				commands.register(makeVaultCommands("vault").build());
				commands.register(makeVaultCommands("v").build());
				commands.register(makePayCommand().build());
				commands.register(makeBalanceCommand().build());
				commands.register(makeBaltopCommand().build());
			}
		);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeVaultCommands(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.vault"))
			.then(makeReloadNode())
			.then(makeGroupNode())
			.then(makePlayerNode());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.reload"))
			.then(Commands.literal("config").executes(new ReloadConfigCommand()))
			.then(Commands.literal("data").executes(new ReloadDataCommand()))
			.then(Commands.literal("all").executes(new ReloadAllCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeGroupNode() {
		return Commands.literal("group")
			.then(Commands.argument("group", StringArgumentType.word())
				.suggests((context, builder) -> {
					try {
						var permissionManager = VaultPlugin.getInstance().getConfigManager();
						var permPlugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(red.aviora.redmc.permissions.PermissionsPlugin.class);
						if (permPlugin != null) {
							var groups = permPlugin.getPermissionManager().getGroups();
							for (String groupId : groups.keySet()) {
								if (groupId.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
									builder.suggest(groupId);
								}
							}
						}
					} catch (Exception ignored) {
					}
					return builder.buildFuture();
				})
				.then(makePrefixNode())
				.then(makeSuffixNode())
			);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePrefixNode() {
		return Commands.literal("prefix")
			.then(Commands.literal("set")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.group.prefix.set"))
				.then(Commands.argument("weight", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
						.then(Commands.argument("prefix", StringArgumentType.greedyString())
							.executes(new GroupPrefixSetCommand()))))
			.then(Commands.literal("get")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.group.prefix.get"))
				.executes(new GroupPrefixGetCommand()))
			.then(Commands.literal("remove")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.group.prefix.remove"))
				.executes(new GroupPrefixRemoveCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeSuffixNode() {
		return Commands.literal("suffix")
			.then(Commands.literal("set")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.group.suffix.set"))
				.then(Commands.argument("weight", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
					.then(Commands.argument("suffix", StringArgumentType.greedyString())
						.executes(new GroupSuffixSetCommand()))))
			.then(Commands.literal("get")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.group.suffix.get"))
				.executes(new GroupSuffixGetCommand()))
			.then(Commands.literal("remove")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.group.suffix.remove"))
				.executes(new GroupSuffixRemoveCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePlayerNode() {
		return Commands.literal("player")
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((context, builder) -> {
					for (var player : Bukkit.getOnlinePlayers()) {
						String name = player.getName();
						if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
							builder.suggest(name);
						}
					}
					return builder.buildFuture();
				})
				.then(makePlayerPrefixNode())
				.then(makePlayerSuffixNode())
				.then(makePlayerAltnameNode())
				.then(makePlayerEconomyNode())
			);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePlayerPrefixNode() {
		return Commands.literal("prefix")
			.then(Commands.literal("set")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.prefix.set"))
				.then(Commands.argument("prefix", StringArgumentType.greedyString())
					.then(Commands.argument("weight", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
						.executes(new PlayerPrefixSetCommand()))))
			.then(Commands.literal("get")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.prefix.get"))
				.executes(new PlayerPrefixGetCommand()))
			.then(Commands.literal("remove")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.prefix.remove"))
				.executes(new PlayerPrefixRemoveCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePlayerSuffixNode() {
		return Commands.literal("suffix")
			.then(Commands.literal("set")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.suffix.set"))
				.then(Commands.argument("suffix", StringArgumentType.greedyString())
					.then(Commands.argument("weight", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
						.executes(new PlayerSuffixSetCommand()))))
			.then(Commands.literal("get")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.suffix.get"))
				.executes(new PlayerSuffixGetCommand()))
			.then(Commands.literal("remove")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.suffix.remove"))
				.executes(new PlayerSuffixRemoveCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePlayerAltnameNode() {
		return Commands.literal("altname")
			.then(Commands.literal("set")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.altname.set"))
				.then(Commands.argument("name", StringArgumentType.greedyString())
					.then(Commands.argument("weight", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
						.executes(new PlayerAltnameSetCommand()))))
			.then(Commands.literal("get")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.altname.get"))
				.executes(new PlayerAltnameGetCommand()))
			.then(Commands.literal("remove")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.altname.remove"))
				.executes(new PlayerAltnameRemoveCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePlayerEconomyNode() {
		return Commands.literal("economy")
			.then(Commands.literal("balance")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.economy.balance"))
				.executes(new PlayerEconomyBalanceCommand())
				.then(Commands.argument("currency", StringArgumentType.word())
					.suggests((context, builder) -> suggestCurrencies(builder))
					.executes(new PlayerEconomyBalanceCommand())))
			.then(Commands.literal("set")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.economy.set"))
				.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
					.executes(new PlayerEconomySetCommand())
					.then(Commands.argument("currency", StringArgumentType.word())
						.suggests((context, builder) -> suggestCurrencies(builder))
						.executes(new PlayerEconomySetCommand()))))
			.then(Commands.literal("add")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.economy.add"))
				.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
					.executes(new PlayerEconomyAddCommand())
					.then(Commands.argument("currency", StringArgumentType.word())
						.suggests((context, builder) -> suggestCurrencies(builder))
						.executes(new PlayerEconomyAddCommand()))))
			.then(Commands.literal("subtract")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.player.economy.subtract"))
				.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
					.executes(new PlayerEconomySubtractCommand())
					.then(Commands.argument("currency", StringArgumentType.word())
						.suggests((context, builder) -> suggestCurrencies(builder))
						.executes(new PlayerEconomySubtractCommand()))));
	}

	private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestCurrencies(com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
		var currencies = VaultPlugin.getInstance().getVaultManager().getCurrencyManager().getAllCurrencies();
		for (var currency : currencies.values()) {
			if (currency.getId().toLowerCase().startsWith(builder.getRemainingLowerCase())) {
				builder.suggest(currency.getId());
			}
		}
		return builder.buildFuture();
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePayCommand() {
		return Commands.literal("pay")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.pay"))
			.then(Commands.argument("player", StringArgumentType.word())
				.suggests((context, builder) -> {
					for (var player : Bukkit.getOnlinePlayers()) {
						String name = player.getName();
						if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
							builder.suggest(name);
						}
					}
					return builder.buildFuture();
				})
				.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
					.executes(new PayCommand())
					.then(Commands.argument("currency", StringArgumentType.word())
						.suggests((context, builder) -> suggestCurrencies(builder))
						.executes(new PayCommand()))));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeBalanceCommand() {
		return Commands.literal("balance")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.balance"))
			.executes(new MyBalanceCommand())
			.then(Commands.argument("currency", StringArgumentType.word())
				.suggests((context, builder) -> suggestCurrencies(builder))
				.executes(new MyBalanceCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeBaltopCommand() {
		return Commands.literal("baltop")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.vault.baltop"))
			.executes(new BaltopCommand())
			.then(Commands.argument("currency", StringArgumentType.word())
				.suggests((context, builder) -> suggestCurrencies(builder))
				.executes(new BaltopCommand()));
	}
}
