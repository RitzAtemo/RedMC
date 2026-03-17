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
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

public class EquippedCommand implements Command<CommandSourceStack> {

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
        PlayerCosmetics cosmetics = plugin.getPlayerCosmeticsManager().get(player);

        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.equipped-header"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));

        if (cosmetics == null || cosmetics.getEquippedMap().isEmpty()) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.equipped-none"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
        } else {
            for (var entry : cosmetics.getEquippedMap().entrySet()) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "cosmetics.equipped-entry"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                    "%slot%", entry.getKey().name(),
                    "%template%", entry.getValue());
            }
        }
        return 1;
    }
}
