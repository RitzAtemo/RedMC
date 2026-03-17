package red.aviora.redmc.perks.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.io.File;
import java.util.UUID;

public class OfflinePlayerDataUtil {

	public static org.bukkit.inventory.ItemStack[] loadInventory(UUID uuid) {
		CompoundTag tag = loadPlayerData(uuid);
		if (tag == null) return null;

		var registryAccess = MinecraftServer.getServer().registryAccess();
		var ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);

		org.bukkit.inventory.ItemStack[] contents = new org.bukkit.inventory.ItemStack[36];

		ListTag list = tag.getListOrEmpty("Inventory");
		for (int i = 0; i < list.size(); i++) {
			CompoundTag itemTag = list.getCompoundOrEmpty(i);
			ItemStackWithSlot entry = ItemStackWithSlot.CODEC.decode(ops, itemTag)
				.result()
				.map(pair -> pair.getFirst())
				.orElse(null);
			if (entry != null && entry.slot() >= 0 && entry.slot() < 36 && !entry.stack().isEmpty()) {
				contents[entry.slot()] = CraftItemStack.asBukkitCopy(entry.stack());
			}
		}

		return contents;
	}

	public static org.bukkit.inventory.ItemStack[] loadEnderChest(UUID uuid) {
		CompoundTag tag = loadPlayerData(uuid);
		if (tag == null) return null;

		var registryAccess = MinecraftServer.getServer().registryAccess();
		var ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);

		org.bukkit.inventory.ItemStack[] contents = new org.bukkit.inventory.ItemStack[27];
		ListTag list = tag.getListOrEmpty("EnderItems");

		for (int i = 0; i < list.size(); i++) {
			CompoundTag itemTag = list.getCompoundOrEmpty(i);
			ItemStackWithSlot entry = ItemStackWithSlot.CODEC.decode(ops, itemTag)
				.result()
				.map(pair -> pair.getFirst())
				.orElse(null);
			if (entry != null && entry.slot() >= 0 && entry.slot() < 27 && !entry.stack().isEmpty()) {
				contents[entry.slot()] = CraftItemStack.asBukkitCopy(entry.stack());
			}
		}
		return contents;
	}


	public static void saveInventory(UUID uuid, org.bukkit.inventory.ItemStack[] contents) {
		CompoundTag tag = loadPlayerData(uuid);
		if (tag == null) return;

		var registryAccess = MinecraftServer.getServer().registryAccess();
		var ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);

		ListTag inventoryList = new ListTag();
		for (int i = 0; i < 36 && i < contents.length; i++) {
			if (contents[i] != null && !contents[i].getType().isAir()) {
				ItemStack nmsStack = CraftItemStack.asNMSCopy(contents[i]);
				ItemStackWithSlot entry = new ItemStackWithSlot(i, nmsStack);
				var encoded = ItemStackWithSlot.CODEC.encodeStart(ops, entry)
					.result()
					.orElse(new CompoundTag());
				inventoryList.add(encoded);
			}
		}
		tag.put("Inventory", inventoryList);

		savePlayerData(uuid, tag);
	}

	public static void saveEnderChest(UUID uuid, org.bukkit.inventory.ItemStack[] contents) {
		CompoundTag tag = loadPlayerData(uuid);
		if (tag == null) return;

		var registryAccess = MinecraftServer.getServer().registryAccess();
		var ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);

		ListTag enderList = new ListTag();
		for (int i = 0; i < 27 && i < contents.length; i++) {
			if (contents[i] != null && !contents[i].getType().isAir()) {
				ItemStack nmsStack = CraftItemStack.asNMSCopy(contents[i]);
				ItemStackWithSlot entry = new ItemStackWithSlot(i, nmsStack);
				var encoded = ItemStackWithSlot.CODEC.encodeStart(ops, entry)
					.result()
					.orElse(new CompoundTag());
				enderList.add(encoded);
			}
		}
		tag.put("EnderItems", enderList);

		savePlayerData(uuid, tag);
	}

	private static void savePlayerData(UUID uuid, CompoundTag tag) {
		try {
			File worldFolder = Bukkit.getServer().getWorlds().get(0).getWorldFolder();
			File playerDataFile = new File(worldFolder, "playerdata/" + uuid + ".dat");
			NbtIo.writeCompressed(tag, playerDataFile.toPath());
		} catch (Exception e) {
			// Ignore save errors
		}
	}

	private static CompoundTag loadPlayerData(UUID uuid) {
		try {
			File worldFolder = Bukkit.getServer().getWorlds().get(0).getWorldFolder();
			File playerDataFile = new File(worldFolder, "playerdata/" + uuid + ".dat");
			if (!playerDataFile.exists()) return null;
			return NbtIo.readCompressed(playerDataFile.toPath(), NbtAccounter.unlimitedHeap());
		} catch (Exception e) {
			return null;
		}
	}
}
