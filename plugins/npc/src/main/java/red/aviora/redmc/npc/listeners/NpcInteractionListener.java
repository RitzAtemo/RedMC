package red.aviora.redmc.npc.listeners;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import red.aviora.redmc.npc.NpcPlugin;
import red.aviora.redmc.npc.models.NpcData;
import red.aviora.redmc.npc.utils.NpcManager;

import java.lang.reflect.Field;

public class NpcInteractionListener {

	private static Field entityIdField;
	private static Field actionField;

	static {
		try {
			entityIdField = ServerboundInteractPacket.class.getDeclaredField("entityId");
			entityIdField.setAccessible(true);
			actionField = ServerboundInteractPacket.class.getDeclaredField("action");
			actionField.setAccessible(true);
		} catch (Exception e) {
			NpcPlugin.getInstance().getLogger().warning("NPC: failed to reflect ServerboundInteractPacket: " + e.getMessage());
		}
	}

	public static void inject(Player player) {
		ServerPlayer sp = ((CraftPlayer) player).getHandle();
		var pipeline = sp.connection.connection.channel.pipeline();
		String key = "npc_interact_" + player.getUniqueId();
		if (pipeline.get(key) != null) return;

		pipeline.addBefore("packet_handler", key, new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				if (msg instanceof ServerboundInteractPacket packet) {
					handleInteract(player, packet);
				}
				super.channelRead(ctx, msg);
			}
		});
	}

	public static void remove(Player player) {
		try {
			ServerPlayer sp = ((CraftPlayer) player).getHandle();
			var pipeline = sp.connection.connection.channel.pipeline();
			String key = "npc_interact_" + player.getUniqueId();
			if (pipeline.get(key) != null) pipeline.remove(key);
		} catch (Exception ignored) {}
	}

	private static void handleInteract(Player player, ServerboundInteractPacket packet) {
		if (entityIdField == null || actionField == null) return;
		try {
			int entityId = (int) entityIdField.get(packet);
			Object action = actionField.get(packet);

			NpcManager manager = NpcPlugin.getInstance().getNpcManager();
			String npcId = manager.getNpcIdByEntityId(entityId);
			if (npcId == null) return;

			NpcData data = manager.getNpc(npcId);
			if (data == null) return;

			boolean isLeftClick = resolveIsAttack(action);

			manager.executeCommands(player, npcId, isLeftClick ? data.getLeftClickCommands() : data.getRightClickCommands());
		} catch (Exception e) {
			NpcPlugin.getInstance().getLogger().warning("NPC: interaction handling failed: " + e.getMessage());
		}
	}

	private static boolean resolveIsAttack(Object action) {
		try {
			java.lang.reflect.Method getType = action.getClass().getMethod("getType");
			getType.setAccessible(true);
			Object type = getType.invoke(action);
			return "ATTACK".equals(((Enum<?>) type).name());
		} catch (Exception e) {
			NpcPlugin.getInstance().getLogger().warning("NPC: cannot resolve action type: " + e.getMessage());
			return false;
		}
	}
}
