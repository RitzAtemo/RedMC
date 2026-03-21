package red.aviora.redmc.tracker;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;
import red.aviora.redmc.tracker.commands.TrackerReloadAllCommand;
import red.aviora.redmc.tracker.commands.TrackerReloadConfigCommand;
import red.aviora.redmc.tracker.commands.TrackerReloadDataCommand;
import red.aviora.redmc.tracker.commands.TrackerStartCommand;
import red.aviora.redmc.tracker.commands.TrackerStopCommand;

@SuppressWarnings("UnstableApiUsage")
public class TrackerBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> event.registrar().register(buildTrackerCommand().build())
        );
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildTrackerCommand() {
        return Commands.literal("tracker")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.tracker"))
                .then(Commands.literal("start")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    ctx.getSource().getSender().getServer()
                                            .getOnlinePlayers()
                                            .forEach(p -> builder.suggest(p.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(new TrackerStartCommand())))
                .then(Commands.literal("stop")
                        .executes(new TrackerStopCommand()))
                .then(makeReloadNode());
    }

    private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
        return Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.tracker.reload"))
                .then(Commands.literal("config")
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.tracker.reload.config"))
                        .executes(new TrackerReloadConfigCommand()))
                .then(Commands.literal("data")
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.tracker.reload.data"))
                        .executes(new TrackerReloadDataCommand()))
                .then(Commands.literal("all")
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.tracker.reload.all"))
                        .executes(new TrackerReloadAllCommand()));
    }
}
