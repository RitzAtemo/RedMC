package red.aviora.redmc.cosmetics.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputManager {

    private static final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    public static void expect(UUID uuid, Consumer<String> handler) {
        pending.put(uuid, handler);
    }

    public static boolean hasPending(UUID uuid) {
        return pending.containsKey(uuid);
    }

    public static Consumer<String> consume(UUID uuid) {
        return pending.remove(uuid);
    }

    public static void cancel(UUID uuid) {
        pending.remove(uuid);
    }
}
