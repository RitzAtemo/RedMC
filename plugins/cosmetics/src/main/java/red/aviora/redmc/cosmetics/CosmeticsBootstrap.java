package red.aviora.redmc.cosmetics;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import red.aviora.redmc.cosmetics.command.AddLayerCommand;
import red.aviora.redmc.cosmetics.command.MenuCommand;
import red.aviora.redmc.cosmetics.command.AdminGiveCommand;
import red.aviora.redmc.cosmetics.command.AdminResetCommand;
import red.aviora.redmc.cosmetics.command.CreateCommand;
import red.aviora.redmc.cosmetics.command.DeleteCommand;
import red.aviora.redmc.cosmetics.command.EquipCommand;
import red.aviora.redmc.cosmetics.command.EquippedCommand;
import red.aviora.redmc.cosmetics.command.ExportCommand;
import red.aviora.redmc.cosmetics.command.ImportCommand;
import red.aviora.redmc.cosmetics.command.InfoCommand;
import red.aviora.redmc.cosmetics.command.ListCommand;
import red.aviora.redmc.cosmetics.command.ReloadCommand;
import red.aviora.redmc.cosmetics.command.RemoveLayerCommand;
import red.aviora.redmc.cosmetics.command.SetLayerCommand;
import red.aviora.redmc.cosmetics.command.ToggleCommand;
import red.aviora.redmc.cosmetics.command.UnequipCommand;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.ParticleShape;

import java.util.Arrays;

