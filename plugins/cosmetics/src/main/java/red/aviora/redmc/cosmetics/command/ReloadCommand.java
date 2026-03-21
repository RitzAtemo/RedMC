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

public class ReloadCommand implements Command<CommandSourceStack> {

    public enum Mode { CONFIG, DATA, ALL }

    private final Mode mode;

    public ReloadCommand(Mode mode) {
        this.mode = mode;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);

        switch (mode) {
            case CONFIG -> {
                plugin.getConfigManager().reload();
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "reload.config-success"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
            }
            case DATA -> {
                plugin.getTemplateManager().reloadAllOnline(plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).toList());
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "reload.data-success"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
            }
            case ALL -> {
                plugin.getConfigManager().reload();
                plugin.getTemplateManager().reloadAllOnline(plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).toList());
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "reload.all-success"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
            }
        }
        return 1;
    }
}
