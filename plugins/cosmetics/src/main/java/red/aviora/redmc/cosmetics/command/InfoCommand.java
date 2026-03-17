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
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.ParticleLayer;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class InfoCommand implements Command<CommandSourceStack> {

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
        String name = getString(ctx, "name");

        CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), name);
        if (template == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", name);
            return 0;
        }

        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.template-info-header"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%name%", template.getName(),
            "%slot%", template.getSlot().name());

        if (template.getLayers().isEmpty()) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "cosmetics.template-info-no-layers"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
        } else {
            for (int i = 0; i < template.getLayers().size(); i++) {
                ParticleLayer layer = template.getLayers().get(i);
                ApiUtils.sendCommandSenderMessageArgs(sender,
                    plugin.getLocaleManager().getMessage(sender, "cosmetics.template-info-layer"),
                    "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                    "%index%", String.valueOf(i),
                    "%particle%", layer.getParticle(),
                    "%shape%", layer.getShape().name(),
                    "%count%", String.valueOf(layer.getCount()),
                    "%speed%", String.valueOf(layer.getSpeed()),
                    "%tickrate%", String.valueOf(layer.getTickRate()));
            }
        }
        return 1;
    }
}
