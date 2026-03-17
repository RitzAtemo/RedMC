package red.aviora.redmc.cosmetics.renderer;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.manager.PlayerCosmeticsManager;
import red.aviora.redmc.cosmetics.manager.TemplateManager;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.ParticleLayer;
import red.aviora.redmc.cosmetics.model.ParticleShape;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CosmeticRenderer {

    private final TrailTracker trailTracker;
    private final AtomicLong globalTick = new AtomicLong(0);
    private Object renderTask;

    public CosmeticRenderer(TrailTracker trailTracker) {
        this.trailTracker = trailTracker;
    }

    public void start() {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        long intervalMs = plugin.getConfigManager().getInt("config.yml", "renderer.tick-interval-ms", 50);

        renderTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(
            plugin,
            task -> tick(),
            0L,
            intervalMs,
            TimeUnit.MILLISECONDS
        );
    }

    public void stop() {
        if (renderTask != null) {
            try {
                renderTask.getClass().getMethod("cancel").invoke(renderTask);
            } catch (Exception ignored) {}
            renderTask = null;
        }
    }

    private void tick() {
        long tick = globalTick.incrementAndGet();
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        PlayerCosmeticsManager playerManager = plugin.getPlayerCosmeticsManager();
        TemplateManager templateManager = plugin.getTemplateManager();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerCosmetics cosmetics = playerManager.get(player);
            if (cosmetics == null || !cosmetics.isVisible()) continue;
            if (cosmetics.getEquippedMap().isEmpty()) continue;

            player.getScheduler().run(plugin, scheduledTask -> {
                for (var entry : cosmetics.getEquippedMap().entrySet()) {
                    CosmeticSlot slot = entry.getKey();
                    CosmeticTemplate template = templateManager.get(entry.getValue());
                    if (template == null) continue;
                    renderSlot(player, slot, template, tick);
                }
            }, null);
        }
    }

    private void renderSlot(Player player, CosmeticSlot slot, CosmeticTemplate template, long tick) {
        for (ParticleLayer layer : template.getLayers()) {
            if (tick % layer.getTickRate() != 0) continue;
            renderLayer(player, slot, layer);
        }
    }

    private void renderLayer(Player player, CosmeticSlot slot, ParticleLayer layer) {
        Particle particle = resolveParticle(layer.getParticle());
        if (particle == null) return;

        List<Vector> points = computeShapePoints(layer);
        boolean needsRotation = slotNeedsRotation(slot);
        float yaw = player.getLocation().getYaw();

        if (slot == CosmeticSlot.TRAIL) {
            renderTrail(player, layer, particle, points);
            return;
        }

        Location anchor = computeAnchor(player, slot, layer.getYOffset());

        for (Vector point : points) {
            Vector rotated = needsRotation ? ShapeCalculator.rotateByYaw(point, yaw) : point;
            Location loc = anchor.clone().add(rotated);
            spawnParticle(player.getWorld(), loc, particle, layer);
        }
    }

    private void renderTrail(Player player, ParticleLayer layer, Particle particle, List<Vector> points) {
        List<Location> trail = trailTracker.getTrail(player);
        for (Location trailLoc : trail) {
            for (Vector point : points) {
                Location loc = trailLoc.clone().add(point);
                spawnParticle(player.getWorld(), loc, particle, layer);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void spawnParticle(org.bukkit.World world, Location loc, Particle particle, ParticleLayer layer) {
        try {
            if (particle == Particle.DUST) {
                Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(layer.getDustColorR(), layer.getDustColorG(), layer.getDustColorB()),
                    layer.getDustSize()
                );
                world.spawnParticle(particle, loc, layer.getCount(),
                    layer.getOffsetX(), layer.getOffsetY(), layer.getOffsetZ(),
                    layer.getSpeed(), dust, true);
            } else if (particle == Particle.DUST_COLOR_TRANSITION) {
                Particle.DustTransition dust = new Particle.DustTransition(
                    Color.fromRGB(layer.getDustColorR(), layer.getDustColorG(), layer.getDustColorB()),
                    Color.fromRGB(layer.getDustColorToR(), layer.getDustColorToG(), layer.getDustColorToB()),
                    layer.getDustSize()
                );
                world.spawnParticle(particle, loc, layer.getCount(),
                    layer.getOffsetX(), layer.getOffsetY(), layer.getOffsetZ(),
                    layer.getSpeed(), dust, true);
            } else {
                world.spawnParticle(particle, loc, layer.getCount(),
                    layer.getOffsetX(), layer.getOffsetY(), layer.getOffsetZ(),
                    layer.getSpeed(), null, true);
            }
        } catch (Exception ignored) {}
    }

    private List<Vector> computeShapePoints(ParticleLayer layer) {
        double r = layer.getShapeRadius();
        int pts = layer.getShapePoints();
        double y = layer.getYOffset();
        return switch (layer.getShape()) {
            case POINT -> ShapeCalculator.point(y);
            case RING -> ShapeCalculator.ring(r, pts, y);
            case SPHERE -> ShapeCalculator.sphere(r, pts, y);
            case SPIRAL -> ShapeCalculator.spiral(r, r * 1.5, pts, y);
            case DOUBLE_HELIX -> ShapeCalculator.doubleHelix(r, r * 2.0, pts / 2, y);
            case STAR -> ShapeCalculator.star(r, 5, y);
            case WINGS_SHAPE -> ShapeCalculator.wingsShape(r, pts, y);
            case CROWN_SHAPE -> ShapeCalculator.crownShape(r, 5, y);
            case HALO_SHAPE -> ShapeCalculator.haloShape(r, pts, y);
            case RANDOM -> ShapeCalculator.random(r, pts, y);
        };
    }

    private Location computeAnchor(Player player, CosmeticSlot slot, double layerYOffset) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        Location eye = player.getEyeLocation();
        Location feet = player.getLocation();
        float yaw = feet.getYaw();
        double rad = Math.toRadians(yaw);
        double fwdX = -Math.sin(rad);
        double fwdZ = Math.cos(rad);
        double rightX = -Math.cos(rad);
        double rightZ = -Math.sin(rad);

        return switch (slot) {
            case HEAD -> eye.clone().add(0, plugin.getConfigManager().getDouble("config.yml", "slots.head.y-offset", 0.4), 0);
            case BACK -> {
                double dist = plugin.getConfigManager().getDouble("config.yml", "slots.back.distance", 0.5);
                yield feet.clone().add(-fwdX * dist, layerYOffset, -fwdZ * dist);
            }
            case FEET -> feet.clone();
            case ORBIT, AURA -> feet.clone().add(0, plugin.getConfigManager().getDouble("config.yml", "slots.orbit.y-offset", 1.0), 0);
            case WINGS -> feet.clone().add(-fwdX * 0.3, plugin.getConfigManager().getDouble("config.yml", "slots.wings.y-offset", 0.8), -fwdZ * 0.3);
            case CROWN -> eye.clone().add(0, plugin.getConfigManager().getDouble("config.yml", "slots.crown.y-offset", 0.3), 0);
            case HALO -> eye.clone().add(0, plugin.getConfigManager().getDouble("config.yml", "slots.halo.y-offset", 0.7), 0);
            case SHOULDER_LEFT -> {
                double side = plugin.getConfigManager().getDouble("config.yml", "slots.shoulder-left.side-offset", 0.45);
                double yOff = plugin.getConfigManager().getDouble("config.yml", "slots.shoulder-left.y-offset", 0.3);
                yield eye.clone().add(-rightX * side, -yOff, -rightZ * side);
            }
            case SHOULDER_RIGHT -> {
                double side = plugin.getConfigManager().getDouble("config.yml", "slots.shoulder-right.side-offset", 0.45);
                double yOff = plugin.getConfigManager().getDouble("config.yml", "slots.shoulder-right.y-offset", 0.3);
                yield eye.clone().add(rightX * side, -yOff, rightZ * side);
            }
            default -> feet.clone();
        };
    }

    private boolean slotNeedsRotation(CosmeticSlot slot) {
        return slot == CosmeticSlot.BACK || slot == CosmeticSlot.WINGS
            || slot == CosmeticSlot.SHOULDER_LEFT || slot == CosmeticSlot.SHOULDER_RIGHT;
    }

    private Particle resolveParticle(String name) {
        try {
            return Particle.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public TrailTracker getTrailTracker() {
        return trailTracker;
    }
}
