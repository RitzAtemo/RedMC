package red.aviora.redmc.teleport.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackManager {

    private final Map<UUID, Deque<Location>> stacks = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> intervalUses = new ConcurrentHashMap<>();
    private final Map<UUID, Long> intervalStart = new ConcurrentHashMap<>();

    public void push(Player player, Location location) {
        int stackSize = JavaPlugin.getPlugin(TeleportPlugin.class).getConfigManager()
            .getInt("config.yml", "back.stack-size", 10);
        Deque<Location> stack = stacks.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>());
        stack.push(location);
        while (stack.size() > stackSize) {
            ((ArrayDeque<Location>) stack).removeLast();
        }
    }

    public Location pop(Player player) {
        Deque<Location> stack = stacks.get(player.getUniqueId());
        if (stack == null || stack.isEmpty()) return null;
        return stack.pop();
    }

    public boolean canUse(Player player, int limit, boolean bypassCooldown) {
        if (limit < 0) return true;
        if (limit == 0) return false;
        UUID uuid = player.getUniqueId();
        if (!bypassCooldown) {
            long interval = JavaPlugin.getPlugin(TeleportPlugin.class).getConfigManager()
                .getInt("config.yml", "back.cooldown-interval", 3600) * 1000L;
            long now = System.currentTimeMillis();
            Long start = intervalStart.get(uuid);
            if (start == null || now - start > interval) {
                return true;
            }
        }
        return intervalUses.getOrDefault(uuid, 0) < limit;
    }

    public void recordUse(Player player, boolean bypassCooldown) {
        UUID uuid = player.getUniqueId();
        if (!bypassCooldown) {
            long interval = JavaPlugin.getPlugin(TeleportPlugin.class).getConfigManager()
                .getInt("config.yml", "back.cooldown-interval", 3600) * 1000L;
            long now = System.currentTimeMillis();
            Long start = intervalStart.get(uuid);
            if (start == null || now - start > interval) {
                intervalStart.put(uuid, now);
                intervalUses.put(uuid, 1);
                return;
            }
        }
        intervalUses.merge(uuid, 1, Integer::sum);
    }

    public void clearPlayer(UUID uuid) {
        stacks.remove(uuid);
        intervalUses.remove(uuid);
        intervalStart.remove(uuid);
    }
}
