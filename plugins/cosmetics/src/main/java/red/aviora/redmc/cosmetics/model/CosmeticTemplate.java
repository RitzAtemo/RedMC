package red.aviora.redmc.cosmetics.model;

import java.util.ArrayList;
import java.util.List;

public class CosmeticTemplate {

    private String name;
    private CosmeticSlot slot;
    private String description = "";
    private String author = "unknown";
    private List<ParticleLayer> layers = new ArrayList<>();

    public CosmeticTemplate() {}

    public CosmeticTemplate(String name, CosmeticSlot slot) {
        this.name = name;
        this.slot = slot;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CosmeticSlot getSlot() { return slot; }
    public void setSlot(CosmeticSlot slot) { this.slot = slot; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public List<ParticleLayer> getLayers() { return layers; }
    public void setLayers(List<ParticleLayer> layers) { this.layers = layers; }

    public void addLayer(ParticleLayer layer) { layers.add(layer); }

    public boolean removeLayer(int index) {
        if (index < 0 || index >= layers.size()) return false;
        layers.remove(index);
        return true;
    }

    public ParticleLayer getLayer(int index) {
        if (index < 0 || index >= layers.size()) return null;
        return layers.get(index);
    }
}
