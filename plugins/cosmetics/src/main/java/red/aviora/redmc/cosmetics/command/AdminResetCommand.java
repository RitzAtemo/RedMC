package red.aviora.redmc.cosmetics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class AdminResetCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        String playerName = getString(ctx, "player");

        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.player-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", playerName);
            return 0;
        }
        plugin.getPlayerCosmeticsManager().reset(target);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.admin-reset-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%player%", target.getName());
        return 1;
    }
}
