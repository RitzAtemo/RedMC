package red.aviora.redmc.cosmetics.manager;

import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.storage.TemplateStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateManager {

    private final TemplateStorage storage;
    private final Map<UUID, Map<String, CosmeticTemplate>> playerTemplates = new ConcurrentHashMap<>();

    public TemplateManager(TemplateStorage storage) {
        this.storage = storage;
    }

    public void loadForPlayer(UUID uuid) {
        storage.extractBuiltInsForPlayer(uuid);
        Map<String, CosmeticTemplate> map = new ConcurrentHashMap<>();
        for (CosmeticTemplate t : storage.loadAllForPlayer(uuid)) {
            map.put(t.getName().toLowerCase(), t);
        }
        playerTemplates.put(uuid, map);
    }

    public void unloadForPlayer(UUID uuid) {
        playerTemplates.remove(uuid);
    }

    public void reloadAllOnline(Iterable<UUID> onlineUuids) {
        for (UUID uuid : onlineUuids) {
            loadForPlayer(uuid);
        }
    }

    public CosmeticTemplate get(UUID uuid, String name) {
        Map<String, CosmeticTemplate> map = playerTemplates.get(uuid);
        return map != null ? map.get(name.toLowerCase()) : null;
    }

    public boolean exists(UUID uuid, String name) {
        Map<String, CosmeticTemplate> map = playerTemplates.get(uuid);
        return map != null && map.containsKey(name.toLowerCase());
    }

    public List<CosmeticTemplate> getAll(UUID uuid) {
        Map<String, CosmeticTemplate> map = playerTemplates.get(uuid);
        return map != null ? new ArrayList<>(map.values()) : List.of();
    }

    public List<CosmeticTemplate> getForSlot(UUID uuid, CosmeticSlot slot) {
        return getAll(uuid).stream().filter(t -> t.getSlot() == slot).toList();
    }

    public void save(UUID uuid, CosmeticTemplate template) {
        playerTemplates.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
            .put(template.getName().toLowerCase(), template);
        storage.save(uuid, template);
    }

    public boolean delete(UUID uuid, String name) {
        Map<String, CosmeticTemplate> map = playerTemplates.get(uuid);
        boolean removed = map != null && map.remove(name.toLowerCase()) != null;
        boolean fileDeleted = storage.delete(uuid, name);
        return removed || fileDeleted;
    }

    public String exportToSignature(UUID uuid, String name) throws IOException {
        CosmeticTemplate template = get(uuid, name);
        if (template == null) return null;
        return storage.toSignature(template);
    }

    public CosmeticTemplate importFromSignature(UUID uuid, String signature) throws IOException {
        CosmeticTemplate template = storage.fromSignature(signature);
        if (template == null) return null;
        save(uuid, template);
        return template;
    }

    public List<String> getNames(UUID uuid) {
        Map<String, CosmeticTemplate> map = playerTemplates.get(uuid);
        return map != null ? new ArrayList<>(map.keySet()) : List.of();
    }

    public List<String> getNamesForSlot(UUID uuid, CosmeticSlot slot) {
        return getForSlot(uuid, slot).stream().map(CosmeticTemplate::getName).toList();
    }
}
