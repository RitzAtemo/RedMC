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

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class ListCommand implements Command<CommandSourceStack> {

    private final boolean filtered;

    public ListCommand(boolean filtered) {
        this.filtered = filtered;
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

        List<CosmeticTemplate> list;
        String filterMsg;

        if (filtered) {
            String slotStr = getString(ctx, "slot");
            CosmeticSlot slot = CosmeticSlot.fromString(slotStr).orElse(null);
            if (slot == null) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "error.invalid-slot"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                    "%slots%", CosmeticSlot.allNames());
                return 0;
            }
            list = plugin.getTemplateManager().getForSlot(player.getUniqueId(), slot);
            filterMsg = plugin.getLocaleManager().getMessage(sender, "cosmetics.list-filter-slot");
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.list-header"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%filter%", filterMsg,
                "%slot%", slot.name());
        } else {
            list = plugin.getTemplateManager().getAll(player.getUniqueId());
            filterMsg = plugin.getLocaleManager().getMessage(sender, "cosmetics.list-filter-none");
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.list-header"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%filter%", filterMsg);
        }

        if (list.isEmpty()) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.list-empty"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
        } else {
            for (CosmeticTemplate t : list) {
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "cosmetics.list-entry"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                    "%name%", t.getName(),
                    "%slot%", t.getSlot().name(),
                    "%description%", t.getDescription());
            }
        }
        return 1;
    }
}
