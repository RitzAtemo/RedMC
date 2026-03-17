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

Trail tracks the last N positions from `PlayerMoveEvent` (configurable via `renderer.trail-history-size`). New positions are only recorded if the player has moved at least `renderer.trail-min-distance` blocks from the previous recorded point.

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

All built-in templates are extracted from the JAR into `plugins/RedMC-Cosmetics/templates/` on first startup. They can be edited like any user template.

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

## Renderer

The renderer runs as an async fixed-rate task via `AsyncScheduler` (default 50 ms per tick). For each online player with cosmetics:

1. The async tick iterates equipped slots
2. Per player, a task is dispatched via `player.getScheduler().run()` to run on the correct Folia region thread
3. For each layer, shape vectors are computed, optionally rotated by player yaw, and translated to world coordinates
4. `World.spawnParticle()` is called with `force=true` to bypass visibility distance limits

Trail positions are collected synchronously in `PlayerMoveListener` (on the correct region thread) and stored in `TrailTracker` per player UUID.

## Config

```yaml
config-version: "0.0.1-alpha"

renderer:
  tick-interval-ms: 50           # renderer tick rate
  trail-history-size: 12         # how many trail positions to keep
  trail-min-distance: 0.25       # minimum movement before recording a trail point

slots:
  trail:
    enabled: true
  head:
    enabled: true
    y-offset: 0.4                # upward offset from eye level
  back:
    enabled: true
    distance: 0.5                # distance behind the player
  feet:
    enabled: true
  orbit:
    enabled: true
    y-offset: 1.0
  aura:
    enabled: true
    y-offset: 1.0
  wings:
    enabled: true
    y-offset: 0.8
  crown:
    enabled: true
    y-offset: 0.3
  halo:
    enabled: true
    y-offset: 0.7
  shoulder-left:
    enabled: true
    y-offset: 0.3
    side-offset: 0.45            # horizontal distance from player center
  shoulder-right:
    enabled: true
    y-offset: 0.3
    side-offset: 0.45
```
