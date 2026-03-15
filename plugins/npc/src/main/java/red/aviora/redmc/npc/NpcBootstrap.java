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
import red.aviora.redmc.npc.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
			.then(Commands.literal("create")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.create"))
				.then(Commands.argument("id", StringArgumentType.word())
					.executes(new NpcCreateCommand())))
			.then(Commands.literal("delete")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.delete"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.executes(new NpcDeleteCommand())))
			.then(Commands.literal("setname")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.setname"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.then(Commands.argument("name", StringArgumentType.greedyString())
						.executes(new NpcSetNameCommand()))))
			.then(Commands.literal("setskin")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.setskin"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.then(Commands.argument("player", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (var p : Bukkit.getOnlinePlayers()) {
								if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
									builder.suggest(p.getName());
							}
							return builder.buildFuture();
						})
						.executes(new NpcSetSkinCommand()))))
			.then(Commands.literal("teleport")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.teleport"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.then(Commands.argument("player", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (var p : Bukkit.getOnlinePlayers()) {
								if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
									builder.suggest(p.getName());
							}
							return builder.buildFuture();
						})
						.executes(new NpcTeleportCommand()))))
			.then(Commands.literal("list")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.list"))
				.executes(new NpcListCommand()))
			.then(Commands.literal("reload")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.reload"))
				.executes(new NpcReloadCommand()))
			.then(Commands.literal("addleftclick")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.addcommand"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.then(Commands.argument("type", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (String t : new String[]{"console", "player"}) {
								if (t.startsWith(builder.getRemainingLowerCase())) builder.suggest(t);
							}
							return builder.buildFuture();
						})
						.then(Commands.argument("command", StringArgumentType.greedyString())
							.executes(new NpcAddCommandCommand(true))))))
			.then(Commands.literal("addrightclick")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.addcommand"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.then(Commands.argument("type", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							for (String t : new String[]{"console", "player"}) {
								if (t.startsWith(builder.getRemainingLowerCase())) builder.suggest(t);
							}
							return builder.buildFuture();
						})
						.then(Commands.argument("command", StringArgumentType.greedyString())
							.executes(new NpcAddCommandCommand(false))))))
			.then(Commands.literal("clearleftclick")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.addcommand"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.executes(new NpcClearCommandsCommand(true))))
			.then(Commands.literal("clearrightclick")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.addcommand"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
					.executes(new NpcClearCommandsCommand(false))))
			.then(Commands.literal("setequipment")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.npc.setequipment"))
				.then(Commands.argument("id", StringArgumentType.word())
					.suggests((ctx, builder) -> suggestNpcs(builder))
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
							.executes(new NpcSetEquipmentCommand())))));
	}

	private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestNpcs(
			com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
		var plugin = NpcPlugin.getInstance();
		if (plugin != null) {
			for (String id : plugin.getNpcManager().getNpcIds()) {
				if (id.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
					builder.suggest(id);
				}
			}
		}
		return builder.buildFuture();
	}
}
