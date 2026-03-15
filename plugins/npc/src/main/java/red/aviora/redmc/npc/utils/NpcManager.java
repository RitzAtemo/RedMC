package red.aviora.redmc.npc.utils;

import com.google.common.collect.HashMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.papermc.paper.adventure.PaperAdventure;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.models.NpcCommand;
import red.aviora.redmc.npc.models.NpcData;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NpcManager {

	private static final AtomicInteger ENTITY_ID_COUNTER = new AtomicInteger(900_000);
	private static final sun.misc.Unsafe UNSAFE = initUnsafe();

	@SuppressWarnings("unchecked")
	private static final EntityDataAccessor<Optional<net.minecraft.network.chat.Component>> DATA_CUSTOM_NAME =
		getAccessor(net.minecraft.world.entity.Entity.class, "DATA_CUSTOM_NAME");

	@SuppressWarnings("unchecked")
	private static final EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE =
		getAccessor(net.minecraft.world.entity.Entity.class, "DATA_CUSTOM_NAME_VISIBLE");

	private volatile int skinLayersDataId = -1;

	private static sun.misc.Unsafe initUnsafe() {
		try {
			Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (sun.misc.Unsafe) f.get(null);
		} catch (Exception e) {
			throw new RuntimeException("Cannot obtain Unsafe", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> EntityDataAccessor<T> getAccessor(Class<?> cls, String fieldName) {
		try {
			Field f = cls.getDeclaredField(fieldName);
			f.setAccessible(true);
			return (EntityDataAccessor<T>) f.get(null);
		} catch (ReflectiveOperationException e) {
			// Fallback: use Unsafe to bypass module restrictions
			try {
				Field f = cls.getDeclaredField(fieldName);
				return (EntityDataAccessor<T>) UNSAFE.getObject(
					UNSAFE.staticFieldBase(f), UNSAFE.staticFieldOffset(f));
			} catch (Exception ex) {
				throw new RuntimeException("Cannot access " + cls.getSimpleName() + "." + fieldName, ex);
			}
		}
	}

	private final Map<String, NpcData> npcs = new ConcurrentHashMap<>();
	private final Map<String, Integer> entityIds = new ConcurrentHashMap<>();
	private final Map<String, UUID> entityUUIDs = new ConcurrentHashMap<>();
	private final Map<String, Long> interactCooldowns = new ConcurrentHashMap<>();

	private final NpcDataStorage storage;
	private Object lookTask;

	public NpcManager() {
		this.storage = new NpcDataStorage();
	}

	public void loadAll() {
		npcs.clear();
		entityIds.clear();
		entityUUIDs.clear();

		Map<String, NpcData> loaded = storage.loadNpcs();
		for (NpcData data : loaded.values()) {
			npcs.put(data.getId(), data);
			entityIds.put(data.getId(), ENTITY_ID_COUNTER.getAndIncrement());
			entityUUIDs.put(data.getId(), UUID.randomUUID());
		}

		startLookTask();
	}

	public void spawnAllForPlayer(Player player) {
		for (NpcData data : npcs.values()) {
			spawnForPlayer(data, player);
		}
	}

	public void despawnAll() {
		stopLookTask();
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (NpcData data : npcs.values()) {
				despawnForPlayer(data, player);
			}
		}
	}

	public void spawnForPlayer(NpcData data, Player player) {
		World world = Bukkit.getWorld(data.getWorld());
		if (world == null || !world.equals(player.getWorld())) return;

		int entityId = entityIds.get(data.getId());
		UUID uuid = entityUUIDs.get(data.getId());

		GameProfile profile = buildProfile(uuid, data);

		// 1. Register profile for skin loading (not listed — hidden from tab)
		var entry = new ClientboundPlayerInfoUpdatePacket.Entry(
			uuid, profile, false, 0, GameType.SURVIVAL, null, false, 0, null
		);
		sendPacket(player, new ClientboundPlayerInfoUpdatePacket(
			EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), List.of(entry)
		));

		// 2. Spawn player entity
		sendPacket(player, new ClientboundAddEntityPacket(
			entityId, uuid,
			data.getX(), data.getY(), data.getZ(),
			data.getPitch(), data.getYaw(),
			EntityType.PLAYER, 0,
			Vec3.ZERO,
			data.getYaw()
		));

		// 3. Initial head rotation
		sendHeadRotation(player, entityId, data.getYaw());

		// 4. Entity metadata: custom name + all skin layers visible
		sendMetadata(player, entityId, data);

		// 5. Equipment
		sendEquipment(player, entityId, data);
	}

	public void despawnForPlayer(NpcData data, Player player) {
		int entityId = entityIds.getOrDefault(data.getId(), -1);
		UUID uuid = entityUUIDs.get(data.getId());

		if (entityId != -1) {
			sendPacket(player, new ClientboundRemoveEntitiesPacket(entityId));
		}
		if (uuid != null) {
			sendPacket(player, new ClientboundPlayerInfoRemovePacket(List.of(uuid)));
		}
	}

	private void respawnAll(NpcData data) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			despawnForPlayer(data, player);
			spawnForPlayer(data, player);
		}
	}

	private GameProfile buildProfile(UUID uuid, NpcData data) {
		if (data.hasSkin()) {
			var props = HashMultimap.<String, Property>create();
			props.put("textures", new Property("textures", data.getSkinTexture(), data.getSkinSignature()));
			return new GameProfile(uuid, data.getDisplayName(), new PropertyMap(props));
		}
		return new GameProfile(uuid, data.getDisplayName());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void sendMetadata(Player player, int entityId, NpcData data) {
		net.minecraft.network.chat.Component nmsName =
			PaperAdventure.asVanilla(ApiUtils.formatText(data.getDisplayName()));

		ServerPlayer sp = ((CraftPlayer) player).getHandle();

		if (skinLayersDataId == -1) {
			for (SynchedEntityData.DataValue<?> dv : sp.getEntityData().packAll()) {
				if (dv.value() instanceof Byte && dv.id() > 8) {
					skinLayersDataId = dv.id();
					break;
				}
			}
		}

		List<SynchedEntityData.DataValue<?>> values = new ArrayList<>();
		values.add(SynchedEntityData.DataValue.create(DATA_CUSTOM_NAME, Optional.of(nmsName)));
		values.add(SynchedEntityData.DataValue.create(DATA_CUSTOM_NAME_VISIBLE, true));
		if (skinLayersDataId != -1) {
			values.add(new SynchedEntityData.DataValue(skinLayersDataId, EntityDataSerializers.BYTE, (byte) 0x7f));
		}

		sendPacket(player, new ClientboundSetEntityDataPacket(entityId, values));
	}

	private void sendEquipment(Player player, int entityId, NpcData data) {
		if (data.getEquipment().isEmpty()) return;
		List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
		data.getEquipment().forEach((slotName, itemId) -> {
			EquipmentSlot slot = resolveSlot(slotName);
			if (slot == null) return;
			String normalized = itemId.contains(":") ? itemId : "minecraft:" + itemId;
			Material material = Material.matchMaterial(normalized);
			if (material == null || material.isLegacy() || material.isAir()) return;
			slots.add(Pair.of(slot, new ItemStack(CraftMagicNumbers.getItem(material))));
		});
		if (!slots.isEmpty()) {
			sendPacket(player, new ClientboundSetEquipmentPacket(entityId, slots));
		}
	}

	private static EquipmentSlot resolveSlot(String name) {
		return switch (name.toLowerCase()) {
			case "mainhand" -> EquipmentSlot.MAINHAND;
			case "offhand"  -> EquipmentSlot.OFFHAND;
			case "head"     -> EquipmentSlot.HEAD;
			case "chest"    -> EquipmentSlot.CHEST;
			case "legs"     -> EquipmentSlot.LEGS;
			case "feet"     -> EquipmentSlot.FEET;
			default -> null;
		};
	}

	private void sendHeadRotation(Player player, int entityId, float yaw) {
		ClientboundRotateHeadPacket packet = createHeadRotPacket(entityId, yaw);
		if (packet != null) sendPacket(player, packet);
	}

	private ClientboundRotateHeadPacket createHeadRotPacket(int entityId, float yaw) {
		try {
			ClientboundRotateHeadPacket packet =
				(ClientboundRotateHeadPacket) UNSAFE.allocateInstance(ClientboundRotateHeadPacket.class);

			for (Field field : ClientboundRotateHeadPacket.class.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getType() == int.class) {
					UNSAFE.putInt(packet, UNSAFE.objectFieldOffset(field), entityId);
				} else if (field.getType() == byte.class) {
					UNSAFE.putByte(packet, UNSAFE.objectFieldOffset(field), packDegrees(yaw));
				}
			}

			return packet;
		} catch (Exception e) {
			ApiUtils.log("NPC: failed to create head rotation packet: " + e.getMessage());
			return null;
		}
	}

	private void startLookTask() {
		stopLookTask();

		NpcPlugin plugin = NpcPlugin.getInstance();
		boolean enabled = plugin.getConfigManager().getBoolean("config.yml", "look-at-player.enabled", true);
		if (!enabled) return;

		long intervalMs = plugin.getConfigManager().getInt("config.yml", "look-at-player.interval-ms", 500);
		double range = plugin.getConfigManager().getDouble("config.yml", "look-at-player.range", 16.0);

		lookTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> {
			for (NpcData data : npcs.values()) {
				World world = Bukkit.getWorld(data.getWorld());
				if (world == null) continue;

				Player nearest = findNearest(data, world, range);
				if (nearest == null) continue;

				float[] angles = calcLookAngles(data, nearest);
				float yaw = angles[0];
				float pitch = angles[1];

				int entityId = entityIds.getOrDefault(data.getId(), -1);
				if (entityId == -1) continue;

				for (Player viewer : world.getPlayers()) {
					sendPacket(viewer, new ClientboundMoveEntityPacket.Rot(
						entityId, packDegrees(yaw), packDegrees(pitch), true
					));
					sendHeadRotation(viewer, entityId, yaw);
				}
			}
		}, 0L, intervalMs, TimeUnit.MILLISECONDS);
	}

	private void stopLookTask() {
		if (lookTask != null) {
			try {
				lookTask.getClass().getMethod("cancel").invoke(lookTask);
			} catch (Exception ignored) {}
			lookTask = null;
		}
	}

	private Player findNearest(NpcData data, World world, double range) {
		Player nearest = null;
		double minDist = range * range;

		for (Player p : world.getPlayers()) {
			try {
				double dx = p.getX() - data.getX();
				double dz = p.getZ() - data.getZ();
				double dist = dx * dx + dz * dz;
				if (dist < minDist) {
					minDist = dist;
					nearest = p;
				}
			} catch (Exception ignored) {}
		}

		return nearest;
	}

	private float[] calcLookAngles(NpcData data, Player target) {
		double dx = target.getX() - data.getX();
		double dy = target.getEyeLocation().getY() - (data.getY() + 1.62);
		double dz = target.getZ() - data.getZ();
		double distXZ = Math.sqrt(dx * dx + dz * dz);
		float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
		float pitch = (float) Math.toDegrees(-Math.atan2(dy, distXZ));
		return new float[]{yaw, pitch};
	}

	private void sendPacket(Player player, Packet<?> packet) {
		try {
			ServerPlayer sp = ((CraftPlayer) player).getHandle();
			sp.connection.send(packet);
		} catch (Exception e) {
			ApiUtils.log("NPC: packet send failed: " + e.getMessage());
		}
	}

	private byte packDegrees(float degrees) {
		return (byte) Math.floor(degrees * 256.0f / 360.0f);
	}

	public NpcData createNpc(String id, String displayName, String world,
			double x, double y, double z, float yaw, float pitch) {
		NpcData data = new NpcData(id, displayName, world, x, y, z, yaw, pitch);
		npcs.put(id, data);
		entityIds.put(id, ENTITY_ID_COUNTER.getAndIncrement());
		entityUUIDs.put(id, UUID.randomUUID());
		storage.saveNpcs(npcs);

		for (Player player : Bukkit.getOnlinePlayers()) {
			spawnForPlayer(data, player);
		}
		return data;
	}

	public boolean deleteNpc(String id) {
		NpcData data = npcs.remove(id);
		if (data == null) return false;

		for (Player player : Bukkit.getOnlinePlayers()) {
			despawnForPlayer(data, player);
		}

		entityIds.remove(id);
		entityUUIDs.remove(id);
		storage.saveNpcs(npcs);
		return true;
	}

	public boolean setDisplayName(String id, String displayName) {
		NpcData data = npcs.get(id);
		if (data == null) return false;

		for (Player player : Bukkit.getOnlinePlayers()) {
			despawnForPlayer(data, player);
		}

		data.setDisplayName(displayName);
		entityIds.put(id, ENTITY_ID_COUNTER.getAndIncrement());
		entityUUIDs.put(id, UUID.randomUUID());
		storage.saveNpcs(npcs);

		for (Player player : Bukkit.getOnlinePlayers()) {
			spawnForPlayer(data, player);
		}

		return true;
	}

	public boolean setSkin(String id, String texture, String signature, String ownerName) {
		NpcData data = npcs.get(id);
		if (data == null) return false;

		for (Player player : Bukkit.getOnlinePlayers()) {
			despawnForPlayer(data, player);
		}

		data.setSkinOwner(ownerName);
		data.setSkinTexture(texture);
		data.setSkinSignature(signature);

		entityIds.put(id, ENTITY_ID_COUNTER.getAndIncrement());
		entityUUIDs.put(id, UUID.randomUUID());
		storage.saveNpcs(npcs);

		for (Player player : Bukkit.getOnlinePlayers()) {
			spawnForPlayer(data, player);
		}

		return true;
	}

	public boolean teleportToPlayer(String id, Player target) {
		NpcData data = npcs.get(id);
		if (data == null) return false;

		data.setWorld(target.getWorld().getName());
		data.setX(target.getX());
		data.setY(target.getY());
		data.setZ(target.getZ());
		data.setYaw(target.getYaw());
		data.setPitch(target.getPitch());

		storage.saveNpcs(npcs);
		respawnAll(data);
		return true;
	}

	public String getNpcIdByEntityId(int entityId) {
		for (Map.Entry<String, Integer> entry : entityIds.entrySet()) {
			if (entry.getValue() == entityId) return entry.getKey();
		}
		return null;
	}

	public boolean addCommand(String id, boolean leftClick, NpcCommand command) {
		NpcData data = npcs.get(id);
		if (data == null) return false;
		if (leftClick) data.getLeftClickCommands().add(command);
		else data.getRightClickCommands().add(command);
		storage.saveNpcs(npcs);
		return true;
	}

	public boolean clearCommands(String id, boolean leftClick) {
		NpcData data = npcs.get(id);
		if (data == null) return false;
		if (leftClick) data.getLeftClickCommands().clear();
		else data.getRightClickCommands().clear();
		storage.saveNpcs(npcs);
		return true;
	}

	public void executeCommands(Player player, String npcId, List<NpcCommand> commands) {
		if (commands.isEmpty()) return;

		long cooldownMs = NpcPlugin.getInstance().getConfigManager()
			.getInt("config.yml", "interaction.cooldown-ms", 500);
		String cooldownKey = player.getUniqueId() + ":" + npcId;
		long now = System.currentTimeMillis();
		Long last = interactCooldowns.get(cooldownKey);
		if (last != null && now - last < cooldownMs) return;
		interactCooldowns.put(cooldownKey, now);

		NpcPlugin plugin = NpcPlugin.getInstance();
		List<NpcCommand> snapshot = List.copyOf(commands);

		for (NpcCommand cmd : snapshot) {
			String command = applyPlaceholders(cmd.getCommand(), player);
			if (cmd.getType() == NpcCommand.Type.CONSOLE) {
				plugin.getServer().getGlobalRegionScheduler().execute(plugin, () ->
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
			} else {
				plugin.getServer().getRegionScheduler().execute(plugin, player.getLocation(), () ->
					player.performCommand(command));
			}
		}
	}

	private String applyPlaceholders(String command, Player player) {
		return command
			.replace("{player}", player.getName())
			.replace("{uuid}", player.getUniqueId().toString())
			.replace("{world}", player.getWorld().getName())
			.replace("{x}", String.valueOf((int) player.getX()))
			.replace("{y}", String.valueOf((int) player.getY()))
			.replace("{z}", String.valueOf((int) player.getZ()));
	}

	public boolean setEquipment(String id, String slot, String itemId) {
		NpcData data = npcs.get(id);
		if (data == null) return false;
		if (resolveSlot(slot) == null) return false;
		if (itemId == null || itemId.equalsIgnoreCase("air") || itemId.equalsIgnoreCase("minecraft:air")) {
			data.getEquipment().remove(slot.toLowerCase());
		} else {
			data.getEquipment().put(slot.toLowerCase(), itemId);
		}
		storage.saveNpcs(npcs);
		int entityId = entityIds.getOrDefault(id, -1);
		if (entityId != -1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				sendEquipment(player, entityId, data);
			}
		}
		return true;
	}

	public void clearPlayerCooldowns(Player player) {
		String prefix = player.getUniqueId() + ":";
		interactCooldowns.keySet().removeIf(k -> k.startsWith(prefix));
	}

	public NpcData getNpc(String id) { return npcs.get(id); }
	public Map<String, NpcData> getAllNpcs() { return Collections.unmodifiableMap(npcs); }
	public Collection<String> getNpcIds() { return npcs.keySet(); }
}
