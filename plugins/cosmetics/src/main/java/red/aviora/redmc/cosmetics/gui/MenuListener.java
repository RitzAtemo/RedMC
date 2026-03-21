package red.aviora.redmc.cosmetics.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import red.aviora.redmc.api.utils.ApiUtils;
import red.aviora.redmc.cosmetics.CosmeticsPlugin;
import red.aviora.redmc.cosmetics.model.CosmeticSlot;
import red.aviora.redmc.cosmetics.model.CosmeticTemplate;
import red.aviora.redmc.cosmetics.model.ParticleLayer;
import red.aviora.redmc.cosmetics.model.ParticleShape;
import red.aviora.redmc.cosmetics.model.PlayerCosmetics;

import java.util.Arrays;
import java.util.List;

public class MenuListener implements Listener {

    private static final List<Integer> SLOT_POSITIONS = Arrays.asList(10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23);
    private static final CosmeticSlot[] SLOT_ORDER = {
        CosmeticSlot.TRAIL, CosmeticSlot.HEAD, CosmeticSlot.BACK,
        CosmeticSlot.FEET, CosmeticSlot.ORBIT, CosmeticSlot.AURA,
        CosmeticSlot.WINGS, CosmeticSlot.CROWN, CosmeticSlot.HALO,
        CosmeticSlot.SHOULDER_LEFT, CosmeticSlot.SHOULDER_RIGHT
    };

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof MainMenu || holder instanceof SlotMenu
                || holder instanceof EquippedMenu || holder instanceof TemplateListMenu
                || holder instanceof TemplateMenu || holder instanceof EditMenu
                || holder instanceof LayerMenu || holder instanceof ParticleSelectMenu
                || holder instanceof ShapeSelectMenu || holder instanceof DeleteConfirmMenu
                || holder instanceof CreateSlotSelectMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getInventory())) {
            InventoryHolder h = event.getInventory().getHolder();
            if (isOurMenu(h)) event.setCancelled(true);
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof MainMenu menu) {
            event.setCancelled(true);
            handleMainMenu(player, event.getSlot());
        } else if (holder instanceof SlotMenu menu) {
            event.setCancelled(true);
            handleSlotMenu(player, menu, event.getSlot());
        } else if (holder instanceof EquippedMenu) {
            event.setCancelled(true);
            handleEquippedMenu(player, event.getSlot());
        } else if (holder instanceof TemplateListMenu menu) {
            event.setCancelled(true);
            handleTemplateListMenu(player, menu, event.getSlot());
        } else if (holder instanceof TemplateMenu menu) {
            event.setCancelled(true);
            handleTemplateMenu(player, menu, event.getSlot());
        } else if (holder instanceof EditMenu menu) {
            event.setCancelled(true);
            handleEditMenu(player, menu, event.getSlot(), event.isShiftClick());
        } else if (holder instanceof LayerMenu menu) {
            event.setCancelled(true);
            handleLayerMenu(player, menu, event.getSlot());
        } else if (holder instanceof ParticleSelectMenu menu) {
            event.setCancelled(true);
            handleParticleSelectMenu(player, menu, event.getSlot());
        } else if (holder instanceof ShapeSelectMenu menu) {
            event.setCancelled(true);
            handleShapeSelectMenu(player, menu, event.getSlot());
        } else if (holder instanceof DeleteConfirmMenu menu) {
            event.setCancelled(true);
            handleDeleteConfirmMenu(player, menu, event.getSlot());
        } else if (holder instanceof CreateSlotSelectMenu menu) {
            event.setCancelled(true);
            handleCreateSlotSelectMenu(player, menu, event.getSlot());
        }
    }

    private boolean isOurMenu(InventoryHolder h) {
        return h instanceof MainMenu || h instanceof SlotMenu || h instanceof EquippedMenu
            || h instanceof TemplateListMenu || h instanceof TemplateMenu || h instanceof EditMenu
            || h instanceof LayerMenu || h instanceof ParticleSelectMenu || h instanceof ShapeSelectMenu
            || h instanceof DeleteConfirmMenu || h instanceof CreateSlotSelectMenu;
    }

    private void handleMainMenu(Player player, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

        int slotIdx = SLOT_POSITIONS.indexOf(slot);
        if (slotIdx >= 0 && slotIdx < SLOT_ORDER.length) {
            player.openInventory(new SlotMenu(player, SLOT_ORDER[slotIdx], 0).getInventory());
            return;
        }

        if (slot == MainMenu.getTogglePosition()) {
            boolean nowVisible = plugin.getPlayerCosmeticsManager().toggleVisibility(player);
            String key = nowVisible ? "cosmetics.toggle-on" : "cosmetics.toggle-off";
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, key),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
            player.openInventory(new MainMenu(player).getInventory());
            return;
        }

        if (slot == MainMenu.getEquippedPosition()) {
            player.openInventory(new EquippedMenu(player).getInventory());
            return;
        }

        if (slot == MainMenu.getManagePosition()) {
            player.openInventory(new TemplateListMenu(player, 0).getInventory());
        }
    }

    private void handleSlotMenu(Player player, SlotMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        CosmeticSlot cosmeticSlot = menu.getSlot();

        if (slot == 45) {
            player.openInventory(new MainMenu(player).getInventory());
            return;
        }

        if (slot == 49) {
            PlayerCosmetics cosmetics = plugin.getPlayerCosmeticsManager().get(player);
            if (cosmetics != null && cosmetics.hasEquipped(cosmeticSlot)) {
                plugin.getPlayerCosmeticsManager().unequip(player, cosmeticSlot);
                ApiUtils.sendCommandSenderMessageArgs(player,
                    plugin.getLocaleManager().getMessage(player, "cosmetics.unequip-success"),
                    "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                    "%slot%", cosmeticSlot.name());
                player.openInventory(new SlotMenu(player, cosmeticSlot, menu.getPage()).getInventory());
            }
            return;
        }

        if (slot == 47) {
            player.openInventory(new SlotMenu(player, cosmeticSlot, menu.getPage() - 1).getInventory());
            return;
        }

        if (slot == 51) {
            player.openInventory(new SlotMenu(player, cosmeticSlot, menu.getPage() + 1).getInventory());
            return;
        }

        List<Integer> templatePositions = Arrays.asList(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        );
        int templateIdx = templatePositions.indexOf(slot);
        if (templateIdx < 0) return;

        List<CosmeticTemplate> templates = plugin.getTemplateManager().getForSlot(player.getUniqueId(), cosmeticSlot);
        int absoluteIdx = menu.getPage() * 28 + templateIdx;
        if (absoluteIdx >= templates.size()) return;

        CosmeticTemplate template = templates.get(absoluteIdx);
        PlayerCosmetics cosmetics = plugin.getPlayerCosmeticsManager().get(player);
        String currentlyEquipped = cosmetics != null ? cosmetics.getEquipped(cosmeticSlot) : null;

        if (template.getName().equalsIgnoreCase(currentlyEquipped)) {
            plugin.getPlayerCosmeticsManager().unequip(player, cosmeticSlot);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.unequip-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%slot%", cosmeticSlot.name());
        } else {
            plugin.getPlayerCosmeticsManager().equip(player, cosmeticSlot, template.getName());
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.equip-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%template%", template.getName(), "%slot%", cosmeticSlot.name());
        }

        player.openInventory(new SlotMenu(player, cosmeticSlot, menu.getPage()).getInventory());
    }

    private void handleEquippedMenu(Player player, int slot) {
        if (slot == 45) {
            player.openInventory(new MainMenu(player).getInventory());
            return;
        }

        int slotIdx = Arrays.asList(10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23).indexOf(slot);
        if (slotIdx >= 0 && slotIdx < SLOT_ORDER.length) {
            player.openInventory(new SlotMenu(player, SLOT_ORDER[slotIdx], 0).getInventory());
        }
    }

    private void handleTemplateListMenu(Player player, TemplateListMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

        if (slot == 45) {
            player.openInventory(new MainMenu(player).getInventory());
            return;
        }

        if (slot == 46) {
            player.openInventory(new TemplateListMenu(player, menu.getPage() - 1).getInventory());
            return;
        }

        if (slot == 52) {
            player.openInventory(new TemplateListMenu(player, menu.getPage() + 1).getInventory());
            return;
        }

        if (slot == 48 && player.hasPermission("redmc.cosmetics.create")) {
            player.closeInventory();
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-prompt-name"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
            ChatInputManager.expect(player.getUniqueId(), text -> {
                if (text.equalsIgnoreCase("cancel")) {
                    ApiUtils.sendCommandSenderMessageArgs(player,
                        plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-cancelled"),
                        "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
                    player.openInventory(new TemplateListMenu(player, 0).getInventory());
                    return;
                }
                String name = text.toLowerCase();
                if (!name.matches("[a-z0-9_]+")) {
                    ApiUtils.sendCommandSenderMessageArgs(player,
                        plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-invalid-name"),
                        "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
                    player.openInventory(new TemplateListMenu(player, 0).getInventory());
                    return;
                }
                if (plugin.getTemplateManager().exists(player.getUniqueId(), name)) {
                    ApiUtils.sendCommandSenderMessageArgs(player,
                        plugin.getLocaleManager().getMessage(player, "error.template-exists"),
                        "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                        "%name%", name);
                    player.openInventory(new TemplateListMenu(player, 0).getInventory());
                    return;
                }
                player.openInventory(new CreateSlotSelectMenu(player, name).getInventory());
            });
            return;
        }

        if (slot == 50 && player.hasPermission("redmc.cosmetics.import")) {
            player.closeInventory();
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-prompt-import"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
            ChatInputManager.expect(player.getUniqueId(), text -> {
                if (text.equalsIgnoreCase("cancel")) {
                    ApiUtils.sendCommandSenderMessageArgs(player,
                        plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-cancelled"),
                        "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
                    player.openInventory(new TemplateListMenu(player, 0).getInventory());
                    return;
                }
                try {
                    CosmeticTemplate imported = plugin.getTemplateManager().importFromSignature(player.getUniqueId(), text.trim());
                    if (imported == null) {
                        ApiUtils.sendCommandSenderMessageArgs(player,
                            plugin.getLocaleManager().getMessage(player, "error.import-failed"),
                            "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                            "%reason%", "null result");
                        player.openInventory(new TemplateListMenu(player, 0).getInventory());
                        return;
                    }
                    ApiUtils.sendCommandSenderMessageArgs(player,
                        plugin.getLocaleManager().getMessage(player, "cosmetics.import-success"),
                        "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                        "%name%", imported.getName());
                    player.openInventory(new TemplateMenu(player, imported.getName()).getInventory());
                } catch (Exception e) {
                    ApiUtils.sendCommandSenderMessageArgs(player,
                        plugin.getLocaleManager().getMessage(player, "error.import-failed"),
                        "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                        "%reason%", e.getMessage() != null ? e.getMessage() : "unknown");
                    player.openInventory(new TemplateListMenu(player, 0).getInventory());
                }
            });
            return;
        }

        int idx = -1;
        int[] tPositions = TemplateListMenu.TEMPLATE_POSITIONS;
        for (int i = 0; i < tPositions.length; i++) {
            if (tPositions[i] == slot) { idx = i; break; }
        }
        if (idx < 0) return;

        java.util.List<CosmeticTemplate> templates = new java.util.ArrayList<>(plugin.getTemplateManager().getAll(player.getUniqueId()));
        templates.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        int absoluteIdx = menu.getPage() * TemplateListMenu.PAGE_SIZE + idx;
        if (absoluteIdx >= templates.size()) return;

        player.openInventory(new TemplateMenu(player, templates.get(absoluteIdx).getName()).getInventory());
    }

    private void handleTemplateMenu(Player player, TemplateMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String templateName = menu.getTemplateName();

        if (slot == TemplateMenu.BACK_POSITION) {
            player.openInventory(new TemplateListMenu(player, 0).getInventory());
            return;
        }

        if (slot == TemplateMenu.EDIT_POSITION && player.hasPermission("redmc.cosmetics.edit")) {
            if (plugin.getTemplateManager().exists(player.getUniqueId(), templateName)) {
                player.openInventory(new EditMenu(player, templateName).getInventory());
            }
            return;
        }

        if (slot == TemplateMenu.DELETE_POSITION && player.hasPermission("redmc.cosmetics.delete")) {
            if (plugin.getTemplateManager().exists(player.getUniqueId(), templateName)) {
                player.openInventory(new DeleteConfirmMenu(player, templateName).getInventory());
            }
            return;
        }

        if (slot == TemplateMenu.EXPORT_POSITION && player.hasPermission("redmc.cosmetics.export")) {
            if (!plugin.getTemplateManager().exists(player.getUniqueId(), templateName)) return;
            try {
                String signature = plugin.getTemplateManager().exportToSignature(player.getUniqueId(), templateName);
                ApiUtils.sendCommandSenderMessageArgs(player,
                    plugin.getLocaleManager().getMessage(player, "cosmetics.export-success"),
                    "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                    "%name%", templateName);
                String buttonText = plugin.getLocaleManager().getMessage(player, "cosmetics.export-copy-button");
                Component button = ApiUtils.getMM().deserialize(buttonText)
                    .clickEvent(ClickEvent.copyToClipboard(signature));
                player.sendMessage(button);
            } catch (Exception e) {
                ApiUtils.sendCommandSenderMessageArgs(player,
                    plugin.getLocaleManager().getMessage(player, "error.export-failed"),
                    "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                    "%reason%", e.getMessage() != null ? e.getMessage() : "unknown");
            }
        }
    }

    private void handleEditMenu(Player player, EditMenu menu, int slot, boolean shiftClick) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String templateName = menu.getTemplateName();

        if (slot == EditMenu.BACK_POSITION) {
            player.openInventory(new TemplateMenu(player, templateName).getInventory());
            return;
        }

        if (slot == EditMenu.ADD_LAYER_POSITION) {
            player.openInventory(
                new ParticleSelectMenu(player, templateName, -1, ParticleSelectMenu.Mode.ADD_LAYER, 0).getInventory());
            return;
        }

        int[] positions = EditMenu.LAYER_POSITIONS;
        int layerIdx = -1;
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == slot) { layerIdx = i; break; }
        }
        if (layerIdx < 0) return;

        CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
        if (template == null || layerIdx >= template.getLayers().size()) return;

        if (shiftClick) {
            template.removeLayer(layerIdx);
            plugin.getTemplateManager().save(player.getUniqueId(), template);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.removelayer-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", templateName,
                "%index%", String.valueOf(layerIdx));
            player.openInventory(new EditMenu(player, templateName).getInventory());
        } else {
            player.openInventory(new LayerMenu(player, templateName, layerIdx).getInventory());
        }
    }

    private void handleLayerMenu(Player player, LayerMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String templateName = menu.getTemplateName();
        int layerIndex = menu.getLayerIndex();

        if (slot == LayerMenu.BACK_POSITION) {
            player.openInventory(new EditMenu(player, templateName).getInventory());
            return;
        }

        if (slot == LayerMenu.REMOVE_POSITION) {
            CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
            if (template != null) {
                template.removeLayer(layerIndex);
                plugin.getTemplateManager().save(player.getUniqueId(), template);
                ApiUtils.sendCommandSenderMessageArgs(player,
                    plugin.getLocaleManager().getMessage(player, "cosmetics.removelayer-success"),
                    "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                    "%name%", templateName,
                    "%index%", String.valueOf(layerIndex));
            }
            player.openInventory(new EditMenu(player, templateName).getInventory());
            return;
        }

        LayerMenu.Prop prop = LayerMenu.Prop.forSlot(slot);
        if (prop == null) return;

        CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
        if (template == null) return;
        ParticleLayer layer = template.getLayer(layerIndex);
        if (layer == null) return;

        if (prop == LayerMenu.Prop.PARTICLE) {
            player.openInventory(
                new ParticleSelectMenu(player, templateName, layerIndex, ParticleSelectMenu.Mode.SET_PARTICLE, 0)
                    .getInventory());
            return;
        }

        if (prop == LayerMenu.Prop.SHAPE) {
            player.openInventory(
                new ShapeSelectMenu(player, templateName, layerIndex, ShapeSelectMenu.Mode.SET_SHAPE, null)
                    .getInventory());
            return;
        }

        String labelKey = "cosmetics.gui.layer-label-" + prop.name().toLowerCase();
        String label = plugin.getLocaleManager().getMessage(player, labelKey);

        player.closeInventory();
        ApiUtils.sendCommandSenderMessageArgs(player,
            plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-prompt-value")
                .replace("%property%", label),
            "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));

        ChatInputManager.expect(player.getUniqueId(), text -> {
            if (text.equalsIgnoreCase("cancel")) {
                ApiUtils.sendCommandSenderMessageArgs(player,
                    plugin.getLocaleManager().getMessage(player, "cosmetics.gui.chat-cancelled"),
                    "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"));
                player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
                return;
            }

            CosmeticTemplate t = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
            if (t == null) return;
            ParticleLayer l = t.getLayer(layerIndex);
            if (l == null) return;

            String applied;
            try {
                applied = applyLayerProp(l, prop, text);
            } catch (IllegalArgumentException e) {
                ApiUtils.sendCommandSenderMessageArgs(player,
                    plugin.getLocaleManager().getMessage(player, "error.invalid-number"),
                    "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                    "%value%", text);
                player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
                return;
            }

            plugin.getTemplateManager().save(player.getUniqueId(), t);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.setlayer-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", templateName,
                "%index%", String.valueOf(layerIndex),
                "%property%", prop.name().toLowerCase(),
                "%value%", applied);
            player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
        });
    }

    private String applyLayerProp(ParticleLayer layer, LayerMenu.Prop prop, String text) {
        return switch (prop) {
            case COUNT -> {
                int v = parseInt(text);
                if (v < 1) v = 1;
                layer.setCount(v);
                yield String.valueOf(v);
            }
            case SPEED -> {
                double v = parseDouble(text);
                layer.setSpeed(v);
                yield String.valueOf(v);
            }
            case TICKRATE -> {
                int v = Math.max(1, parseInt(text));
                layer.setTickRate(v);
                yield String.valueOf(v);
            }
            case YOFFSET -> {
                double v = parseDouble(text);
                layer.setYOffset(v);
                yield String.valueOf(v);
            }
            case RADIUS -> {
                double v = parseDouble(text);
                layer.setShapeRadius(v);
                yield String.valueOf(v);
            }
            case POINTS -> {
                int v = Math.max(1, parseInt(text));
                layer.setShapePoints(v);
                yield String.valueOf(v);
            }
            case OFFSETX -> {
                double v = parseDouble(text);
                layer.setOffsetX(v);
                yield String.valueOf(v);
            }
            case OFFSETY -> {
                double v = parseDouble(text);
                layer.setOffsetY(v);
                yield String.valueOf(v);
            }
            case OFFSETZ -> {
                double v = parseDouble(text);
                layer.setOffsetZ(v);
                yield String.valueOf(v);
            }
            case COLOR -> {
                int[] rgb = parseRgb(text);
                layer.setDustColorR(rgb[0]);
                layer.setDustColorG(rgb[1]);
                layer.setDustColorB(rgb[2]);
                yield rgb[0] + " " + rgb[1] + " " + rgb[2];
            }
            case COLORTO -> {
                int[] rgb = parseRgb(text);
                layer.setDustColorToR(rgb[0]);
                layer.setDustColorToG(rgb[1]);
                layer.setDustColorToB(rgb[2]);
                yield rgb[0] + " " + rgb[1] + " " + rgb[2];
            }
            case DUSTSIZE -> {
                float v = (float) parseDouble(text);
                layer.setDustSize(v);
                yield String.valueOf(v);
            }
            default -> throw new IllegalArgumentException("unhandled prop");
        };
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(s); }
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(s); }
    }

    private int[] parseRgb(String s) {
        String[] parts = s.trim().split("[\\s,]+");
        if (parts.length != 3) throw new IllegalArgumentException(s);
        int r = Integer.parseInt(parts[0]);
        int g = Integer.parseInt(parts[1]);
        int b = Integer.parseInt(parts[2]);
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
            throw new IllegalArgumentException(s);
        return new int[]{r, g, b};
    }

    private void handleParticleSelectMenu(Player player, ParticleSelectMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String templateName = menu.getTemplateName();
        int layerIndex = menu.getLayerIndex();

        if (slot == ParticleSelectMenu.BACK_POSITION) {
            if (menu.getMode() == ParticleSelectMenu.Mode.ADD_LAYER) {
                player.openInventory(new EditMenu(player, templateName).getInventory());
            } else {
                player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
            }
            return;
        }

        if (slot == ParticleSelectMenu.PREV_POSITION) {
            player.openInventory(
                new ParticleSelectMenu(player, templateName, layerIndex, menu.getMode(), menu.getPage() - 1)
                    .getInventory());
            return;
        }

        if (slot == ParticleSelectMenu.NEXT_POSITION) {
            player.openInventory(
                new ParticleSelectMenu(player, templateName, layerIndex, menu.getMode(), menu.getPage() + 1)
                    .getInventory());
            return;
        }

        if (slot >= ParticleSelectMenu.PAGE_SIZE) return;

        int particleIdx = menu.getPage() * ParticleSelectMenu.PAGE_SIZE + slot;
        if (particleIdx >= ParticleSelectMenu.PARTICLES.size()) return;

        Particle selected = ParticleSelectMenu.PARTICLES.get(particleIdx);

        if (menu.getMode() == ParticleSelectMenu.Mode.ADD_LAYER) {
            player.openInventory(
                new ShapeSelectMenu(player, templateName, -1, ShapeSelectMenu.Mode.ADD_LAYER, selected.name())
                    .getInventory());
        } else {
            CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
            if (template == null) return;
            ParticleLayer layer = template.getLayer(layerIndex);
            if (layer == null) return;
            layer.setParticle(selected.name());
            plugin.getTemplateManager().save(player.getUniqueId(), template);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.setlayer-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", templateName,
                "%index%", String.valueOf(layerIndex),
                "%property%", "particle",
                "%value%", selected.name());
            player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
        }
    }

    private void handleShapeSelectMenu(Player player, ShapeSelectMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String templateName = menu.getTemplateName();
        int layerIndex = menu.getLayerIndex();

        if (slot == ShapeSelectMenu.BACK_POSITION) {
            if (menu.getMode() == ShapeSelectMenu.Mode.ADD_LAYER) {
                player.openInventory(
                    new ParticleSelectMenu(player, templateName, -1, ParticleSelectMenu.Mode.ADD_LAYER, 0)
                        .getInventory());
            } else {
                player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
            }
            return;
        }

        int shapeIdx = -1;
        for (int i = 0; i < ShapeSelectMenu.SHAPE_POSITIONS.length; i++) {
            if (ShapeSelectMenu.SHAPE_POSITIONS[i] == slot) { shapeIdx = i; break; }
        }
        if (shapeIdx < 0 || shapeIdx >= ShapeSelectMenu.SHAPES.length) return;

        ParticleShape selectedShape = ShapeSelectMenu.SHAPES[shapeIdx];

        if (menu.getMode() == ShapeSelectMenu.Mode.ADD_LAYER) {
            CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
            if (template == null) return;
            ParticleLayer layer = new ParticleLayer();
            layer.setParticle(menu.getPendingParticle());
            layer.setShape(selectedShape);
            template.addLayer(layer);
            plugin.getTemplateManager().save(player.getUniqueId(), template);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.addlayer-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", templateName,
                "%count%", String.valueOf(template.getLayers().size()));
            player.openInventory(new EditMenu(player, templateName).getInventory());
        } else {
            CosmeticTemplate template = plugin.getTemplateManager().get(player.getUniqueId(), templateName);
            if (template == null) return;
            ParticleLayer layer = template.getLayer(layerIndex);
            if (layer == null) return;
            layer.setShape(selectedShape);
            plugin.getTemplateManager().save(player.getUniqueId(), template);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.setlayer-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", templateName,
                "%index%", String.valueOf(layerIndex),
                "%property%", "shape",
                "%value%", selectedShape.name());
            player.openInventory(new LayerMenu(player, templateName, layerIndex).getInventory());
        }
    }

    private void handleDeleteConfirmMenu(Player player, DeleteConfirmMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String templateName = menu.getTemplateName();

        if (slot == DeleteConfirmMenu.CONFIRM_POSITION) {
            plugin.getTemplateManager().delete(player.getUniqueId(), templateName);
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "cosmetics.delete-success"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", templateName);
            player.openInventory(new TemplateListMenu(player, 0).getInventory());
            return;
        }

        if (slot == DeleteConfirmMenu.CANCEL_POSITION) {
            if (plugin.getTemplateManager().exists(player.getUniqueId(), templateName)) {
                player.openInventory(new TemplateMenu(player, templateName).getInventory());
            } else {
                player.openInventory(new TemplateListMenu(player, 0).getInventory());
            }
        }
    }

    private void handleCreateSlotSelectMenu(Player player, CreateSlotSelectMenu menu, int slot) {
        CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
        String pendingName = menu.getPendingName();

        if (slot == 45) {
            player.openInventory(new TemplateListMenu(player, 0).getInventory());
            return;
        }

        int[] positions = CreateSlotSelectMenu.SLOT_POSITIONS;
        int slotIdx = -1;
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == slot) { slotIdx = i; break; }
        }
        if (slotIdx < 0) return;

        CosmeticSlot cosmeticSlot = CreateSlotSelectMenu.SLOT_ORDER[slotIdx];

        if (plugin.getTemplateManager().exists(player.getUniqueId(), pendingName)) {
            ApiUtils.sendCommandSenderMessageArgs(player,
                plugin.getLocaleManager().getMessage(player, "error.template-exists"),
                "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
                "%name%", pendingName);
            player.openInventory(new TemplateListMenu(player, 0).getInventory());
            return;
        }

        CosmeticTemplate template = new CosmeticTemplate(pendingName, cosmeticSlot);
        template.setAuthor(player.getName());
        plugin.getTemplateManager().save(player.getUniqueId(), template);
        ApiUtils.sendCommandSenderMessageArgs(player,
            plugin.getLocaleManager().getMessage(player, "cosmetics.create-success"),
            "%prefix%", plugin.getLocaleManager().getMessage(player, "prefix"),
            "%name%", pendingName,
            "%slot%", cosmeticSlot.name());
        player.openInventory(new TemplateMenu(player, pendingName).getInventory());
    }
}
