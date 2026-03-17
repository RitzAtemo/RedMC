package red.aviora.redmc.cosmetics.renderer;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShapeCalculator {

    private static final Random RANDOM = new Random();

    public static List<Vector> point(double yOffset) {
        return List.of(new Vector(0, yOffset, 0));
    }

    public static List<Vector> ring(double radius, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            result.add(new Vector(Math.cos(angle) * radius, yOffset, Math.sin(angle) * radius));
        }
        return result;
    }

    public static List<Vector> sphere(double radius, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points);
        double goldenRatio = (1 + Math.sqrt(5)) / 2;
        for (int i = 0; i < points; i++) {
            double theta = Math.acos(1 - 2.0 * (i + 0.5) / points);
            double phi = 2 * Math.PI * i / goldenRatio;
            double x = radius * Math.sin(theta) * Math.cos(phi);
            double y = radius * Math.cos(theta) + yOffset;
            double z = radius * Math.sin(theta) * Math.sin(phi);
            result.add(new Vector(x, y, z));
        }
        return result;
    }

    public static List<Vector> spiral(double radius, double height, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            double t = (double) i / points;
            double angle = t * 4 * Math.PI;
            double x = Math.cos(angle) * radius;
            double y = t * height + yOffset;
            double z = Math.sin(angle) * radius;
            result.add(new Vector(x, y, z));
        }
        return result;
    }

    public static List<Vector> doubleHelix(double radius, double height, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points * 2);
        for (int i = 0; i < points; i++) {
            double t = (double) i / points;
            double angle = t * 4 * Math.PI;
            double y = t * height + yOffset;
            result.add(new Vector(Math.cos(angle) * radius, y, Math.sin(angle) * radius));
            result.add(new Vector(Math.cos(angle + Math.PI) * radius, y, Math.sin(angle + Math.PI) * radius));
        }
        return result;
    }

    public static List<Vector> star(double outerRadius, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points * 2);
        double innerRadius = outerRadius * 0.45;
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.PI * i / points - Math.PI / 2;
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            result.add(new Vector(Math.cos(angle) * r, yOffset, Math.sin(angle) * r));
        }
        return result;
    }

    public static List<Vector> wingsShape(double span, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points);
        int halfPoints = points / 2;
        for (int i = 0; i < halfPoints; i++) {
            double t = (double) i / halfPoints;
            double x = span * t;
            double y = yOffset + Math.sin(t * Math.PI) * span * 0.4;
            double z = Math.sin(t * Math.PI * 2) * span * 0.15;
            result.add(new Vector(x, y, z));
            result.add(new Vector(-x, y, z));
        }
        return result;
    }

    public static List<Vector> crownShape(double radius, int spikes, double yOffset) {
        List<Vector> result = new ArrayList<>();
        double spikeHeight = radius * 0.7;
        double basePoints = 8;
        for (int i = 0; i < (int) basePoints; i++) {
            double angle = 2 * Math.PI * i / basePoints;
            result.add(new Vector(Math.cos(angle) * radius, yOffset, Math.sin(angle) * radius));
        }
        for (int i = 0; i < spikes; i++) {
            double angle = 2 * Math.PI * i / spikes;
            double bx = Math.cos(angle) * radius;
            double bz = Math.sin(angle) * radius;
            int spikeSteps = 6;
            for (int j = 0; j <= spikeSteps; j++) {
                double t = (double) j / spikeSteps;
                double y = yOffset + Math.sin(t * Math.PI) * spikeHeight;
                result.add(new Vector(bx * (1 - t * 0.3), y, bz * (1 - t * 0.3)));
            }
        }
        return result;
    }

    public static List<Vector> haloShape(double radius, int points, double yOffset) {
        List<Vector> result = ring(radius, points, yOffset);
        double innerR = radius * 0.85;
        result.addAll(ring(innerR, points / 2, yOffset + 0.02));
        return result;
    }

    public static List<Vector> random(double radius, int points, double yOffset) {
        List<Vector> result = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            double x = (RANDOM.nextDouble() * 2 - 1) * radius;
            double y = (RANDOM.nextDouble() * 2 - 1) * radius + yOffset;
            double z = (RANDOM.nextDouble() * 2 - 1) * radius;
            result.add(new Vector(x, y, z));
        }
        return result;
    }

    public static Vector rotateByYaw(Vector v, float yaw) {
        double rad = Math.toRadians(yaw);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = -v.getX() * cos - v.getZ() * sin;
        double z = -v.getX() * sin + v.getZ() * cos;
        return new Vector(x, v.getY(), z);
    }
}
