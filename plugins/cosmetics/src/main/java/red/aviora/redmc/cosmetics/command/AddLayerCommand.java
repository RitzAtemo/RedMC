package red.aviora.redmc.cosmetics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.ParticleLayer;
import red.aviora.redmc.cosmetics.model.ParticleShape;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class AddLayerCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        CosmeticsPlugin plugin = JavaPlugin.getPlugin(CosmeticsPlugin.class);
        String name = getString(ctx, "name");
        String particleStr = getString(ctx, "particle").toUpperCase();
        String shapeStr = getString(ctx, "shape");

        CosmeticTemplate template = plugin.getTemplateManager().get(name);
        if (template == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", name);
            return 0;
        }

        try {
            Particle.valueOf(particleStr);
        } catch (IllegalArgumentException e) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.invalid-particle"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"));
            return 0;
        }

        ParticleShape shape = ParticleShape.fromString(shapeStr).orElse(null);
        if (shape == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.invalid-shape"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%shapes%", ParticleShape.allNames());
            return 0;
        }

        ParticleLayer layer = new ParticleLayer();
        layer.setParticle(particleStr);
        layer.setShape(shape);
        template.addLayer(layer);
        plugin.getTemplateManager().save(template);

        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.addlayer-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%name%", name, "%count%", String.valueOf(template.getLayers().size()));
        return 1;
    }
}