public class CosmeticsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var registrar = event.registrar();
            registrar.register(buildCosmeticsCommand("cosmetics").build(), "Cosmetics plugin", java.util.List.of("cos"));
        });
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildCosmeticsCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics"))
            .executes(new MenuCommand())
            .then(makeMenuNode())
            .then(makeEquipNode())
            .then(makeUnequipNode())
            .then(makeListNode())
            .then(makeEquippedNode())
            .then(makeToggleNode())
            .then(makeCreateNode())
            .then(makeDeleteNode())
            .then(makeInfoNode())
            .then(makeEditNode())
            .then(makeExportNode())
            .then(makeImportNode())
            .then(makeAdminNode())
            .then(makeReloadNode());
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeMenuNode() {
        return Commands.literal("menu")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics"))
            .executes(new MenuCommand());
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeEquipNode() {
        return Commands.literal("equip")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.equip"))
            .then(Commands.argument("slot", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (CosmeticSlot s : CosmeticSlot.values()) {
                        if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            builder.suggest(s.name().toLowerCase());
                    }
                    return builder.buildFuture();
                })
                .then(Commands.argument("template", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        String slotStr = StringArgumentType.getString(ctx, "slot");
                        CosmeticSlot slot = CosmeticSlot.fromString(slotStr).orElse(null);
                        if (slot != null && ctx.getSource().getSender() instanceof org.bukkit.entity.Player p) {
                            try {
                                CosmeticsPlugin plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(CosmeticsPlugin.class);
                                if (plugin != null) {
                                    for (String name : plugin.getTemplateManager().getNamesForSlot(p.getUniqueId(), slot)) {
                                        if (name.startsWith(builder.getRemainingLowerCase()))
                                            builder.suggest(name);
                                    }
                                }
                            } catch (Exception ignored) {}
                        }
                        return builder.buildFuture();
                    })
                    .executes(new EquipCommand())));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeUnequipNode() {
        return Commands.literal("unequip")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.equip"))
            .then(Commands.literal("all")
                .executes(new UnequipCommand(true)))
            .then(Commands.argument("slot", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (CosmeticSlot s : CosmeticSlot.values()) {
                        if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            builder.suggest(s.name().toLowerCase());
                    }
                    return builder.buildFuture();
                })
                .executes(new UnequipCommand(false)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeListNode() {
        return Commands.literal("list")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.list"))
            .executes(new ListCommand(false))
            .then(Commands.argument("slot", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (CosmeticSlot s : CosmeticSlot.values()) {
                        if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            builder.suggest(s.name().toLowerCase());
                    }
                    return builder.buildFuture();
                })
                .executes(new ListCommand(true)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeEquippedNode() {
        return Commands.literal("equipped")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.equip"))
            .executes(new EquippedCommand());
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeToggleNode() {
        return Commands.literal("toggle")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.toggle"))
            .executes(new ToggleCommand());
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeCreateNode() {
        return Commands.literal("create")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.create"))
            .then(Commands.argument("name", StringArgumentType.word())
                .then(Commands.argument("slot", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (CosmeticSlot s : CosmeticSlot.values()) {
                            if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                builder.suggest(s.name().toLowerCase());
                        }
                        return builder.buildFuture();
                    })
                    .executes(new CreateCommand())));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeDeleteNode() {
        return Commands.literal("delete")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.delete"))
            .then(templateArgument()
                .executes(new DeleteCommand()));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeInfoNode() {
        return Commands.literal("info")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.list"))
            .then(templateArgument()
                .executes(new InfoCommand()));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeEditNode() {
        return Commands.literal("edit")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.edit"))
            .then(templateArgument()
                .then(makeAddLayerNode())
                .then(makeRemoveLayerNode())
                .then(makeSetLayerNode()));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeAddLayerNode() {
        return Commands.literal("addlayer")
            .then(Commands.argument("particle", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String rem = builder.getRemainingLowerCase();
                    for (Particle p : Particle.values()) {
                        if (p.name().toLowerCase().startsWith(rem))
                            builder.suggest(p.name().toLowerCase());
                    }
                    return builder.buildFuture();
                })
                .then(Commands.argument("shape", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (ParticleShape s : ParticleShape.values()) {
                            if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                builder.suggest(s.name().toLowerCase());
                        }
                        return builder.buildFuture();
                    })
                    .executes(new AddLayerCommand())));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeRemoveLayerNode() {
        return Commands.literal("removelayer")
            .then(Commands.argument("index", IntegerArgumentType.integer(0))
                .executes(new RemoveLayerCommand()));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeSetLayerNode() {
        return Commands.literal("setlayer")
            .then(Commands.argument("index", IntegerArgumentType.integer(0))
                .then(Commands.literal("particle")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (Particle p : Particle.values()) {
                                if (p.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(p.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .executes(new SetLayerCommand(SetLayerCommand.Property.PARTICLE))))
                .then(Commands.literal("shape")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (ParticleShape s : ParticleShape.values()) {
                                if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(s.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .executes(new SetLayerCommand(SetLayerCommand.Property.SHAPE))))
                .then(Commands.literal("count")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 50))
                        .executes(new SetLayerCommand(SetLayerCommand.Property.COUNT))))
                .then(Commands.literal("speed")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.SPEED))))
                .then(Commands.literal("yoffset")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.YOFFSET))))
                .then(Commands.literal("tickrate")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 100))
                        .executes(new SetLayerCommand(SetLayerCommand.Property.TICKRATE))))
                .then(Commands.literal("radius")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.RADIUS))))
                .then(Commands.literal("points")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 128))
                        .executes(new SetLayerCommand(SetLayerCommand.Property.POINTS))))
                .then(Commands.literal("offsetx")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.OFFSETX))))
                .then(Commands.literal("offsety")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.OFFSETY))))
                .then(Commands.literal("offsetz")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.OFFSETZ))))
                .then(Commands.literal("color")
                    .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                            .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                .executes(new SetLayerCommand(SetLayerCommand.Property.COLOR))))))
                .then(Commands.literal("colorto")
                    .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                            .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                .executes(new SetLayerCommand(SetLayerCommand.Property.COLORTO))))))
                .then(Commands.literal("dustsize")
                    .then(Commands.argument("value", StringArgumentType.word())
                        .executes(new SetLayerCommand(SetLayerCommand.Property.DUSTSIZE)))));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeExportNode() {
        return Commands.literal("export")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.export"))
            .then(templateArgument()
                .executes(new ExportCommand()));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeImportNode() {
        return Commands.literal("import")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.import"))
            .then(Commands.argument("signature", StringArgumentType.greedyString())
                .executes(new ImportCommand()));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeAdminNode() {
        return Commands.literal("admin")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.admin"))
            .then(Commands.literal("give")
                .then(Commands.argument("player", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (var p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                builder.suggest(p.getName());
                        }
                        return builder.buildFuture();
                    })
                    .then(Commands.argument("slot", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (CosmeticSlot s : CosmeticSlot.values()) {
                                if (s.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    builder.suggest(s.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("template", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                try {
                                    CosmeticsPlugin plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(CosmeticsPlugin.class);
                                    if (plugin != null) {
                                        String pName = StringArgumentType.getString(ctx, "player");
                                        org.bukkit.entity.Player target = Bukkit.getPlayerExact(pName);
                                        if (target != null) {
                                            for (String name : plugin.getTemplateManager().getNames(target.getUniqueId())) {
                                                if (name.startsWith(builder.getRemainingLowerCase()))
                                                    builder.suggest(name);
                                            }
                                        }
                                    }
                                } catch (Exception ignored) {}
                                return builder.buildFuture();
                            })
                            .executes(new AdminGiveCommand())))))
            .then(Commands.literal("reset")
                .then(Commands.argument("player", StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        for (var p : Bukkit.getOnlinePlayers()) {
                            if (p.getName().toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                builder.suggest(p.getName());
                        }
                        return builder.buildFuture();
                    })
                    .executes(new AdminResetCommand())));
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
        return Commands.literal("reload")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.reload"))
            .then(Commands.literal("config")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.reload.config"))
                .executes(new ReloadCommand(ReloadCommand.Mode.CONFIG)))
            .then(Commands.literal("data")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.reload.data"))
                .executes(new ReloadCommand(ReloadCommand.Mode.DATA)))
            .then(Commands.literal("all")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.cosmetics.reload.all"))
                .executes(new ReloadCommand(ReloadCommand.Mode.ALL)));
    }

    private RequiredArgumentBuilder<CommandSourceStack, String> templateArgument() {
        return Commands.argument("name", StringArgumentType.word())
            .suggests((ctx, builder) -> {
                if (!(ctx.getSource().getSender() instanceof org.bukkit.entity.Player p)) return builder.buildFuture();
                try {
                    CosmeticsPlugin plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(CosmeticsPlugin.class);
                    if (plugin != null) {
                        for (String name : plugin.getTemplateManager().getNames(p.getUniqueId())) {
                            if (name.startsWith(builder.getRemainingLowerCase()))
                                builder.suggest(name);
                        }
                    }
                } catch (Exception ignored) {}
                return builder.buildFuture();
            });
    }
}
