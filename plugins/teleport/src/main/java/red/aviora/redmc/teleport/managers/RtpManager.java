package red.aviora.redmc.teleport.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RtpManager {

    private final Map<UUID, Integer> intervalUses = new ConcurrentHashMap<>();
    private final Map<UUID, Long> intervalStart = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private long getIntervalMs() {
        return JavaPlugin.getPlugin(TeleportPlugin.class).getConfigManager()
            .getInt("config.yml", "rtp.cooldown-interval", 3600) * 1000L;
    }

    public boolean canUse(Player player, int limit, boolean bypassCooldown) {
        if (limit < 0) return true;
        if (limit == 0) return false;
        UUID uuid = player.getUniqueId();
        if (!bypassCooldown) {
            long now = System.currentTimeMillis();
            Long start = intervalStart.get(uuid);
            if (start == null || now - start > getIntervalMs()) {
                return true;
            }
        }
        return intervalUses.getOrDefault(uuid, 0) < limit;
    }

    public void recordUse(Player player, boolean bypassCooldown) {
        UUID uuid = player.getUniqueId();
        if (!bypassCooldown) {
            long now = System.currentTimeMillis();
            Long start = intervalStart.get(uuid);
            if (start == null || now - start > getIntervalMs()) {
                intervalStart.put(uuid, now);
                intervalUses.put(uuid, 1);
                return;
            }
        }
        intervalUses.merge(uuid, 1, Integer::sum);
    }

    public void clearPlayer(UUID uuid) {
        intervalUses.remove(uuid);
        intervalStart.remove(uuid);
    }

    public CompletableFuture<Location> findLocation(World world) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        int minDistance = plugin.getConfigManager().getInt("config.yml", "rtp.min-distance", 500);
        int maxDistance = plugin.getConfigManager().getInt("config.yml", "rtp.max-distance", 5000);
        int maxAttempts = plugin.getConfigManager().getInt("config.yml", "rtp.max-attempts", 30);

        CompletableFuture<Location> result = new CompletableFuture<>();
        tryAttempt(world, 0, maxAttempts, minDistance, maxDistance, result);
        return result;
    }

    private void tryAttempt(World world, int attempt, int maxAttempts, int minDist, int maxDist, CompletableFuture<Location> result) {
        if (attempt >= maxAttempts) {
            result.complete(null);
            return;
        }

        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = minDist + random.nextDouble() * (double) (maxDist - minDist);
        int x = (int) (Math.cos(angle) * distance);
        int z = (int) (Math.sin(angle) * distance);

        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            if (isSafe(loc)) {
                result.complete(loc);
            } else {
                tryAttempt(world, attempt + 1, maxAttempts, minDist, maxDist, result);
            }
        });
    }

    private boolean isSafe(Location loc) {
        if (loc == null) return false;
        Material below = loc.clone().subtract(0, 1, 0).getBlock().getType();
        if (below == Material.WATER || below == Material.LAVA || below == Material.AIR || below == Material.VOID_AIR) return false;
        Material feet = loc.getBlock().getType();
        Material head = loc.clone().add(0, 1, 0).getBlock().getType();
        return feet.isAir() && head.isAir();
    }
}
