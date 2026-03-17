package red.aviora.redmc.teleport.commands.rtp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.utils.LimitUtils;

public class RtpCommand implements Command<CommandSourceStack> {

    private final boolean hasWorldArg;

    public RtpCommand(boolean hasWorldArg) {
        this.hasWorldArg = hasWorldArg;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();

        if (!(context.getSource().getSender() instanceof Player player)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(context.getSource().getSender(), "error.only-players"),
                        "%prefix%", locale.getMessage(context.getSource().getSender(), "prefix"))
                )
            ).create();
        }

        int limit = LimitUtils.getRtpLimit(player);
        boolean bypassCooldown = LimitUtils.hasRtpCooldownBypass(player);
        if (!plugin.getRtpManager().canUse(player, limit, bypassCooldown)) {
            throw new SimpleCommandExceptionType(
                MessageComponentSerializer.message().serialize(
                    ApiUtils.formatText(locale.getMessage(player, "rtp.limit-reached"),
                        "%prefix%", locale.getMessage(player, "prefix"))
                )
            ).create();
        }

        World world;
        if (hasWorldArg) {
            String worldName = StringArgumentType.getString(context, "world");
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                world = player.getWorld();
            }
        } else {
            world = player.getWorld();
        }

        ApiUtils.sendPlayerMessageArgs(player,
            locale.getMessage(player, "rtp.searching"),
            "%prefix%", locale.getMessage(player, "prefix"));

        plugin.getBackManager().push(player, player.getLocation());
        World finalWorld = world;
        plugin.getRtpManager().findLocation(finalWorld).thenAccept(loc -> {
            if (loc == null) {
                ApiUtils.sendPlayerMessageArgs(player,
                    locale.getMessage(player, "rtp.failed"),
                    "%prefix%", locale.getMessage(player, "prefix"));
                return;
            }
            player.teleportAsync(loc).thenAccept(success -> {
                if (success) {
                    plugin.getRtpManager().recordUse(player, bypassCooldown);
                    ApiUtils.sendPlayerMessageArgs(player,
                        locale.getMessage(player, "rtp.success"),
                        "%prefix%", locale.getMessage(player, "prefix"));
                }
            });
        });

        return Command.SINGLE_SUCCESS;
    }
}
