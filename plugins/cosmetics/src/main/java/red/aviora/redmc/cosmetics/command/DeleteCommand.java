package red.aviora.redmc.cosmetics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class DeleteCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        String name = getString(ctx, "name");

        if (!plugin.getTemplateManager().exists(name)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", name);
            return 0;
        }
        plugin.getTemplateManager().delete(name);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.delete-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%name%", name);
        return 1;
    }
}
