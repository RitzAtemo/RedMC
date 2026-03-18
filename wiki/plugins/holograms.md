# Holograms Plugin

Text-display entity holograms with placeholder support. Holograms are stored in YAML and rendered using the Paper `TextDisplay` API (no NMS required).

## Command Tree

```
/holograms
├── create <id>
├── read
├── delete <id>
├── reload
│   ├── config
│   ├── data
│   └── all
└── update <id>
    ├── name <name>
    ├── location
    └── lines
        ├── add <text>
        ├── set <index> <text>
        ├── remove <index>
        └── clear
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.holograms` | op |
| `redmc.holograms.create` | op |
| `redmc.holograms.read` | op |
| `redmc.holograms.delete` | op |
| `redmc.holograms.reload` | op |
| `redmc.holograms.reload.config` | op |
| `redmc.holograms.reload.data` | op |
| `redmc.holograms.reload.all` | op |
| `redmc.holograms.update.location` | op |
| `redmc.holograms.update.name` | op |
| `redmc.holograms.update.lines` | op |

## Placeholder Support

Line text supports `##Key##` placeholder tokens resolved by the RedMC-Placeholders registry.

- Placeholders are refreshed on a global tick set by `refresh-interval-ticks`.
- The refresh uses the first online player in the hologram's world as the context. For player-specific placeholders, use a single-player context (e.g. scoreboards or tab instead).
- On player join, all hologram texts are updated once immediately.

## Line Management

Lines are zero-indexed. Use `/holograms update <id> lines add <text>` to append a line, then `set <index> <text>` to edit individual lines.

MiniMessage formatting is supported in line text: `<#FF1493>Hello <#F0F8FF>##PlayerName##`.

## Config

```yaml
config-version: "0.0.1-alpha"

refresh-interval-ticks: 20

line-spacing: 0.3
```

## Data Storage

Holograms are stored in `holograms.yml`:

```yaml
holograms:
  spawn_info:
    name: "Spawn Info"
    world: world
    x: 0.5
    y: 65.0
    z: 0.5
    lines:
      - "<#1E90FF><bold>Welcome to RedMC"
      - "<#9b94a6>Players online: ##OnlinePlayers##"
```
