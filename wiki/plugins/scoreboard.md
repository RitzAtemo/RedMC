# Scoreboard Plugin

Per-player sidebar scoreboard with frame-based title animations and placeholder support.

## Command Tree

```
/sb
├── reload
│   ├── config
│   └── all
└── toggle
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.scoreboard` | true |
| `redmc.scoreboard.reload` | op |
| `redmc.scoreboard.reload.config` | op |
| `redmc.scoreboard.reload.all` | op |
| `redmc.scoreboard.toggle` | true |

## Configuration

| Key | Default | Description |
|---|---|---|
| `title.animation` | `true` | Enable animated title cycling |
| `title.interval` | `100` | Ticks between title frame changes |
| `title.frames` | `[...]` | List of MiniMessage title frames |
| `lines` | `[...]` | Ordered list of sidebar line objects; each entry has a `text` field (MiniMessage + `##Placeholder##` + `%player_prefix%`/`%player_altname%`/`%player_suffix%`) |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Scoreboard<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `success` | `%prefix%<#3DDC97>Done.` |
| `toggle-shown` | `%prefix%<#3DDC97>Scoreboard is now visible.` |
| `toggle-hidden` | `%prefix%<#FF6B6B>Scoreboard is now hidden.` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
