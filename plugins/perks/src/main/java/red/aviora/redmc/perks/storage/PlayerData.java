package red.aviora.redmc.perks.storage;

import org.bukkit.inventory.ItemStack;

public class PlayerData {

	private String joinMessage;
	private String quitMessage;
	private ItemStack[] backpackContents;

	public String getJoinMessage() { return joinMessage; }
	public void setJoinMessage(String joinMessage) { this.joinMessage = joinMessage; }

	public String getQuitMessage() { return quitMessage; }
	public void setQuitMessage(String quitMessage) { this.quitMessage = quitMessage; }

	public ItemStack[] getBackpackContents() { return backpackContents; }
	public void setBackpackContents(ItemStack[] backpackContents) { this.backpackContents = backpackContents; }
}
