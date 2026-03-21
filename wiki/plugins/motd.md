# MOTD Plugin

Server list customization: MOTD text, favicon, player sample, version string, and ping logging.

## Command Tree

```
/motd
└── reload
    └── all      — reload config.yml
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.motd` | op |
| `redmc.motd.reload` | op |
| `redmc.motd.reload.all` | op |

## Template System

Templates are defined under `motd.templates` in `config.yml`. Each template is a list of **two strings** — line 1 and line 2 of the MOTD. Both lines support full MiniMessage formatting and placeholders.

### Selection Modes

| Value | Behaviour |
|---|---|
| `random` | A template is picked at random on every ping |
| `sequential` | Templates cycle in order across pings |

### Built-in Placeholders

These are resolved at ping time without a player context:

| Placeholder | Value |
|---|---|
| `%online%` | Current online player count |
| `%max%` | Maximum player count |
| `%version%` | Server version string |
| `%address%` | Client IP address (ping logging only) |

### RedMC Placeholder Integration

If **RedMC-Placeholders** is loaded, all `##Key##` tokens in templates are resolved first via `PlaceholderParser` with a `null` player. Placeholders that require a player instance will silently return unresolved (skipped) — only server-level placeholders will expand.

## Favicon

Multiple `.png` files (must be 64×64 px) can be listed under `icons.files`. Paths are relative to the plugin's data folder. `reload all` re-reads all icon files from disk and rebuilds the cache.

## Player List

Lines shown when hovering over the player count. MiniMessage formatting is converted to legacy `§` codes for rendering in the vanilla client.

## Version String

Setting `version.protocol` to a value that doesn't match the client's protocol version causes the client to show "outdated server / client" — useful for displaying a custom version label regardless of client version.

## Ping Logging

Logs to the server console on every server-list ping. Fires on the Paper async event thread.

## Configuration

| Key | Default | Description |
|---|---|---|
| `motd.mode` | `random` | Template selection mode: `random` or `sequential` |
| `motd.templates` | `[...]` | List of templates; each template is a two-element list of MiniMessage strings (line 1, line 2) |
| `icons.enabled` | `false` | Enable favicon rotation |
| `icons.mode` | `random` | Favicon selection mode: `random` or `sequential` |
| `icons.files` | `[server-icon.png]` | List of PNG file paths relative to the plugin data folder |
| `players.override-count` | `false` | Override the displayed player count |
| `players.online` | `-1` | Displayed online count; `-1` uses the real value |
| `players.max` | `-1` | Displayed max count; `-1` uses the real value |
| `players.sample.enabled` | `false` | Enable hover text on the player count |
| `players.sample.lines` | `[...]` | List of MiniMessage lines shown on hover |
| `version.override` | `false` | Override the version string shown in the server browser |
| `version.text` | `<#FF1493>RedMC <#F0F8FF>1.21.11` | MiniMessage version label (converted to legacy `§` codes) |
| `version.protocol` | `-1` | Protocol number; `-1` keeps server default; any mismatch triggers "outdated" indicator |
| `ping-logging.enabled` | `false` | Log each ping to console |
| `ping-logging.format` | `<#9b94a6>Ping from <#F0F8FF>%address%` | MiniMessage format; `%address%` is the client IP |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>MOTD<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
