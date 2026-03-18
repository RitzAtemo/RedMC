# NPC Plugin

Packet-based NPC rendering using NMS. NPCs are fake players rendered via Brigadier commands and stored in YAML.

## Command Tree

```
/npc
├── create <id>
├── read
├── delete <id>
├── reload
│   ├── config
│   ├── data
│   └── all
└── update <id>
    ├── name <name>
    ├── skin <player>
    ├── teleport <player>
    ├── equipment <slot> <item>
    └── commands
        ├── leftclick
        │   ├── add <type> <command>
        │   └── clear
        └── rightclick
            ├── add <type> <command>
            └── clear
```

Equipment slots: `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`

Command types: `console` (dispatched as console), `player` (executed as the clicking player)

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.npc` | op |
| `redmc.npc.create` | op |
| `redmc.npc.read` | op |
| `redmc.npc.delete` | op |
| `redmc.npc.reload` | op |
| `redmc.npc.reload.config` | op |
| `redmc.npc.reload.data` | op |
| `redmc.npc.reload.all` | op |
| `redmc.npc.update.name` | op |
| `redmc.npc.update.skin` | op |
| `redmc.npc.update.teleport` | op |
| `redmc.npc.update.equipment` | op |
| `redmc.npc.update.commands` | op |

## Look-at-Player

An async task checks nearby players every `look-at-player.interval-ms` ms and rotates NPCs toward the nearest player within `look-at-player.range` blocks.

## Command Placeholders

Commands support: `{player}`, `{uuid}`, `{world}`, `{x}`, `{y}`, `{z}`

## Config

```yaml
config-version: "0.0.1-alpha"

look-at-player:
  enabled: true
  range: 16.0
  interval-ms: 500

interaction:
  cooldown-ms: 500
```
