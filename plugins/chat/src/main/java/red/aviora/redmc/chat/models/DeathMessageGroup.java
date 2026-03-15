package red.aviora.redmc.chat.models;

import java.util.List;
import java.util.Random;

public class DeathMessageGroup {

	private final String id;
	private final List<String> messages;

	public DeathMessageGroup(String id, List<String> messages) {
		this.id = id;
		this.messages = messages;
	}

	public String getRandomMessage(Random random) {
		if (messages.isEmpty()) return null;
		return messages.get(random.nextInt(messages.size()));
	}

	public String getId() { return id; }
	public List<String> getMessages() { return messages; }
}
