# Cosmetics Plugin

Layered particle cosmetics system with 11 placement slots, 10 shape patterns, full Bukkit `Particle` enum support, and template-based presets with import/export.

## Command Tree

```
/cosmetics  (alias: /cos)
├── menu
├── equip <slot> <template>
├── unequip <slot|all>
├── list [slot]
├── equipped
├── toggle
├── create <name> <slot>
├── delete <name>
├── info <name>
├── edit <name>
│   ├── addlayer <particle> <shape>
│   ├── removelayer <index>
│   └── setlayer <index>
│       ├── particle <type>
│       ├── shape <type>
│       ├── count <n>
│       ├── speed <n>
│       ├── yoffset <n>
│       ├── tickrate <n>
│       ├── radius <n>
│       ├── points <n>
│       ├── offsetx <n>
│       ├── offsety <n>
│       ├── offsetz <n>
│       ├── color <r> <g> <b>
│       ├── colorto <r> <g> <b>
│       └── dustsize <n>
├── export <name>
├── import <signature>
├── admin
│   ├── give <player> <slot> <template>
│   └── reset <player>
└── reload
    ├── config
    ├── data
    └── all
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.cosmetics` | true |
| `redmc.cosmetics.menu` | true |
| `redmc.cosmetics.equip` | true |
| `redmc.cosmetics.toggle` | true |
| `redmc.cosmetics.list` | true |
| `redmc.cosmetics.create` | op |
| `redmc.cosmetics.delete` | op |
| `redmc.cosmetics.edit` | op |
| `redmc.cosmetics.export` | op |
| `redmc.cosmetics.import` | op |
| `redmc.cosmetics.admin` | op |
| `redmc.cosmetics.reload` | op |
| `redmc.cosmetics.reload.config` | op |
| `redmc.cosmetics.reload.data` | op |
| `redmc.cosmetics.reload.all` | op |

## Slots

Each player can equip one template per slot independently. Slots define **where** particles are anchored relative to the player's body.

| Slot | Position | Rotates with player |
|---|---|---|
| `TRAIL` | Behind the player along their path | No |
| `HEAD` | Above the player's head | No |
| `BACK` | Behind the player's torso | Yes |
| `FEET` | At the player's feet | No |
| `ORBIT` | Centered on the player's body | No |
| `AURA` | Centered on the player's body (full sphere) | No |
| `WINGS` | Behind the player's shoulders | Yes |
| `CROWN` | On top of the player's head | No |
| `HALO` | Floating above the player's head | No |
| `SHOULDER_LEFT` | Left shoulder | Yes |
| `SHOULDER_RIGHT` | Right shoulder | Yes |

Slots marked **Rotates with player** apply a Y-axis rotation equal to the player's yaw to all shape vectors, so the effect stays oriented relative to the direction the player is facing.

## Shapes

Shapes define the spatial arrangement of spawn points for each layer. All shapes are generated as a list of relative `Vector` offsets from the slot anchor point.

| Shape | Description | Relevant params |
|---|---|---|
| `POINT` | Single point at the anchor | `y-offset` |
| `RING` | Flat horizontal circle | `shape-radius`, `shape-points`, `y-offset` |
| `SPHERE` | Fibonacci-distributed sphere | `shape-radius`, `shape-points`, `y-offset` |
| `SPIRAL` | Ascending single helix (2 full turns) | `shape-radius`, `shape-points`, `y-offset` |
| `DOUBLE_HELIX` | Two interleaved ascending helices | `shape-radius`, `shape-points`, `y-offset` |
| `STAR` | 5-pointed star polygon | `shape-radius`, `y-offset` |
| `WINGS_SHAPE` | Symmetric curved wings spanning left/right | `shape-radius` (span), `shape-points`, `y-offset` |
| `CROWN_SHAPE` | Base ring with 5 upward spikes | `shape-radius`, `y-offset` |
| `HALO_SHAPE` | Double-ring halo (outer + inner offset ring) | `shape-radius`, `shape-points`, `y-offset` |
| `RANDOM` | Random points in a sphere | `shape-radius`, `shape-points`, `y-offset` |

## Template Format

Templates are stored as YAML files in `plugins/RedMC-Cosmetics/templates/`. Each file defines one template.

