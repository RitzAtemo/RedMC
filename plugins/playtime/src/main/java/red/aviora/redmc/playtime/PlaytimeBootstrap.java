package red.aviora.redmc.playtime;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;
import red.aviora.redmc.playtime.commands.PlaytimeCheckCommand;
import red.aviora.redmc.playtime.commands.PlaytimeReloadAllCommand;
import red.aviora.redmc.playtime.commands.PlaytimeReloadConfigCommand;
import red.aviora.redmc.playtime.commands.PlaytimeReloadDataCommand;

@SuppressWarnings("UnstableApiUsage")
public class PlaytimeBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> event.registrar().register(buildCommand().build())
        );
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
        return Commands.literal("playtime")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.playtime"))
                .executes(new PlaytimeCheckCommand(false))
                .then(Commands.argument("player", StringArgumentType.word())
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.playtime.others"))
                        .suggests((ctx, builder) -> {
                            ctx.getSource().getSender().getServer()
                                    .getOnlinePlayers()
                                    .forEach(p -> builder.suggest(p.getName()));
                            return builder.buildFuture();
                        })
                        .executes(new PlaytimeCheckCommand(true)))
                .then(buildReloadNode());
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildReloadNode() {
        return Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.playtime.reload"))
                .then(Commands.literal("config")
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.playtime.reload.config"))
                        .executes(new PlaytimeReloadConfigCommand()))
                .then(Commands.literal("data")
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.playtime.reload.data"))
                        .executes(new PlaytimeReloadDataCommand()))
                .then(Commands.literal("all")
                        .requires(ctx -> ctx.getSender().hasPermission("redmc.playtime.reload.all"))
                        .executes(new PlaytimeReloadAllCommand()));
    }
}
