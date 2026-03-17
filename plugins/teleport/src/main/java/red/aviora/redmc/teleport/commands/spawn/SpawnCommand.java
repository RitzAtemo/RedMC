package red.aviora.redmc.teleport.commands.spawn;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.SerializableLocation;

public class SpawnCommand implements Command<CommandSourceStack> {

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

        Location spawnLoc = null;
        SerializableLocation serializableSpawn = plugin.getSpawnManager().getSpawn();
        if (serializableSpawn != null) {
            spawnLoc = serializableSpawn.toBukkitLocation();
        }
        if (spawnLoc == null) {
            spawnLoc = player.getWorld().getSpawnLocation();
        }

        plugin.getBackManager().push(player, player.getLocation());
        player.teleportAsync(spawnLoc).thenAccept(success -> {
            if (success) {
                ApiUtils.sendPlayerMessageArgs(player,
                    locale.getMessage(player, "spawn.teleport-success"),
                    "%prefix%", locale.getMessage(player, "prefix"));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
