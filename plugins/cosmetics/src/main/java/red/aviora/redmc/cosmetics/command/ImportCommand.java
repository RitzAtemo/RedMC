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

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class ImportCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        String signature = getString(ctx, "signature");

        try {
            CosmeticTemplate template = plugin.getTemplateManager().importFromSignature(signature);
            if (template == null) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "error.import-failed"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                    "%reason%", "Invalid signature");
                return 0;
            }
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.import-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", template.getName());
        } catch (Exception e) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.import-failed"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%reason%", e.getMessage());
        }
        return 1;
    }
}
