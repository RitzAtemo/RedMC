package red.aviora.redmc.cosmetics.renderer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TrailTracker {

    private final int historySize;
    private final double minDistance;
    private final Map<UUID, Deque<Location>> trails = new ConcurrentHashMap<>();

    public TrailTracker(int historySize, double minDistance) {
        this.historySize = historySize;
        this.minDistance = minDistance;
    }

    public void onMove(Player player, Location to) {
        Deque<Location> trail = trails.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>());
        if (!trail.isEmpty()) {
            Location last = trail.peekLast();
            if (last.distanceSquared(to) < minDistance * minDistance) {
                return;
            }
        }
        trail.addLast(to.clone());
        while (trail.size() > historySize) {
            trail.pollFirst();
        }
    }

    public List<Location> getTrail(Player player) {
        Deque<Location> trail = trails.get(player.getUniqueId());
        if (trail == null) return List.of();
        return new ArrayList<>(trail);
    }

    public void remove(Player player) {
        trails.remove(player.getUniqueId());
    }

    public void clear() {
        trails.clear();
    }
}
