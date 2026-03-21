# Tab Plugin

Tab list header/footer customization with frame-based animations and placeholder support.

## Command Tree

```
/tab
└── reload
    ├── config
    └── all
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.tab` | op |
| `redmc.tab.reload` | op |
| `redmc.tab.reload.config` | op |
| `redmc.tab.reload.all` | op |

## Configuration

| Key | Default | Description |
|---|---|---|
| `header.animation` | `true` | Enable animated header cycling |
| `header.interval` | `100` | Ticks between header frame changes |
| `header.frames` | `[...]` | List of MiniMessage header frames; `\n` separates lines within a frame |
| `footer.animation` | `false` | Enable animated footer cycling |
| `footer.interval` | `20` | Ticks between footer frame changes |
| `footer.frames` | `[...]` | List of MiniMessage footer frames |
| `player-row.format` | `<#1E90FF>##PlayerPrefix## ##PlayerName##` | MiniMessage format for each player row |
| `player-row.interval` | `20` | Ticks between player-row refresh passes |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Tab<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `success` | `%prefix%<#3DDC97>Done.` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
