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
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class CreateCommand implements Command<CommandSourceStack> {

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

        String name = getString(ctx, "name").toLowerCase();
        String slotStr = getString(ctx, "slot");

        CosmeticSlot slot = CosmeticSlot.fromString(slotStr).orElse(null);
        if (slot == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.invalid-slot"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%slots%", CosmeticSlot.allNames());
            return 0;
        }
        if (plugin.getTemplateManager().exists(player.getUniqueId(), name)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-exists"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", name);
            return 0;
        }
        CosmeticTemplate template = new CosmeticTemplate(name, slot);
        template.setAuthor(player.getName());
        plugin.getTemplateManager().save(player.getUniqueId(), template);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.create-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%name%", name, "%slot%", slot.name());
        return 1;
    }
}
