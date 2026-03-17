package red.aviora.redmc.cosmetics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;

public class ToggleCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                CosmeticsPlugin.getInstance().getLocaleManager().getMessage(sender, "error.only-players"),
                "%prefix%", CosmeticsPlugin.getInstance().getLocaleManager().getMessage(sender, "prefix"));
            return 0;
        }
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        boolean nowVisible = plugin.getPlayerCosmeticsManager().toggleVisibility(player);
        String key = nowVisible ? "cosmetics.toggle-on" : "cosmetics.toggle-off";
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, key),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
        return 1;
    }
}
