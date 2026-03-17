package red.aviora.redmc.cosmetics.manager;

import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.storage.TemplateStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateManager {

    private final TemplateStorage storage;
    private final Map<String, CosmeticTemplate> templates = new ConcurrentHashMap<>();

    public TemplateManager(TemplateStorage storage) {
        this.storage = storage;
    }

    public void loadAll() {
        templates.clear();
        for (CosmeticTemplate template : storage.loadAll()) {
            templates.put(template.getName().toLowerCase(), template);
        }
    }

    public CosmeticTemplate get(String name) {
        return templates.get(name.toLowerCase());
    }

    public boolean exists(String name) {
        return templates.containsKey(name.toLowerCase());
    }

    public List<CosmeticTemplate> getAll() {
        return new ArrayList<>(templates.values());
    }

    public List<CosmeticTemplate> getForSlot(CosmeticSlot slot) {
        return templates.values().stream()
            .filter(t -> t.getSlot() == slot)
            .toList();
    }

    public void save(CosmeticTemplate template) {
        templates.put(template.getName().toLowerCase(), template);
        storage.save(template);
    }

    public boolean delete(String name) {
        boolean removed = templates.remove(name.toLowerCase()) != null;
        boolean fileDeleted = storage.delete(name);
        return removed || fileDeleted;
    }

    public String exportToSignature(String name) throws IOException {
        CosmeticTemplate template = get(name);
        if (template == null) return null;
        return storage.toSignature(template);
    }

    public CosmeticTemplate importFromSignature(String signature) throws IOException {
        CosmeticTemplate template = storage.fromSignature(signature);
        if (template == null) return null;
        storage.save(template);
        templates.put(template.getName().toLowerCase(), template);
        return template;
    }

    public List<String> getNames() {
        return new ArrayList<>(templates.keySet());
    }

    public List<String> getNamesForSlot(CosmeticSlot slot) {
        return templates.values().stream()
            .filter(t -> t.getSlot() == slot)
            .map(CosmeticTemplate::getName)
            .toList();
    }
}
