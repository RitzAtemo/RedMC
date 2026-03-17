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
import red.aviora.redmc.cosmetics.model.CosmeticSlot;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class AdminGiveCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        String playerName = getString(ctx, "player");
        String slotStr = getString(ctx, "slot");
        String templateName = getString(ctx, "template");

        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.player-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", playerName);
            return 0;
        }
        CosmeticSlot slot = CosmeticSlot.fromString(slotStr).orElse(null);
        if (slot == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.invalid-slot"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%slots%", CosmeticSlot.allNames());
            return 0;
        }
        if (!plugin.getTemplateManager().exists(target.getUniqueId(), templateName)) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", templateName);
            return 0;
        }
        plugin.getPlayerCosmeticsManager().equip(target, slot, templateName);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.admin-give-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%player%", target.getName(),
            "%template%", templateName,
            "%slot%", slot.name());
        return 1;
    }
}
