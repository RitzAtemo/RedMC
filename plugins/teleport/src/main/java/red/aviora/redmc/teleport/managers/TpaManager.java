package red.aviora.redmc.teleport.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.TeleportRequest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TpaManager {

    private final Map<UUID, TeleportRequest> pendingByTarget = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> outgoingByRequester = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastRequestTime = new ConcurrentHashMap<>();

    public TeleportRequest sendRequest(Player requester, Player target, boolean toRequester) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        UUID requesterUuid = requester.getUniqueId();
        UUID targetUuid = target.getUniqueId();

        UUID previousTarget = outgoingByRequester.get(requesterUuid);
        if (previousTarget != null) {
            pendingByTarget.remove(previousTarget);
            outgoingByRequester.remove(requesterUuid);
        }

        TeleportRequest request = new TeleportRequest(requesterUuid, targetUuid, toRequester, System.currentTimeMillis());
        pendingByTarget.put(targetUuid, request);
        outgoingByRequester.put(requesterUuid, targetUuid);
        lastRequestTime.put(requesterUuid, System.currentTimeMillis());

        long timeout = plugin.getConfigManager().getInt("config.yml", "tpa.timeout", 60);
        Bukkit.getAsyncScheduler().runDelayed(plugin, task -> {
            TeleportRequest current = pendingByTarget.get(targetUuid);
            if (current != null && current.getCreatedAt() == request.getCreatedAt()) {
                pendingByTarget.remove(targetUuid);
                outgoingByRequester.remove(requesterUuid);
            }
        }, timeout, TimeUnit.SECONDS);

        return request;
    }

    public TeleportRequest getIncoming(UUID targetUuid) {
        return pendingByTarget.get(targetUuid);
    }

    public void cancelRequest(UUID targetUuid) {
        TeleportRequest req = pendingByTarget.remove(targetUuid);
        if (req != null) {
            outgoingByRequester.remove(req.getRequesterUuid());
        }
    }

    public TeleportRequest accept(Player target) {
        TeleportRequest request = pendingByTarget.remove(target.getUniqueId());
        if (request == null) return null;
        outgoingByRequester.remove(request.getRequesterUuid());
        Player requester = Bukkit.getPlayer(request.getRequesterUuid());
        if (requester == null || !requester.isOnline()) return request;
        if (request.isToRequester()) {
            target.teleportAsync(requester.getLocation());
        } else {
            requester.teleportAsync(target.getLocation());
        }
        return request;
    }

    public TeleportRequest deny(Player target) {
        TeleportRequest request = pendingByTarget.remove(target.getUniqueId());
        if (request == null) return null;
        outgoingByRequester.remove(request.getRequesterUuid());
        return request;
    }

    public void cancelOutgoing(UUID requesterUuid) {
        UUID targetUuid = outgoingByRequester.remove(requesterUuid);
        if (targetUuid != null) {
            pendingByTarget.remove(targetUuid);
        }
    }

    public void cancelAll() {
        pendingByTarget.clear();
        outgoingByRequester.clear();
    }

    public boolean isOnCooldown(Player player) {
        TeleportPlugin plugin = JavaPlugin.getPlugin(TeleportPlugin.class);
        long cooldown = plugin.getConfigManager().getInt("config.yml", "tpa.cooldown", 30);
        Long last = lastRequestTime.get(player.getUniqueId());
        if (last == null) return false;
        return System.currentTimeMillis() - last < cooldown * 1000L;
    }

    public void clearPlayer(UUID uuid) {
        TeleportRequest incoming = pendingByTarget.remove(uuid);
        if (incoming != null) {
            outgoingByRequester.remove(incoming.getRequesterUuid());
        }
        UUID targetUuid = outgoingByRequester.remove(uuid);
        if (targetUuid != null) {
            pendingByTarget.remove(targetUuid);
        }
        lastRequestTime.remove(uuid);
    }
}
