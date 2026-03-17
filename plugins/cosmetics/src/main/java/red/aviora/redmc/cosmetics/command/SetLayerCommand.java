package red.aviora.redmc.cosmetics.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.ParticleLayer;
import red.aviora.redmc.cosmetics.model.ParticleShape;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

public class SetLayerCommand implements Command<CommandSourceStack> {

    public enum Property {
        PARTICLE, SHAPE, COUNT, SPEED, YOFFSET, TICKRATE, RADIUS, POINTS,
        COLOR, COLORTO, DUSTSIZE, OFFSETX, OFFSETY, OFFSETZ
    }

    private final Property property;

    public SetLayerCommand(Property property) {
        this.property = property;
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
        String name = getString(ctx, "name");
        int index = getInteger(ctx, "index");

        CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), name);
        if (template == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.template-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%name%", name);
            return 0;
        }
        ParticleLayer layer = template.getLayer(index);
        if (layer == null) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.layer-not-found"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%index%", String.valueOf(index),
                "%count%", String.valueOf(template.getLayers().size()));
            return 0;
        }

        String valueStr;
        try {
            valueStr = applyProperty(ctx, layer);
        } catch (IllegalArgumentException e) {
            ApiUtils.sendCommandSenderMessageArgs(sender,
                plugin.getLocaleManager().getMessage(sender, "error.invalid-number"),
                "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
                "%value%", e.getMessage());
            return 0;
        }

        plugin.getTemplateManager().save(player.getUniqueId(), template);
        ApiUtils.sendCommandSenderMessageArgs(sender,
            plugin.getLocaleManager().getMessage(sender, "cosmetics.setlayer-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(sender, "prefix"),
            "%name%", name,
            "%index%", String.valueOf(index),
            "%property%", property.name().toLowerCase(),
            "%value%", valueStr);
        return 1;
    }

    private String applyProperty(CommandContext<CommandSourceStack> ctx, ParticleLayer layer) {
        return switch (property) {
            case PARTICLE -> {
                String val = getString(ctx, "value").toUpperCase();
                try { Particle.valueOf(val); } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid particle: " + val);
                }
                layer.setParticle(val);
                yield val;
            }
            case SHAPE -> {
                String val = getString(ctx, "value");
                ParticleShape shape = ParticleShape.fromString(val).orElseThrow(
                    () -> new IllegalArgumentException("Invalid shape: " + val));
                layer.setShape(shape);
                yield shape.name();
            }
            case COUNT -> {
                int val = getInteger(ctx, "value");
                layer.setCount(val);
                yield String.valueOf(val);
            }
            case SPEED -> {
                double val = parseDouble(getString(ctx, "value"));
                layer.setSpeed(val);
                yield String.valueOf(val);
            }
            case YOFFSET -> {
                double val = parseDouble(getString(ctx, "value"));
                layer.setYOffset(val);
                yield String.valueOf(val);
            }
            case TICKRATE -> {
                int val = getInteger(ctx, "value");
                layer.setTickRate(Math.max(1, val));
                yield String.valueOf(val);
            }
            case RADIUS -> {
                double val = parseDouble(getString(ctx, "value"));
                layer.setShapeRadius(val);
                yield String.valueOf(val);
            }
            case POINTS -> {
                int val = getInteger(ctx, "value");
                layer.setShapePoints(val);
                yield String.valueOf(val);
            }
            case OFFSETX -> {
                double val = parseDouble(getString(ctx, "value"));
                layer.setOffsetX(val);
                yield String.valueOf(val);
            }
            case OFFSETY -> {
                double val = parseDouble(getString(ctx, "value"));
                layer.setOffsetY(val);
                yield String.valueOf(val);
            }
            case OFFSETZ -> {
                double val = parseDouble(getString(ctx, "value"));
                layer.setOffsetZ(val);
                yield String.valueOf(val);
            }
            case COLOR -> {
                int r = getInteger(ctx, "r"); int g = getInteger(ctx, "g"); int b = getInteger(ctx, "b");
                validateColor(r, g, b);
                layer.setDustColorR(r); layer.setDustColorG(g); layer.setDustColorB(b);
                yield r + " " + g + " " + b;
            }
            case COLORTO -> {
                int r = getInteger(ctx, "r"); int g = getInteger(ctx, "g"); int b = getInteger(ctx, "b");
                validateColor(r, g, b);
                layer.setDustColorToR(r); layer.setDustColorToG(g); layer.setDustColorToB(b);
                yield r + " " + g + " " + b;
            }
            case DUSTSIZE -> {
                float val = (float) parseDouble(getString(ctx, "value"));
                layer.setDustSize(val);
                yield String.valueOf(val);
            }
        };
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) {
            throw new IllegalArgumentException(s);
        }
    }

    private void validateColor(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
            throw new IllegalArgumentException("Color out of range");
    }
}
