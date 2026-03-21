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

## Configuration

| Key | Default | Description |
|---|---|---|
| `refresh-interval-ticks` | `20` | Ticks between placeholder refresh passes |
| `line-spacing` | `0.3` | Vertical gap in blocks between hologram lines |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Holograms<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `hologram-not-found` | `%prefix%<#FF6B6B>Hologram with id '<#F0F8FF>%id%<#FF6B6B>' not found.` |
| `hologram-already-exists` | `%prefix%<#FF6B6B>Hologram with id '<#F0F8FF>%id%<#FF6B6B>' already exists.` |
| `hologram-created` | `%prefix%<#3DDC97>Hologram '<#F0F8FF>%id%<#3DDC97>' created at your location.` |
| `hologram-deleted` | `%prefix%<#3DDC97>Hologram '<#F0F8FF>%id%<#3DDC97>' deleted.` |
| `hologram-read-header` | `%prefix%<#F0F8FF>Holograms (<#3DDC97>%count%<#F0F8FF>):` |
| `hologram-read-entry` | `  <#1E90FF>- <#F0F8FF>%id% <#888888>(%name%) @ %world% %.1f%x% %.1f%y% %.1f%z% <#9b94a6>[%lines% lines]` |
| `hologram-name-set` | `%prefix%<#3DDC97>Name of hologram '<#F0F8FF>%id%<#3DDC97>' set to '<#F0F8FF>%name%<#3DDC97>'.` |
| `hologram-moved` | `%prefix%<#3DDC97>Hologram '<#F0F8FF>%id%<#3DDC97>' moved to your location.` |
| `hologram-line-set` | `%prefix%<#3DDC97>Line <#F0F8FF>%index%<#3DDC97> of hologram '<#F0F8FF>%id%<#3DDC97>' set.` |
| `hologram-line-added` | `%prefix%<#3DDC97>Line added to hologram '<#F0F8FF>%id%<#3DDC97>'.` |
| `hologram-line-removed` | `%prefix%<#3DDC97>Line <#F0F8FF>%index%<#3DDC97> removed from hologram '<#F0F8FF>%id%<#3DDC97>'.` |
| `hologram-lines-cleared` | `%prefix%<#3DDC97>All lines of hologram '<#F0F8FF>%id%<#3DDC97>' cleared.` |
| `invalid-line-index` | `%prefix%<#FF6B6B>Invalid line index '<#F0F8FF>%index%<#FF6B6B>'. Valid range: 0-%max%.` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |

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
