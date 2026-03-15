package red.aviora.redmc.chat;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import red.aviora.redmc.chat.commands.ChatReloadCommand;
import red.aviora.redmc.chat.commands.MsgCommand;
import red.aviora.redmc.chat.commands.ReplyCommand;

public class ChatBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> {
				var commands = event.registrar();

				commands.register(
					Commands.literal("chat")
						.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.reload"))
						.then(Commands.literal("reload")
							.executes(new ChatReloadCommand()))
						.build()
				);

				commands.register(buildMsgCommand("msg"));
				commands.register(buildMsgCommand("tell"));

				commands.register(
					Commands.literal("reply")
						.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.msg"))
						.then(Commands.argument("message", StringArgumentType.greedyString())
							.executes(new ReplyCommand()))
						.build()
				);

				commands.register(
					Commands.literal("r")
						.requires(ctx -> ctx.getSender().hasPermission("redmc.chat.msg"))
						.then(Commands.argument("message", StringArgumentType.greedyString())
							.executes(new ReplyCommand()))
						.build()
				);
			}
		);
	}

	private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildMsgCommand(String label) {
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
}
