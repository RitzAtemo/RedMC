package red.aviora.redmc.teleport.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class LimitUtils {

    public static int getHomeLimit(Player player) {
        if (player.hasPermission("redmc.teleport.homes.bypass.limit")) return -1;
        int max = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue;
            String perm = info.getPermission();
            if (perm.startsWith("redmc.teleport.homes.")) {
                String suffix = perm.substring("redmc.teleport.homes.".length());
                try {
                    int n = Integer.parseInt(suffix);
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return max < 0 ? 1 : max;
    }

    public static int getBackLimit(Player player) {
        if (player.hasPermission("redmc.teleport.back.bypass.limit")) return -1;
        int max = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue;
            String perm = info.getPermission();
            if (perm.startsWith("redmc.teleport.back.")) {
                String suffix = perm.substring("redmc.teleport.back.".length());
                try {
                    int n = Integer.parseInt(suffix);
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return max < 0 ? 1 : max;
    }

    public static boolean hasBackCooldownBypass(Player player) {
        return player.hasPermission("redmc.teleport.back.bypass.cooldown");
    }

    public static int getRtpLimit(Player player) {
        if (player.hasPermission("redmc.teleport.rtp.bypass.limit")) return -1;
        int max = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue;
            String perm = info.getPermission();
            if (perm.startsWith("redmc.teleport.rtp.")) {
                String suffix = perm.substring("redmc.teleport.rtp.".length());
                try {
                    int n = Integer.parseInt(suffix);
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return max < 0 ? 1 : max;
    }

    public static boolean hasRtpCooldownBypass(Player player) {
        return player.hasPermission("redmc.teleport.rtp.bypass.cooldown");
    }
}
