package red.aviora.redmc.moderation;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import red.aviora.redmc.moderation.commands.BanCommand;
import red.aviora.redmc.moderation.commands.HistoryCommand;
import red.aviora.redmc.moderation.commands.MuteCommand;
import red.aviora.redmc.moderation.commands.TicketCommand;
import red.aviora.redmc.moderation.commands.TicketsCommand;
import red.aviora.redmc.moderation.commands.UnbanCommand;
import red.aviora.redmc.moderation.commands.UnmuteCommand;
import red.aviora.redmc.moderation.commands.WarnCommand;
import red.aviora.redmc.moderation.commands.reload.ReloadAllCommand;
import red.aviora.redmc.moderation.commands.reload.ReloadConfigCommand;
import red.aviora.redmc.moderation.commands.reload.ReloadDataCommand;

@SuppressWarnings("UnstableApiUsage")
public class ModerationBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS,
            event -> {
                var commands = event.registrar();

                commands.register(buildWarnCommand());
                commands.register(buildMuteCommand());
                commands.register(buildUnmuteCommand());
                commands.register(buildBanCommand());
                commands.register(buildUnbanCommand());
                commands.register(buildHistoryCommand());
                commands.register(buildTicketCommand());
                commands.register(buildTicketsCommand());
                commands.register(buildModerationCommand().build());
            }
        );
    }

    private LiteralCommandNode<CommandSourceStack> buildWarnCommand() {
        return Commands.literal("warn")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.warn"))
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
                .then(Commands.argument("reason", StringArgumentType.greedyString())
                    .executes(new WarnCommand())))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildMuteCommand() {
        return Commands.literal("mute")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.mute"))
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
                .then(Commands.argument("duration", StringArgumentType.word())
                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                        .executes(new MuteCommand()))))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildUnmuteCommand() {
        return Commands.literal("unmute")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.unmute"))
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
                .executes(new UnmuteCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildBanCommand() {
        return Commands.literal("ban")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.ban"))
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
                .then(Commands.argument("duration", StringArgumentType.word())
                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                        .executes(new BanCommand()))))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildUnbanCommand() {
        return Commands.literal("unban")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.unban"))
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
                .executes(new UnbanCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildHistoryCommand() {
        return Commands.literal("history")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.history"))
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
                .executes(new HistoryCommand()))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTicketCommand() {
        return Commands.literal("ticket")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.ticket"))
            .then(Commands.literal("create")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(new TicketCommand.CreateCommand())))
            .then(Commands.literal("list")
                .executes(new TicketCommand.ListCommand()))
            .then(Commands.literal("close")
                .then(Commands.argument("id", StringArgumentType.word())
                    .executes(new TicketCommand.CloseCommand())))
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> buildTicketsCommand() {
        return Commands.literal("tickets")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.tickets"))
            .then(Commands.literal("list")
                .executes(new TicketsCommand.ListCommand()))
            .then(Commands.literal("view")
                .then(Commands.argument("id", StringArgumentType.word())
                    .executes(new TicketsCommand.ViewCommand())))
            .then(Commands.literal("close")
                .then(Commands.argument("id", StringArgumentType.word())
                    .executes(new TicketsCommand.CloseCommand())))
            .then(Commands.literal("reply")
                .then(Commands.argument("id", StringArgumentType.word())
                    .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(new TicketsCommand.ReplyCommand()))))
            .build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildModerationCommand() {
        return Commands.literal("moderation")
            .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.reload"))
            .then(Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.reload"))
                .then(Commands.literal("config")
                    .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.reload.config"))
                    .executes(new ReloadConfigCommand()))
                .then(Commands.literal("data")
                    .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.reload.data"))
                    .executes(new ReloadDataCommand()))
                .then(Commands.literal("all")
                    .requires(ctx -> ctx.getSender().hasPermission("redmc.moderation.reload.all"))
                    .executes(new ReloadAllCommand())));
    }
}