```yaml
name: my_template         # unique identifier (lowercase)
slot: ORBIT               # CosmeticSlot enum value
description: "My effect"
author: Steve

layers:
  - particle: DUST                 # Bukkit Particle enum name
    shape: RING
    count: 1                       # particles per spawn point
    speed: 0.0                     # particle speed (spread for non-directional)
    offset-x: 0.0                  # random offset applied after spawn point
    offset-y: 0.0
    offset-z: 0.0
    y-offset: 1.0                  # vertical shift of the entire shape
    tick-rate: 1                   # spawn every N renderer ticks (50 ms each)
    shape-radius: 1.2              # radius / span of the shape
    shape-points: 24               # number of points to generate
    dust-color-r: 255              # DUST / DUST_COLOR_TRANSITION from-color
    dust-color-g: 100
    dust-color-b: 0
    dust-color-to-r: 0             # DUST_COLOR_TRANSITION to-color only
    dust-color-to-g: 200
    dust-color-to-b: 255
    dust-size: 1.2                 # dust particle size
```

### Particle Types

Any value from Bukkit's `Particle` enum is valid. Three categories have special handling:

| Category | Particle | Extra data required |
|---|---|---|
| Colored dust | `DUST` | `dust-color-r/g/b`, `dust-size` |
| Gradient dust | `DUST_COLOR_TRANSITION` | all dust fields including `dust-color-to-*` |
| Standard | everything else | none — `count`, `speed`, `offset-*` only |

Useful particles for cosmetics: `FLAME`, `SOUL_FIRE_FLAME`, `END_ROD`, `ENCHANT`, `ENCHANTED_HIT`, `PORTAL`, `SNOWFLAKE`, `HAPPY_VILLAGER`, `HEART`, `NOTE`, `TOTEM_OF_UNDYING`, `DRAGON_BREATH`, `WITCH`, `SOUL`, `GLOW`, `CHERRY_LEAVES`, `SPORE_BLOSSOM_AIR`, `LARGE_SMOKE`, `LAVA`.

## Layer System

A template contains an ordered list of layers. Each layer is rendered independently every `tick-rate` ticks. Layers stack — you can mix particle types, shapes, and tick rates in one template to build complex multi-layer effects.

Example: `angel_wings` uses two layers:
1. `END_ROD` on `WINGS_SHAPE` every tick — the bright white outline
2. `DUST` (white, `#F0F8FF`) on `WINGS_SHAPE` every 2 ticks — soft fill dust

## Premade Templates

| Name | Slot | Effect |
|---|---|---|
| `fire_trail` | TRAIL | Flame + smoke trail |
| `frost_trail` | TRAIL | Snowflakes + cyan dust |
| `void_trail` | TRAIL | Portal particles + dark purple dust |
| `portal_orbit` | ORBIT | Portal particle ring + enchantment ring |
| `enchant_aura` | AURA | Enchantment sphere + random enchanted hits |
| `spiral_orbit` | ORBIT | Blue→pink `DUST_COLOR_TRANSITION` double helix |
| `angel_wings` | WINGS | `END_ROD` + white dust wings |
| `devil_wings` | WINGS | `FLAME` + red dust wings |
| `golden_crown` | CROWN | Gold dust crown with enchantment sparks |
| `soul_halo` | HALO | `SOUL_FIRE_FLAME` + `SOUL` halo ring |
| `rainbow_feet` | FEET | Pink→blue gradient dust ring at feet |
| `nature_head` | HEAD | Happy villager ring + spore blossom sphere |
| `smoke_back` | BACK | Large smoke + dark dust rising from back |

## Equipping & Visibility

Players equip templates per-slot:
```
/cos equip trail fire_trail
/cos equip wings angel_wings
/cos equip halo soul_halo
```

`/cos toggle` hides all active cosmetics without unequipping them (state is saved). Useful for PvP or performance-sensitive situations.

`/cos equipped` lists all currently worn templates per slot.

Equipped state and visibility are persisted to `plugins/RedMC-Cosmetics/playerdata/<uuid>.yml`.

## Editing Templates

Create a new template, then add layers one by one:

```
/cos create my_orbit ORBIT
/cos edit my_orbit addlayer DUST RING
/cos edit my_orbit setlayer 0 radius 1.2
/cos edit my_orbit setlayer 0 points 24
/cos edit my_orbit setlayer 0 color 30 144 255
/cos edit my_orbit setlayer 0 dustsize 1.1
/cos edit my_orbit setlayer 0 yoffset 1.0
/cos edit my_orbit setlayer 0 tickrate 1
```

