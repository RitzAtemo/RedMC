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

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class UnequipCommand implements Command<CommandSourceStack> {

    private final boolean all;

    public UnequipCommand(boolean all) {
        this.all = all;
    }

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

        if (all) {
            plugin.getPlayerCosmeticsManager().unequipAll(player);
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.unequip-all-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
            return 1;
        }

        String slotStr = getString(ctx, "slot");
        CosmeticSlot slot = CosmeticSlot.fromString(slotStr).orElse(null);
        if (slot == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.invalid-slot"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%slots%", CosmeticSlot.allNames());
            return 0;
        }
        boolean removed = plugin.getPlayerCosmeticsManager().unequip(player, slot);
        if (!removed) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.not-equipped"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%slot%", slot.name());
            return 0;
        }
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.unequip-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%slot%", slot.name());
        return 1;
    }
}
