package red.aviora.redmc.teleport.managers;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import red.aviora.redmc.teleport.TeleportPlugin;
import red.aviora.redmc.teleport.models.SerializableLocation;
import red.aviora.redmc.teleport.utils.TeleportDataStorage;

public class SpawnManager {

    private SerializableLocation spawn;
    private SerializableLocation newbieSpawn;

    public void loadAll() {
        TeleportDataStorage storage = JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage();
        spawn = storage.loadSpawn();
        newbieSpawn = storage.loadNewbieSpawn();
    }

    public void setSpawn(Location location) {
        spawn = SerializableLocation.fromBukkitLocation(location);
        JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage().saveSpawn(spawn);
    }

    public void setNewbieSpawn(Location location) {
        newbieSpawn = SerializableLocation.fromBukkitLocation(location);
        JavaPlugin.getPlugin(TeleportPlugin.class).getDataStorage().saveNewbieSpawn(newbieSpawn);
    }

    public SerializableLocation getSpawn() { return spawn; }
    public SerializableLocation getNewbieSpawn() { return newbieSpawn; }
}
