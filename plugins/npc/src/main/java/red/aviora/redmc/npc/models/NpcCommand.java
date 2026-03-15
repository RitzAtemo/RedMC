package red.aviora.redmc.npc.models;

public class NpcCommand {

	public enum Type { CONSOLE, PLAYER }

	private final Type type;
	private final String command;

	public NpcCommand(Type type, String command) {
		this.type = type;
		this.command = command;
	}

	public Type getType() { return type; }
	public String getCommand() { return command; }
}
