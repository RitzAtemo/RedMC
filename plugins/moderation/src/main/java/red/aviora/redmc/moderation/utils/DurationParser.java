package red.aviora.redmc.moderation.utils;

public class DurationParser {

    /**
     * Parse a duration string to seconds. Returns -1 for permanent.
     * Accepts: perm, permanent, 1d, 2h, 30m, 60s
     */
    public static long parse(String input) {
        if (input == null) return -2;
        String s = input.trim().toLowerCase();
        if (s.equals("perm") || s.equals("permanent")) return -1;

        long total = 0;
        StringBuilder num = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else {
                if (num.isEmpty()) return -2;
                long value = Long.parseLong(num.toString());
                num.setLength(0);
                switch (c) {
                    case 'd' -> total += value * 86400L;
                    case 'h' -> total += value * 3600L;
                    case 'm' -> total += value * 60L;
                    case 's' -> total += value;
                    default -> { return -2; }
                }
            }
        }
        if (!num.isEmpty()) return -2; // trailing number without unit
        if (total <= 0) return -2;
        return total;
    }

    /**
     * Returns true if the parse result is a valid duration (not error sentinel -2).
     */
    public static boolean isValid(long parsed) {
        return parsed == -1 || parsed > 0;
    }

    /**
     * Format seconds to human-readable string like "2d 3h 15m 10s".
     * -1 → "Permanent"
     */
    public static String format(long seconds) {
        if (seconds == -1) return "Permanent";
        if (seconds <= 0) return "0s";

        long d = seconds / 86400;
        long h = (seconds % 86400) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (d > 0) sb.append(d).append("d ");
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        if (s > 0) sb.append(s).append("s");
        return sb.toString().trim();
    }

    /**
     * Format remaining time from an expiry timestamp in millis.
     */
    public static String formatRemaining(long expiresAtMillis) {
        long remaining = (expiresAtMillis - System.currentTimeMillis()) / 1000L;
        if (remaining <= 0) return "0s";
        return format(remaining);
    }
}
