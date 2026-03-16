package red.aviora.redmc.chat;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import red.aviora.redmc.chat.commands.MsgCommand;
import red.aviora.redmc.chat.commands.ReloadAllCommand;
import red.aviora.redmc.chat.commands.ReloadAlertsCommand;
import red.aviora.redmc.chat.commands.ReloadConfigCommand;
import red.aviora.redmc.chat.commands.ReplyCommand;
import red.aviora.redmc.chat.commands.SayCommand;

public class ChatBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> {
				var commands = event.registrar();

				commands.register(makeChatCommand().build());
				commands.register(buildSayCommand("say"));
				commands.register(buildMsgCommand("msg"));
				commands.register(buildMsgCommand("tell"));
				commands.register(buildMsgCommand("w"));
				commands.register(buildMsgCommand("whisper"));
				commands.register(buildMsgCommand("pm"));
				commands.register(buildReplyCommand("reply"));
				commands.register(buildReplyCommand("r"));
				commands.register(buildReplyCommand("re"));
			}
		);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeChatCommand() {
		return Commands.literal("chat")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.chat"))
			.then(makeReloadNode());
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.reload"))
			.then(Commands.literal("config")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.reload.config"))
				.executes(new ReloadConfigCommand()))
			.then(Commands.literal("alerts")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.reload.alerts"))
				.executes(new ReloadAlertsCommand()))
			.then(Commands.literal("all")
				.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.reload.all"))
				.executes(new ReloadAllCommand()));
	}

	private LiteralCommandNode<CommandSourceStack> buildSayCommand(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.global"))
			.then(Commands.argument("message", StringArgumentType.greedyString())
				.executes(new SayCommand()))
			.build();
	}

	private LiteralCommandNode<CommandSourceStack> buildMsgCommand(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.msg"))
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
				.then(Commands.argument("message", StringArgumentType.greedyString())
					.executes(new MsgCommand())))
			.build();
	}

	private LiteralCommandNode<CommandSourceStack> buildReplyCommand(String label) {
		return Commands.literal(label)
			.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.msg"))
			.then(Commands.argument("message", StringArgumentType.greedyString())
				.executes(new ReplyCommand()))
			.build();
	}
}
