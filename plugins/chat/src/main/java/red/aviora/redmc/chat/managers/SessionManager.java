package red.aviora.redmc.chat.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

	public record MessageRecord(UUID senderUuid, String message) {}

	private final Map<UUID, MessageRecord> lastMessages = new ConcurrentHashMap<>();

	public void recordReceived(UUID receiver, UUID sender, String message) {
		lastMessages.put(receiver, new MessageRecord(sender, message));
	}

	public UUID getLastSender(UUID player) {
		MessageRecord record = lastMessages.get(player);
		return record != null ? record.senderUuid() : null;
	}

	public String getLastReceivedMessage(UUID player) {
		MessageRecord record = lastMessages.get(player);
		return record != null ? record.message() : null;
	}

	public void clearSession(UUID player) {
		lastMessages.remove(player);
	}
}
