package red.aviora.redmc.permissions;

import red.aviora.redmc.permissions.commands.group.*;
import red.aviora.redmc.permissions.commands.player.*;
import red.aviora.redmc.permissions.commands.reload.*;
import red.aviora.redmc.permissions.commands.group.*;
import red.aviora.redmc.permissions.commands.player.*;
import red.aviora.redmc.permissions.commands.reload.ReloadAllCommand;
import red.aviora.redmc.permissions.commands.reload.ReloadConfigCommand;
import red.aviora.redmc.permissions.commands.reload.ReloadDataSourceCommand;
import red.aviora.redmc.permissions.models.Group;
import red.aviora.redmc.permissions.models.PermissionEntry;
import red.aviora.redmc.permissions.models.PlayerData;
import red.aviora.redmc.permissions.utils.PermissionManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class PermissionsBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext context) {
		context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			commands.registrar().register(makePermissionCommands("permissions"));
		});
	}

	private LiteralCommandNode<CommandSourceStack> makePermissionCommands(String label) {
		return
			Commands.literal(label)
				.requires(ctx -> ctx.getSender().hasPermission("redmc.permissions"))
				.then(makeReloadNode())
				.then(makeGroupNode())
				.then(makePlayerNode())
				.build();
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeReloadNode() {
		return Commands.literal("reload")
			.requires(context -> context.getSender().hasPermission("redmc.permissions.reload"))
			.then(Commands.literal("config")
				.executes(new ReloadConfigCommand()))
			.then(Commands.literal("data")
				.executes(new ReloadDataSourceCommand()))
			.then(Commands.literal("all")
				.executes(new ReloadAllCommand()));
	}

	private LiteralArgumentBuilder<CommandSourceStack> makeGroupNode() {
		return Commands.literal("group")
			.then(Commands.literal("create")
				.requires(context -> context.getSender().hasPermission("redmc.permissions.group.create"))
				.then(Commands.argument("<group>", StringArgumentType.word())
					.executes(new GroupCreateCommand())))
			.then(Commands.literal("read")
				.requires(context -> context.getSender().hasPermission("redmc.permissions.group.read"))
				.executes(new GroupReadCommand()))
			.then(Commands.literal("update")
				.then(Commands.argument("<group>", StringArgumentType.word())
					.suggests((context, builder) -> {
						PermissionsPlugin.getInstance().getPermissionManager().getGroups().keySet().stream()
							.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
							.forEach(builder::suggest);
						return builder.buildFuture();
					})
					.then(Commands.literal("permissions")
						.then(Commands.literal("create")
							.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.permissions.create"))
							.then(Commands.argument("<permission>", StringArgumentType.word())
								.suggests((context, builder) -> {
									String group = StringArgumentType.getString(context, "<group>");
									getRegisteredPermissions().stream()
										.filter(permission -> !getGroupPermissions(PermissionsPlugin.getInstance().getPermissionManager(), group).contains(permission))
										.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
										.forEach(builder::suggest);
									return builder.buildFuture();
								})
								.then(Commands.argument("<weight>", IntegerArgumentType.integer())
									.then(Commands.argument("<allowed>", BoolArgumentType.bool())
										.executes(new GroupPermissionCreateCommand())))
							)
						)
						.then(Commands.literal("read")
							.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.permissions.read"))
							.executes(new GroupPermissionsReadCommand()))
						.then(Commands.literal("delete")
							.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.permissions.delete"))
							.then(Commands.argument("<permission>", StringArgumentType.word())
								.suggests((context, builder) -> {
									getGroupPermissions(
										PermissionsPlugin.getInstance().getPermissionManager(),
										StringArgumentType.getString(context, "<group>")
									)
										.stream()
										.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
										.forEach(builder::suggest);
									return builder.buildFuture();
								})
								.executes(new GroupPermissionDeleteCommand())
							)
						)
					)
					.then(Commands.literal("inherits")
						.then(Commands.literal("create")
							.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.inherits.create"))
							.then(Commands.argument("<parent>", StringArgumentType.word())
								.suggests((context, builder) -> {
									String groupId = StringArgumentType.getString(context, "<group>").toLowerCase();
									Group current = PermissionsPlugin.getInstance().getPermissionManager().getGroups().get(groupId);
									PermissionsPlugin.getInstance().getPermissionManager().getGroups().keySet().stream()
										.filter(id -> !id.equals(groupId))
										.filter(id -> current == null || !current.getInherits().contains(id))
										.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
										.forEach(builder::suggest);
									return builder.buildFuture();
								})
								.executes(new GroupInheritCreateCommand())
							)
						)
						.then(Commands.literal("read")
							.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.inherits.read"))
							.executes(new GroupInheritsReadCommand()))
						.then(Commands.literal("delete")
							.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.inherits.delete"))
							.then(Commands.argument("<parent>", StringArgumentType.word())
								.suggests((context, builder) -> {
									Group group = PermissionsPlugin.getInstance().getPermissionManager()
										.getGroups().get(StringArgumentType.getString(context, "<group>").toLowerCase());
									if (group != null) {
										group.getInherits().stream()
											.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
											.forEach(builder::suggest);
									}
									return builder.buildFuture();
								})
								.executes(new GroupInheritDeleteCommand())
							)
						)
					)
					.then(Commands.literal("name")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.group.update.name"))
						.then(Commands.argument("<name>", StringArgumentType.word())
							.executes(new GroupUpdateNameCommand())))
				)
			)
			.then(Commands.literal("delete")
				.requires(context -> context.getSender().hasPermission("redmc.permissions.group.delete"))
				.then(Commands.argument("<group>", StringArgumentType.word())
					.suggests((context, builder) -> {
						PermissionsPlugin.getInstance().getPermissionManager().getGroups().keySet().stream()
							.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
							.forEach(builder::suggest);
						return builder.buildFuture();
					})
					.executes(new GroupDeleteCommand())
				)
			);
	}

	private LiteralArgumentBuilder<CommandSourceStack> makePlayerNode() {
		return Commands.literal("player")
			.then(Commands.argument("<player>", StringArgumentType.word())
				.suggests((context, builder) -> {
					Bukkit.getOnlinePlayers().stream()
						.map(Player::getName)
						.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
						.forEach(builder::suggest);
					return builder.buildFuture();
				})
				.then(Commands.literal("permissions")
					.then(Commands.literal("create")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.player.permissions.create"))
						.then(Commands.argument("<permission>", StringArgumentType.word())
							.suggests((context, builder) -> {
								getRegisteredPermissions().stream()
									.filter(permission -> !getPlayerPermissions(PermissionsPlugin.getInstance().getPermissionManager(), StringArgumentType.getString(context, "<player>")).contains(permission))
									.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
									.forEach(builder::suggest);
								return builder.buildFuture();
							})
							.then(Commands.argument("<weight>", IntegerArgumentType.integer())
								.then(Commands.argument("<allowed>", BoolArgumentType.bool())
									.executes(new PlayerPermissionCreateCommand())))
						)
					)
					.then(Commands.literal("read")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.player.permissions.read"))
						.executes(new PlayerPermissionsReadCommand()))
					.then(Commands.literal("delete")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.player.permissions.delete"))
						.then(Commands.argument("<permission>", StringArgumentType.word())
							.suggests((context, builder) -> {
								getPlayerPermissions(PermissionsPlugin.getInstance().getPermissionManager(), StringArgumentType.getString(context, "<player>")).stream()
									.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
									.forEach(builder::suggest);
								return builder.buildFuture();
							})
							.executes(new PlayerPermissionDeleteCommand())
						)
					)
				)
				.then(Commands.literal("groups")
					.then(Commands.literal("create")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.player.groups.create"))
						.then(Commands.argument("<group>", StringArgumentType.word())
							.suggests((context, builder) -> {
								PermissionsPlugin.getInstance().getPermissionManager().getGroups().keySet().stream()
									.filter(group -> !getPlayerGroups(PermissionsPlugin.getInstance().getPermissionManager(), StringArgumentType.getString(context, "<player>")).contains(group))
									.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
									.forEach(builder::suggest);
								return builder.buildFuture();
							})
							.executes(new PlayerGroupCreateCommand())
						)
					)
					.then(Commands.literal("read")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.player.groups.read"))
						.executes(new PlayerGroupsReadCommand()))
					.then(Commands.literal("delete")
						.requires(context -> context.getSender().hasPermission("redmc.permissions.player.groups.delete"))
						.then(Commands.argument("<group>", StringArgumentType.word())
							.suggests((context, builder) -> {
								getPlayerGroups(PermissionsPlugin.getInstance().getPermissionManager(), StringArgumentType.getString(context, "<player>")).stream()
									.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
									.forEach(builder::suggest);
								return builder.buildFuture();
							})
							.executes(new PlayerGroupDeleteCommand())
						)
					)
				)
			);
	}

	private List<String> getGroupPermissions(PermissionManager permissionManager, String groupId) {
		Group group = permissionManager.getGroups().get(groupId);
		if (group == null) return List.of();
		return group.getPermissions().stream().map(PermissionEntry::getName).toList();
	}

	private List<String> getPlayerPermissions(PermissionManager permissionManager, String playerName) {
		Player player = Bukkit.getPlayerExact(playerName);
		if (player == null) return List.of();
		PlayerData data = permissionManager.getPlayers().get(player.getUniqueId());
		if (data == null) return List.of();
		return data.getPermissions().stream().map(PermissionEntry::getName).toList();
	}

	private List<String> getPlayerGroups(PermissionManager permissionManager, String playerName) {
		Player player = Bukkit.getPlayerExact(playerName);
		if (player == null) return List.of();
		PlayerData data = permissionManager.getPlayers().get(player.getUniqueId());
		if (data == null) return List.of();
		return data.getGroupIds();
	}

	private List<String> getRegisteredPermissions() {
		return Bukkit.getPluginManager()
			.getPermissions()
			.stream()
			.map(Permission::getName)
			.sorted()
			.toList();
	}
}
