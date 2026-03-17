package red.aviora.redmc.cosmetics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class RemoveLayerCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        String name = getString(ctx, "name");
        int index = getInteger(ctx, "index");

        CosmeticTemplate template = plugin.getTemplateManager().get(name);
        if (template == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", name);
            return 0;
        }
        if (!template.removeLayer(index)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.layer-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%index%", String.valueOf(index),
                "%count%", String.valueOf(template.getLayers().size()));
            return 0;
        }
        plugin.getTemplateManager().save(template);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.removelayer-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%name%", name, "%index%", String.valueOf(index));
        return 1;
    }
}
