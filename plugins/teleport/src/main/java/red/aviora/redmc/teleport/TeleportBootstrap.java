package red.aviora.redmc.teleport;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import red.aviora.redmc.teleport.commands.back.BackCommand;
import red.aviora.redmc.teleport.commands.home.HomeDeleteCommand;
import red.aviora.redmc.teleport.commands.home.HomeGoCommand;
import red.aviora.redmc.teleport.commands.home.HomeListCommand;
import red.aviora.redmc.teleport.commands.home.HomeSetCommand;
import red.aviora.redmc.teleport.commands.reload.ReloadAllCommand;
import red.aviora.redmc.teleport.commands.reload.ReloadConfigCommand;
import red.aviora.redmc.teleport.commands.reload.ReloadDataCommand;
import red.aviora.redmc.teleport.commands.rtp.RtpCommand;
import red.aviora.redmc.teleport.commands.spawn.SetNewbieSpawnCommand;
import red.aviora.redmc.teleport.commands.spawn.SetSpawnCommand;
import red.aviora.redmc.teleport.commands.spawn.SpawnCommand;
import red.aviora.redmc.teleport.commands.tpa.TpAcceptCommand;
import red.aviora.redmc.teleport.commands.tpa.TpCancelCommand;
import red.aviora.redmc.teleport.commands.tpa.TpDenyCommand;
import red.aviora.redmc.teleport.commands.tpa.TpaCommand;
import red.aviora.redmc.teleport.commands.tpa.TpaHereCommand;
import red.aviora.redmc.teleport.commands.warp.WarpCreateCommand;
import red.aviora.redmc.teleport.commands.warp.WarpDeleteCommand;
import red.aviora.redmc.teleport.commands.warp.WarpListCommand;
import red.aviora.redmc.teleport.commands.warp.WarpTeleportCommand;

public class TeleportBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS,
            event -> {
                var commands = event.registrar();

                commands.register(makeTeleportCommand().build());
                commands.register(buildSpawnCommand("spawn"));
                commands.register(buildSetSpawnCommand("setspawn"));
                commands.register(buildWarpCommand("warp"));
                commands.register(buildHomeCommand("home"));
                commands.register(buildBackCommand("back"));
                commands.register(buildRtpCommand("rtp"));
                commands.register(buildTpaCommand("tpa"));
                commands.register(buildTpaHereCommand("tpahere"));
                commands.register(buildTpAcceptCommand("tpaccept"));
                commands.register(buildTpDenyCommand("tpdeny"));
                commands.register(buildTpCancelCommand("tpcancel"));
            }
        );
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeTeleportCommand() {
        return Commands.literal("teleport")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.reload"))
            .then(makeReloadNode());
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
        return Commands.literal("reload")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.reload"))
            .then(Commands.literal("config")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.reload.config"))
                .executes(new ReloadConfigCommand()))
            .then(Commands.literal("data")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.reload.data"))
                .executes(new ReloadDataCommand()))
            .then(Commands.literal("all")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.reload.all"))
                .executes(new ReloadAllCommand()));
    }

    private LiteralCommandNode<CommandSourceStack> buildSpawnCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.spawn"))
            .executes(new SpawnCommand())
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildSetSpawnCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.spawn.set"))
            .executes(new SetSpawnCommand())
            .then(Commands.literal("newbie")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.spawn.set.newbie"))
                .executes(new SetNewbieSpawnCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildWarpCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.warp"))
            .executes(new WarpListCommand())
            .then(Commands.literal("create")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.warp.create"))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(new WarpCreateCommand())))
            .then(Commands.literal("delete")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.warp.delete"))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(new WarpDeleteCommand())))
            .then(Commands.literal("list")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.warp"))
                .executes(new WarpListCommand()))
            .then(Commands.argument("name", StringArgumentType.word())
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.warp"))
                .executes(new WarpTeleportCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildHomeCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.home"))
            .executes(new HomeGoCommand(false))
            .then(Commands.literal("set")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.home.set"))
                .executes(new HomeSetCommand(false))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(new HomeSetCommand(true))))
            .then(Commands.literal("delete")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.home.delete"))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(new HomeDeleteCommand())))
            .then(Commands.literal("list")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.home"))
                .executes(new HomeListCommand()))
            .then(Commands.argument("name", StringArgumentType.word())
                .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.home"))
                .executes(new HomeGoCommand(true)))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildBackCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.back"))
            .executes(new BackCommand())
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildRtpCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.rtp"))
            .executes(new RtpCommand(false))
            .then(Commands.argument("world", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (var world : Bukkit.getWorlds()) {
                        String name = world.getName();
                        if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                            builder.suggest(name);
                        }
                    }
                    return builder.buildFuture();
                })
                .executes(new RtpCommand(true)))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTpaCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.tpa"))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (var player : Bukkit.getOnlinePlayers()) {
                        String name = player.getName();
                        if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                            builder.suggest(name);
                        }
                    }
                    return builder.buildFuture();
                })
                .executes(new TpaCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTpaHereCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.tpa.here"))
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    for (var player : Bukkit.getOnlinePlayers()) {
                        String name = player.getName();
                        if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                            builder.suggest(name);
                        }
                    }
                    return builder.buildFuture();
                })
                .executes(new TpaHereCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTpAcceptCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.tpa.accept"))
            .executes(new TpAcceptCommand())
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTpDenyCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.tpa.deny"))
            .executes(new TpDenyCommand())
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTpCancelCommand(String label) {
        return Commands.literal(label)
            .requires(ctx -> ctx.getSender().hasPermission("redmc.teleport.tpa.cancel"))
            .executes(new TpCancelCommand())
            .build();
    }
}
