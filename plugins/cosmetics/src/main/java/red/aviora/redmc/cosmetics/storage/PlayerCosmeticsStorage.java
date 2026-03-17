package red.aviora.redmc.cosmetics.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerCosmeticsStorage {

    private final File dataDir;

    public PlayerCosmeticsStorage() {
        this.dataDir = new File(CosmeticsPlugin.getInstance().getDataFolder(), "playerdata");
        if (!dataDir.exists()) dataDir.mkdirs();
    }

    public PlayerCosmetics load(UUID uuid) {
        File file = new File(dataDir, uuid + ".yml");
        PlayerCosmetics cosmetics = new PlayerCosmetics(uuid);
        if (!file.exists()) return cosmetics;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        cosmetics.setVisible(yaml.getBoolean("visible", true));

        var equippedSection = yaml.getConfigurationSection("equipped");
        if (equippedSection != null) {
            for (String key : equippedSection.getKeys(false)) {
                CosmeticSlot.fromString(key).ifPresent(slot ->
                    cosmetics.equip(slot, equippedSection.getString(key))
                );
            }
        }
        return cosmetics;
    }

    public void save(PlayerCosmetics cosmetics) {
        File file = new File(dataDir, cosmetics.getUuid() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("visible", cosmetics.isVisible());
        for (var entry : cosmetics.getEquippedMap().entrySet()) {
            yaml.set("equipped." + entry.getKey().name(), entry.getValue());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            CosmeticsPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not save player cosmetics: " + cosmetics.getUuid(), e);
        }
    }

    public void delete(UUID uuid) {
        File file = new File(dataDir, uuid + ".yml");
        if (file.exists()) file.delete();
    }
}
