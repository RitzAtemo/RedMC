package red.aviora.redmc.npc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import red.aviora.redmc.npc.commands.NpcCommandsAddCommand;
import red.aviora.redmc.npc.commands.NpcCommandsClearCommand;
import red.aviora.redmc.npc.commands.NpcCreateCommand;
import red.aviora.redmc.npc.commands.NpcDeleteCommand;
import red.aviora.redmc.npc.commands.NpcReadCommand;
import red.aviora.redmc.npc.commands.NpcReloadAllCommand;
import red.aviora.redmc.npc.commands.NpcReloadConfigCommand;
import red.aviora.redmc.npc.commands.NpcReloadDataCommand;
import red.aviora.redmc.npc.commands.NpcSetEquipmentCommand;
import red.aviora.redmc.npc.commands.NpcSetNameCommand;
import red.aviora.redmc.npc.commands.NpcSetSkinCommand;
import red.aviora.redmc.npc.commands.NpcTeleportCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NpcBootstrap implements PluginBootstrap {

	private final List<String> ALL_ITEMS = new ArrayList<>();
	private final Map<String, List<String>> SLOT_ITEMS = new HashMap<>();

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> {
				buildItemLists();
				var commands = event.registrar();
				commands.register(buildNpcCommand("npc").build());
			}
		);
	}

	private void buildItemLists() {
		for (String slot : new String[]{"head", "chest", "legs", "feet"}) {
			SLOT_ITEMS.put(slot, new ArrayList<>());
		}
		for (Material m : Material.values()) {
			if (m.isLegacy()) continue;
			String key = m.getKey().getKey();
			ALL_ITEMS.add(key);
			try {
				EquipmentSlot slot = m.getEquipmentSlot();
				switch (slot) {
					case HEAD  -> SLOT_ITEMS.get("head").add(key);
					case CHEST -> SLOT_ITEMS.get("chest").add(key);
					case LEGS  -> SLOT_ITEMS.get("legs").add(key);
					case FEET  -> SLOT_ITEMS.get("feet").add(key);
					default    -> {}
				}
			} catch (Exception ignored) {}
		}
	}

	private LiteralArgumentBuilder<CommandSourceStack> buildNpcCommand(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.npc"))
			.then(makeCreateNode())
			.then(makeReadNode())
			.then(makeDeleteNode())
			.then(makeReloadNode())
			.then(makeUpdateNode());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeCreateNode() {
		return Commands.literal("create")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.create"))
			.then(Commands.argument("id", StringArgumentType.word())
				.executes(new NpcCreateCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReadNode() {
		return Commands.literal("read")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.read"))
			.executes(new NpcReadCommand());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeDeleteNode() {
		return Commands.literal("delete")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.delete"))
			.then(Commands.argument("id", StringArgumentType.word())
				.suggests((ctx, builder) -> suggestNpcs(builder))
				.executes(new NpcDeleteCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.reload"))
			.then(Commands.literal("config")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.reload.config"))
				.executes(new NpcReloadConfigCommand()))
			.then(Commands.literal("data")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.reload.data"))
				.executes(new NpcReloadDataCommand()))
			.then(Commands.literal("all")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.reload.all"))
				.executes(new NpcReloadAllCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeUpdateNode() {
		return Commands.literal("update")
			.then(Commands.argument("id", StringArgumentType.word())
				.suggests((ctx, builder) -> suggestNpcs(builder))
				.then(Commands.literal("name")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.update.name"))
					.then(Commands.argument("name", StringArgumentType.greedyString())
						.executes(new NpcSetNameCommand())))
				.then(Commands.literal("skin")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.update.skin"))
					.then(Commands.argument("player", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (var p : Bukkit.getOnlinePlayers()) {
								if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
									builder.suggest(p.getName());
							}
							return builder.buildFuture();
						})
						.executes(new NpcSetSkinCommand())))
				.then(Commands.literal("teleport")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.update.teleport"))
					.then(Commands.argument("player", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (var p : Bukkit.getOnlinePlayers()) {
								if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
									builder.suggest(p.getName());
							}
							return builder.buildFuture();
						})
						.executes(new NpcTeleportCommand())))
				.then(Commands.literal("equipment")
					.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.update.equipment"))
					.then(Commands.argument("slot", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (String s : new String[]{"mainhand", "offhand", "head", "chest", "legs", "feet"}) {
								if (s.startsWith(builder.getRemainingLowerCase())) builder.suggest(s);
							}
							return builder.buildFuture();
						})
						.then(Commands.argument("item", StringArgumentType.word())
							.suggests((ctx, builder) -> {
								String remaining = builder.getRemainingLowerCase();
								String slot = StringArgumentType.getString(ctx, "slot").toLowerCase();
								List<String> candidates = SLOT_ITEMS.getOrDefault(slot, ALL_ITEMS);
								for (String key : candidates) {
									if (key.contains(remaining)) builder.suggest(key);
								}
								return builder.buildFuture();
							})
							.executes(new NpcSetEquipmentCommand()))))
				.then(makeCommandsNode()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeCommandsNode() {
		return Commands.literal("commands")
			.then(Commands.literal("leftclick")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.update.commands"))
				.then(Commands.literal("add")
					.then(Commands.argument("type", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (String t : new String[]{"console", "player"}) {
								if (t.startsWith(builder.getRemainingLowerCase())) builder.suggest(t);
							}
							return builder.buildFuture();
						})
						.then(Commands.argument("command", StringArgumentType.greedyString())
							.executes(new NpcCommandsAddCommand(true)))))
				.then(Commands.literal("clear")
					.executes(new NpcCommandsClearCommand(true))))
			.then(Commands.literal("rightclick")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.update.commands"))
				.then(Commands.literal("add")
					.then(Commands.argument("type", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (String t : new String[]{"console", "player"}) {
								if (t.startsWith(builder.getRemainingLowerCase())) builder.suggest(t);
							}
							return builder.buildFuture();
						})
						.then(Commands.argument("command", StringArgumentType.greedyString())
							.executes(new NpcCommandsAddCommand(false)))))
				.then(Commands.literal("clear")
					.executes(new NpcCommandsClearCommand(false))));
	}

	private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestNpcs(
			com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
		var plugin = NpcPlugin.getInstance();
		if (plugin != null) {
			for (String id : plugin.getNpcManager().getNpcIds()) {
				if (id.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) {
					builder.suggest(id);
				}
			}
		}
		return builder.buildFuture();
	}
}