`/cos info <name>` shows all layers and their key properties.

Layer indices are 0-based. `removelayer <index>` removes by index; indices above it shift down.

## Import / Export

Templates are transferred via an encrypted **signature string** — no file access required.

**Export** serializes the template to a GZIP-compressed Base64 string and prints it to chat with a clickable **Copy signature** button:
```
/cos export my_orbit
```
Click the button in chat to copy the signature to your clipboard. Signatures start with `COS1:`.

**Import** registers a template from a signature pasted directly into the command:
```
/cos import COS1:H4sIAAAAA...
```
The template name is taken from the signature's embedded YAML. If a template with that name already exists it will be overwritten.

## Configuration

| Key | Default | Description |
|---|---|---|
| `renderer.tick-interval-ms` | `50` | Renderer tick rate in milliseconds |
| `renderer.trail-history-size` | `12` | Number of trail positions to keep |
| `renderer.trail-min-distance` | `0.25` | Minimum movement in blocks before recording a trail point |
| `slots.<name>.enabled` | `true` | Enable or disable a slot |
| `slots.head.y-offset` | `0.4` | Upward offset in blocks from eye level |
| `slots.back.distance` | `0.5` | Distance in blocks behind the player |
| `slots.orbit.y-offset` | `1.0` | Vertical center offset in blocks |
| `slots.aura.y-offset` | `1.0` | Vertical center offset in blocks |
| `slots.wings.y-offset` | `0.8` | Vertical center offset in blocks |
| `slots.crown.y-offset` | `0.3` | Vertical offset above head |
| `slots.halo.y-offset` | `0.7` | Vertical offset above head |
| `slots.shoulder-left.y-offset` | `0.3` | Vertical offset from player center |
| `slots.shoulder-left.side-offset` | `0.45` | Horizontal distance from player center |
| `slots.shoulder-right.y-offset` | `0.3` | Vertical offset from player center |
| `slots.shoulder-right.side-offset` | `0.45` | Horizontal distance from player center |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Cosmetics<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `error.template-not-found` | `%prefix%<#FF6B6B>Template <#F0F8FF>%name%<#FF6B6B> not found.` |
| `error.template-exists` | `%prefix%<#FF6B6B>Template <#F0F8FF>%name%<#FF6B6B> already exists.` |
| `error.invalid-slot` | `%prefix%<#FF6B6B>Invalid slot. Valid slots: %slots%` |
| `error.invalid-particle` | `%prefix%<#FF6B6B>Invalid particle type.` |
| `error.invalid-shape` | `%prefix%<#FF6B6B>Invalid shape. Valid shapes: %shapes%` |
| `error.invalid-number` | `%prefix%<#FF6B6B>Invalid number: <#F0F8FF>%value%` |
| `error.invalid-color` | `%prefix%<#FF6B6B>Color values must be 0–255.` |
| `error.layer-not-found` | `%prefix%<#FF6B6B>Layer <#F0F8FF>%index%<#FF6B6B> not found (template has %count% layers).` |
| `error.not-equipped` | `%prefix%<#FF6B6B>Nothing equipped in slot <#F0F8FF>%slot%<#FF6B6B>.` |
| `error.export-failed` | `%prefix%<#FF6B6B>Export failed: %reason%` |
| `error.import-failed` | `%prefix%<#FF6B6B>Import failed: %reason%` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player <#F0F8FF>%name%<#FF6B6B> not found.` |
| `cosmetics.equip-success` | `%prefix%Equipped <#C780FA>%template%<#F0F8FF> to slot <#1E90FF>%slot%<#F0F8FF>.` |
| `cosmetics.unequip-success` | `%prefix%Unequipped slot <#1E90FF>%slot%<#F0F8FF>.` |
| `cosmetics.unequip-all-success` | `%prefix%Unequipped all cosmetics.` |
| `cosmetics.toggle-on` | `%prefix%<#3DDC97>Cosmetics visible.` |
| `cosmetics.toggle-off` | `%prefix%<#9b94a6>Cosmetics hidden.` |
| `cosmetics.create-success` | `%prefix%Created template <#C780FA>%name%<#F0F8FF> for slot <#1E90FF>%slot%<#F0F8FF>.` |
| `cosmetics.delete-success` | `%prefix%Deleted template <#C780FA>%name%<#F0F8FF>.` |
| `cosmetics.addlayer-success` | `%prefix%Added layer to <#C780FA>%name%<#F0F8FF>. Now has %count% layers.` |
| `cosmetics.removelayer-success` | `%prefix%Removed layer %index% from <#C780FA>%name%<#F0F8FF>.` |
| `cosmetics.setlayer-success` | `%prefix%Updated layer %index% of <#C780FA>%name%<#F0F8FF>: <#9b94a6>%property%<#F0F8FF> → <#3DDC97>%value%<#F0F8FF>.` |
| `cosmetics.export-success` | `%prefix%Template <#C780FA>%name%<#F0F8FF> exported — copy signature below:` |
| `cosmetics.import-success` | `%prefix%Imported template <#C780FA>%name%<#F0F8FF>.` |
| `cosmetics.admin-give-success` | `%prefix%Equipped <#C780FA>%template%<#F0F8FF> to <#1E90FF>%player%<#F0F8FF> slot <#1E90FF>%slot%<#F0F8FF>.` |
| `cosmetics.admin-reset-success` | `%prefix%Reset all cosmetics for <#1E90FF>%player%<#F0F8FF>.` |
| `cosmetics.list-header` | `%prefix%Templates%filter%:` |
| `cosmetics.list-filter-slot` | ` for slot <#1E90FF>%slot%` |
| `cosmetics.list-entry` | `  <#C780FA>%name% <#9b94a6>[<#1E90FF>%slot%<#9b94a6>] <#888888>%description%` |
| `cosmetics.list-empty` | `  <#9b94a6>No templates found.` |
| `cosmetics.equipped-header` | `%prefix%Your equipped cosmetics:` |
| `cosmetics.equipped-entry` | `  <#1E90FF>%slot%<#9b94a6>: <#C780FA>%template%` |
| `cosmetics.equipped-none` | `  <#9b94a6>Nothing equipped.` |
| `cosmetics.template-info-header` | `%prefix%Template <#C780FA>%name%<#F0F8FF> [slot: <#1E90FF>%slot%<#F0F8FF>]:` |
| `cosmetics.template-info-layer` | `  <#9b94a6>Layer %index%<#F0F8FF>: <#3DDC97>%particle% <#9b94a6>shape=<#F0F8FF>%shape% ...` |
| `cosmetics.template-info-no-layers` | `  <#9b94a6>No layers defined.` |
| `cosmetics.slot.trail` | `Trail` |
| `cosmetics.slot.head` | `Head` |
| `cosmetics.slot.back` | `Back` |
| `cosmetics.slot.feet` | `Feet` |
| `cosmetics.slot.orbit` | `Orbit` |
| `cosmetics.slot.aura` | `Aura` |
| `cosmetics.slot.wings` | `Wings` |
| `cosmetics.slot.crown` | `Crown` |
| `cosmetics.slot.halo` | `Halo` |
| `cosmetics.slot.shoulder_left` | `Left Shoulder` |
| `cosmetics.slot.shoulder_right` | `Right Shoulder` |
| `cosmetics.gui.main-title` | `<#C780FA>✦ Cosmetics` |
| `cosmetics.gui.slot-title` | `<#C780FA>✦ %slot%` |
| `cosmetics.gui.slot-name-equipped` | `<#C780FA>%slot%` |
| `cosmetics.gui.slot-name-empty` | `<#9b94a6>%slot%` |
| `cosmetics.gui.slot-lore-equipped` | `<#3DDC97>✔ Equipped: <#C780FA>%template%` |
| `cosmetics.gui.slot-lore-empty` | `<#9b94a6>Empty` |
| `cosmetics.gui.slot-lore-click` | `<#888888>Click to browse templates` |
| `cosmetics.gui.toggle-on-name` | `<#3DDC97>✔ Cosmetics: ON` |
| `cosmetics.gui.toggle-off-name` | `<#9b94a6>✗ Cosmetics: OFF` |
| `cosmetics.gui.toggle-lore` | `<#888888>Click to toggle visibility` |
| `cosmetics.gui.back-name` | `<#9b94a6>← Back` |
| `cosmetics.gui.back-lore` | `<#888888>Return to main menu` |
| `cosmetics.gui.unequip-name` | `<#FF6B6B>✗ Unequip` |
| `cosmetics.gui.unequip-lore` | `<#888888>Remove from this slot` |
| `cosmetics.gui.prev-page-name` | `<#9b94a6>← Previous` |
| `cosmetics.gui.next-page-name` | `<#9b94a6>Next →` |
| `cosmetics.gui.page-lore` | `<#888888>Page %page% of %total%` |
| `cosmetics.gui.template-name` | `<#F0F8FF>%name%` |
| `cosmetics.gui.template-name-equipped` | `<#C780FA>%name%` |
| `cosmetics.gui.template-lore-description` | `<#9b94a6>%description%` |
| `cosmetics.gui.template-lore-layers` | `<#9b94a6>Layers: <#F0F8FF>%count%` |
| `cosmetics.gui.template-lore-author` | `<#888888>Author: %author%` |
| `cosmetics.gui.template-lore-equipped` | `<#3DDC97>✔ Currently equipped` |
| `cosmetics.gui.template-lore-click-equip` | `<#888888>Click to equip` |
| `cosmetics.gui.template-lore-click-unequip` | `<#888888>Click to unequip` |
| `cosmetics.gui.equipped-button-name` | `<#3DDC97>★ Equipped` |
| `cosmetics.gui.equipped-button-lore` | `<#888888>View your equipped cosmetics` |
| `cosmetics.gui.manage-button-name` | `<#1E90FF>⚙ Manage Templates` |
| `cosmetics.gui.manage-button-lore` | `<#888888>Create, edit and delete templates` |
| `cosmetics.gui.equipped-title` | `<#C780FA>✦ Equipped Cosmetics` |
| `cosmetics.gui.equipped-slot-equipped-name` | `<#C780FA>%slot%` |
| `cosmetics.gui.equipped-slot-equipped-lore` | `<#3DDC97>✔ <#C780FA>%template%` |
| `cosmetics.gui.equipped-slot-empty-name` | `<#9b94a6>%slot%` |
| `cosmetics.gui.equipped-slot-empty-lore` | `<#9b94a6>Empty` |
| `cosmetics.gui.equipped-slot-click` | `<#888888>Click to manage this slot` |
| `cosmetics.gui.template-list-title` | `<#C780FA>✦ Manage Templates` |
| `cosmetics.gui.template-list-create-name` | `<#3DDC97>+ Create Template` |
| `cosmetics.gui.template-list-create-lore` | `<#888888>Create a new template` |
| `cosmetics.gui.template-list-import-name` | `<#FFB800>⬡ Import` |
| `cosmetics.gui.template-list-import-lore` | `<#888888>Import from a signature` |
| `cosmetics.gui.template-list-click` | `<#888888>Click to manage` |
| `cosmetics.gui.template-menu-title` | `<#C780FA>✦ %name%` |
| `cosmetics.gui.template-menu-info-name` | `<#F0F8FF>ℹ Template Info` |
| `cosmetics.gui.template-menu-info-lore-slot` | `<#9b94a6>Slot: <#1E90FF>%slot%` |
| `cosmetics.gui.template-menu-info-lore-author` | `<#9b94a6>Author: <#888888>%author%` |
| `cosmetics.gui.template-menu-info-lore-layers` | `<#9b94a6>Layers: <#F0F8FF>%count%` |
| `cosmetics.gui.template-menu-edit-name` | `<#FFB800>✎ Edit Layers` |
| `cosmetics.gui.template-menu-edit-lore` | `<#888888>Modify particle layers` |
| `cosmetics.gui.template-menu-delete-name` | `<#FF6B6B>✗ Delete` |
| `cosmetics.gui.template-menu-delete-lore` | `<#888888>Permanently delete` |
| `cosmetics.gui.template-menu-export-name` | `<#3DDC97>⬡ Export` |
| `cosmetics.gui.template-menu-export-lore` | `<#888888>Copy shareable signature` |
| `cosmetics.gui.edit-title` | `<#C780FA>✦ Edit: %name%` |
| `cosmetics.gui.edit-add-layer-name` | `<#3DDC97>+ Add Layer` |
| `cosmetics.gui.edit-add-layer-lore` | `<#888888>Select particle and shape` |
| `cosmetics.gui.edit-layer-name` | `<#F0F8FF>Layer %index%` |
| `cosmetics.gui.edit-layer-lore-particle` | `<#9b94a6>Particle: <#3DDC97>%particle%` |
| `cosmetics.gui.edit-layer-lore-shape` | `<#9b94a6>Shape: <#F0F8FF>%shape%` |
| `cosmetics.gui.edit-layer-lore-count` | `<#9b94a6>Count: <#F0F8FF>%count%` |
| `cosmetics.gui.edit-layer-click` | `<#888888>Click to edit  \| Shift-click to remove` |
| `cosmetics.gui.layer-title` | `<#C780FA>✦ Layer %index%: %name%` |
| `cosmetics.gui.layer-prop-name` | `<#F0F8FF>%label%` |
| `cosmetics.gui.layer-prop-lore-value` | `<#9b94a6>Value: <#F0F8FF>%value%` |
| `cosmetics.gui.layer-prop-lore-click` | `<#888888>Click to change` |
| `cosmetics.gui.layer-prop-lore-select` | `<#888888>Click to select` |
| `cosmetics.gui.layer-remove-name` | `<#FF6B6B>✗ Remove Layer` |
| `cosmetics.gui.layer-remove-lore` | `<#888888>Remove this layer` |
| `cosmetics.gui.layer-label-particle` | `Particle` |
| `cosmetics.gui.layer-label-shape` | `Shape` |
| `cosmetics.gui.layer-label-count` | `Count` |
| `cosmetics.gui.layer-label-speed` | `Speed` |
| `cosmetics.gui.layer-label-tickrate` | `Tick Rate` |
| `cosmetics.gui.layer-label-radius` | `Radius` |
| `cosmetics.gui.layer-label-points` | `Points` |
| `cosmetics.gui.layer-label-yoffset` | `Y Offset` |
| `cosmetics.gui.layer-label-offsetx` | `Offset X` |
| `cosmetics.gui.layer-label-offsety` | `Offset Y` |
| `cosmetics.gui.layer-label-offsetz` | `Offset Z` |
| `cosmetics.gui.layer-label-color` | `Color (from)` |
| `cosmetics.gui.layer-label-colorto` | `Color (to)` |
| `cosmetics.gui.layer-label-dustsize` | `Dust Size` |
| `cosmetics.gui.particle-select-title` | `<#C780FA>✦ Select Particle` |
| `cosmetics.gui.shape-select-title` | `<#C780FA>✦ Select Shape` |
| `cosmetics.gui.select-item-name` | `<#3DDC97>%value%` |
| `cosmetics.gui.select-item-lore` | `<#888888>Click to select` |
| `cosmetics.gui.delete-confirm-title` | `<#FF6B6B>Delete: %name%?` |
| `cosmetics.gui.delete-confirm-yes-name` | `<#FF6B6B>✗ Confirm Delete` |
| `cosmetics.gui.delete-confirm-yes-lore` | `<#FF6B6B>This cannot be undone!` |
| `cosmetics.gui.delete-confirm-no-name` | `<#3DDC97>✔ Cancel` |
| `cosmetics.gui.delete-confirm-no-lore` | `<#888888>Keep this template` |
| `cosmetics.gui.create-slot-title` | `<#C780FA>✦ Create — Select Slot` |
| `cosmetics.gui.create-slot-item-name` | `<#F0F8FF>%slot%` |
| `cosmetics.gui.create-slot-item-lore` | `<#888888>Click to use this slot` |
| `cosmetics.gui.chat-prompt-name` | `%prefix%<#9b94a6>Type the template name in chat (or type cancel):` |
| `cosmetics.gui.chat-prompt-value` | `%prefix%<#9b94a6>Type new value for <#F0F8FF>%property%<#9b94a6> (or type cancel):` |
| `cosmetics.gui.chat-prompt-import` | `%prefix%<#9b94a6>Paste the import signature in chat (or type cancel):` |
| `cosmetics.gui.chat-cancelled` | `%prefix%<#9b94a6>Input cancelled.` |
| `cosmetics.gui.chat-invalid-name` | `%prefix%<#FF6B6B>Name must be alphanumeric (a-z, 0-9, _).` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
