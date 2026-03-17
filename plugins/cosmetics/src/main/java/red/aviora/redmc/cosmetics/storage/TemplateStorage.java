package red.aviora.redmc.cosmetics.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.ParticleLayer;
import red.aviora.redmc.cosmetics.model.ParticleShape;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TemplateStorage {

    private static final String[] BUILT_IN = {
        "fire_trail", "frost_trail", "void_trail",
        "portal_orbit", "enchant_aura", "spiral_orbit",
        "angel_wings", "devil_wings",
        "golden_crown",
        "soul_halo",
        "rainbow_feet",
        "nature_head",
        "smoke_back",
        "magic_shoulders"
    };

    private final File templatesDir;

    public TemplateStorage() {
        this.templatesDir = new File(CosmeticsPlugin.getInstance().getDataFolder(), "templates");
        if (!templatesDir.exists()) templatesDir.mkdirs();
        extractBuiltIn();
    }

    private void extractBuiltIn() {
        for (String name : BUILT_IN) {
            File target = new File(templatesDir, name + ".yml");
            if (!target.exists()) {
                String resource = "templates/" + name + ".yml";
                try (InputStream in = CosmeticsPlugin.getInstance().getResource(resource)) {
                    if (in == null) continue;
                    CosmeticsPlugin.getInstance().saveResource(resource, false);
                } catch (Exception e) {
                    CosmeticsPlugin.getInstance().getLogger().log(Level.WARNING, "Could not extract built-in template: " + name, e);
                }
            }
        }
    }

    public List<CosmeticTemplate> loadAll() {
        List<CosmeticTemplate> templates = new ArrayList<>();
        File[] files = templatesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return templates;
        for (File file : files) {
            CosmeticTemplate template = load(file);
            if (template != null) templates.add(template);
        }
        return templates;
    }

    public CosmeticTemplate load(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return fromYaml(yaml);
    }

    public CosmeticTemplate loadFromResource(String resourcePath) {
        try (InputStream in = CosmeticsPlugin.getInstance().getResource(resourcePath)) {
            if (in == null) return null;
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
            return fromYaml(yaml);
        } catch (Exception e) {
            return null;
        }
    }

    private CosmeticTemplate fromYaml(YamlConfiguration yaml) {
        String name = yaml.getString("name");
        String slotStr = yaml.getString("slot");
        if (name == null || slotStr == null) return null;
        CosmeticSlot slot = CosmeticSlot.fromString(slotStr).orElse(null);
        if (slot == null) return null;

        CosmeticTemplate template = new CosmeticTemplate(name, slot);
        template.setDescription(yaml.getString("description", ""));
        template.setAuthor(yaml.getString("author", "unknown"));

        List<?> layerList = yaml.getList("layers");
        if (layerList != null) {
            for (Object obj : layerList) {
                if (!(obj instanceof java.util.Map<?, ?> map)) continue;
                ParticleLayer layer = layerFromMap(map);
                if (layer != null) template.addLayer(layer);
            }
        }
        return template;
    }

    private ParticleLayer layerFromMap(java.util.Map<?, ?> map) {
        ParticleLayer layer = new ParticleLayer();
        String particle = getString(map, "particle", "FLAME");
        layer.setParticle(particle.toUpperCase());
        String shapeStr = getString(map, "shape", "POINT");
        ParticleShape.fromString(shapeStr).ifPresent(layer::setShape);
        layer.setCount(getInt(map, "count", 3));
        layer.setSpeed(getDouble(map, "speed", 0.05));
        layer.setOffsetX(getDouble(map, "offset-x", 0.1));
        layer.setOffsetY(getDouble(map, "offset-y", 0.1));
        layer.setOffsetZ(getDouble(map, "offset-z", 0.1));
        layer.setYOffset(getDouble(map, "y-offset", 0.1));
        layer.setTickRate(Math.max(1, getInt(map, "tick-rate", 1)));
        layer.setShapeRadius(getDouble(map, "shape-radius", 1.0));
        layer.setShapePoints(getInt(map, "shape-points", 16));
        layer.setDustColorR(getInt(map, "dust-color-r", 255));
        layer.setDustColorG(getInt(map, "dust-color-g", 255));
        layer.setDustColorB(getInt(map, "dust-color-b", 255));
        layer.setDustColorToR(getInt(map, "dust-color-to-r", 255));
        layer.setDustColorToG(getInt(map, "dust-color-to-g", 255));
        layer.setDustColorToB(getInt(map, "dust-color-to-b", 255));
        layer.setDustSize((float) getDouble(map, "dust-size", 1.0));
        return layer;
    }

    public void save(CosmeticTemplate template) {
        File file = new File(templatesDir, template.getName() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("name", template.getName());
        yaml.set("slot", template.getSlot().name());
        yaml.set("description", template.getDescription());
        yaml.set("author", template.getAuthor());

        List<java.util.Map<String, Object>> layers = new ArrayList<>();
        for (ParticleLayer layer : template.getLayers()) {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("particle", layer.getParticle());
            map.put("shape", layer.getShape().name());
            map.put("count", layer.getCount());
            map.put("speed", layer.getSpeed());
            map.put("offset-x", layer.getOffsetX());
            map.put("offset-y", layer.getOffsetY());
            map.put("offset-z", layer.getOffsetZ());
            map.put("y-offset", layer.getYOffset());
            map.put("tick-rate", layer.getTickRate());
            map.put("shape-radius", layer.getShapeRadius());
            map.put("shape-points", layer.getShapePoints());
            map.put("dust-color-r", layer.getDustColorR());
            map.put("dust-color-g", layer.getDustColorG());
            map.put("dust-color-b", layer.getDustColorB());
            map.put("dust-color-to-r", layer.getDustColorToR());
            map.put("dust-color-to-g", layer.getDustColorToG());
            map.put("dust-color-to-b", layer.getDustColorToB());
            map.put("dust-size", layer.getDustSize());
            layers.add(map);
        }
        yaml.set("layers", layers);

        try {
            yaml.save(file);
        } catch (IOException e) {
            CosmeticsPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not save template: " + template.getName(), e);
        }
    }

    public boolean delete(String name) {
        File file = new File(templatesDir, name + ".yml");
        return file.exists() && file.delete();
    }

    public File getTemplatesDir() { return templatesDir; }

    public File getExportsDir() {
        File dir = new File(CosmeticsPlugin.getInstance().getDataFolder(), "exports");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public File getImportsDir() {
        File dir = new File(CosmeticsPlugin.getInstance().getDataFolder(), "imports");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static final String SIGNATURE_PREFIX = "COS1:";

    public String toSignature(CosmeticTemplate template) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("name", template.getName());
        yaml.set("slot", template.getSlot().name());
        yaml.set("description", template.getDescription());
        yaml.set("author", template.getAuthor());
        List<java.util.Map<String, Object>> layers = new ArrayList<>();
        for (ParticleLayer layer : template.getLayers()) {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("particle", layer.getParticle());
            map.put("shape", layer.getShape().name());
            map.put("count", layer.getCount());
            map.put("speed", layer.getSpeed());
            map.put("offset-x", layer.getOffsetX());
            map.put("offset-y", layer.getOffsetY());
            map.put("offset-z", layer.getOffsetZ());
            map.put("y-offset", layer.getYOffset());
            map.put("tick-rate", layer.getTickRate());
            map.put("shape-radius", layer.getShapeRadius());
            map.put("shape-points", layer.getShapePoints());
            map.put("dust-color-r", layer.getDustColorR());
            map.put("dust-color-g", layer.getDustColorG());
            map.put("dust-color-b", layer.getDustColorB());
            map.put("dust-color-to-r", layer.getDustColorToR());
            map.put("dust-color-to-g", layer.getDustColorToG());
            map.put("dust-color-to-b", layer.getDustColorToB());
            map.put("dust-size", layer.getDustSize());
            layers.add(map);
        }
        yaml.set("layers", layers);
        byte[] raw = yaml.saveToString().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gz = new GZIPOutputStream(bos)) {
            gz.write(raw);
        }
        return SIGNATURE_PREFIX + Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    public CosmeticTemplate fromSignature(String signature) throws IOException {
        if (!signature.startsWith(SIGNATURE_PREFIX)) {
            throw new IOException("Invalid signature format");
        }
        byte[] compressed = Base64.getDecoder().decode(signature.substring(SIGNATURE_PREFIX.length()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            gz.transferTo(bos);
        }
        String yamlStr = bos.toString(StandardCharsets.UTF_8);
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(new StringReader(yamlStr));
        } catch (Exception e) {
            throw new IOException("Failed to parse template YAML", e);
        }
        return fromYaml(yaml);
    }

    private String getString(java.util.Map<?, ?> map, String key, String def) {
        Object val = map.get(key);
        return val != null ? val.toString() : def;
    }

    private int getInt(java.util.Map<?, ?> map, String key, int def) {
        Object val = map.get(key);
        if (val == null) return def;
        try { return Integer.parseInt(val.toString()); } catch (NumberFormatException e) { return def; }
    }

    private double getDouble(java.util.Map<?, ?> map, String key, double def) {
        Object val = map.get(key);
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (NumberFormatException e) { return def; }
    }
}
