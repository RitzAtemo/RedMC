package red.aviora.redmc.teleport.commands.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.api.utils.LocaleManager;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.Warp;

import java.util.Collection;

public class WarpListCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();
        CommandSender sender = context.getSource().getSender();

        Collection<Warp> warps = plugin.getWarpManager().getWarps();
        if (warps.isEmpty()) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "warp.list-empty"),
                "%prefix%", locale.getMessage(sender, "prefix"));
            return Command.SINGLE_SUCCESS;
        }

        ApiUtils.sendCommandSenderMessageArgs(sender,
            locale.getMessage(sender, "warp.list-header"),
            "%prefix%", locale.getMessage(sender, "prefix"));

        for (Warp warp : warps) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                locale.getMessage(sender, "warp.list-entry"),
                "%warp%", warp.getId());
        }

        return Command.SINGLE_SUCCESS;
    }
}
