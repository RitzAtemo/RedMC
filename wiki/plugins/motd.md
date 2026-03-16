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

### Template Config Example

```yaml
motd:
  mode: random   # random | sequential
  templates:
    - - "<#1E90FF>━━━ <#F0F8FF>RedMC SMP <#1E90FF>━━━ <#FF1493>● Online"
      - "<#9b94a6>Players: <#F0F8FF>%online%<#9b94a6>/<#F0F8FF>%max% <#888888>● <#F0F8FF>play.redmc.example.com"
    - - "<#FF1493>✦ <#F0F8FF>RedMC SMP <#FF1493>✦ <#1E90FF>Vanilla Survival"
      - "<#FFB800>Join us! <#9b94a6>play.redmc.example.com"
```

## Favicon

Multiple `.png` files (must be 64×64 px) can be listed under `icons.files`. Paths are relative to the plugin's data folder.

```yaml
icons:
  enabled: true
  mode: random       # random | sequential
  files:
    - "server-icon.png"
    - "icons/holiday.png"
```

`reload config` picks up new file paths but does **not** reload the image data from disk.
`reload all` re-reads all icon files from disk and rebuilds the cache.

## Player List

### Count Override

```yaml
players:
  override-count: true
  online: 42    # -1 = use real value
  max: 100      # -1 = use real value
```

### Sample Lines (hover text)

Lines shown when hovering over the player count. MiniMessage formatting is converted to legacy `§` codes for rendering in the vanilla client.

```yaml
players:
  sample:
    enabled: true
    lines:
      - "<#1E90FF>━━━ RedMC SMP ━━━"
      - "<#9b94a6>Vanilla Survival"
      - "<#3DDC97>%online% players online"
```

## Version String

```yaml
version:
  override: true
  text: "<#FF1493>RedMC <#F0F8FF>1.21.11"   # MiniMessage → legacy §
  protocol: -1   # -1 = keep server default; any value forces a mismatch indicator
```

Setting `protocol` to a value that doesn't match the client's protocol version causes the client to show "outdated server / client" — useful for displaying a custom version label regardless of client version.

## Ping Logging

```yaml
ping-logging:
  enabled: true
  format: "<#9b94a6>Ping from <#F0F8FF>%address%"
```

Logged to the server console on every server-list ping. Fires on the Paper async event thread.

## Full Config Reference

```yaml
config-version: "0.0.1-alpha"

motd:
  mode: random
  templates:
    - - "<#1E90FF>━━━ <#F0F8FF>RedMC SMP <#1E90FF>━━━ <#FF1493>● Online"
      - "<#9b94a6>Players: <#F0F8FF>%online%<#9b94a6>/<#F0F8FF>%max% <#888888>● <#F0F8FF>play.redmc.example.com"

icons:
  enabled: false
  mode: random
  files:
    - "server-icon.png"

players:
  override-count: false
  online: -1
  max: -1
  sample:
    enabled: false
    lines:
      - "<#1E90FF>━━━ RedMC SMP ━━━"
      - "<#9b94a6>Vanilla Survival"

version:
  override: false
  text: "<#FF1493>RedMC <#F0F8FF>1.21.11"
  protocol: -1

ping-logging:
  enabled: false
  format: "<#9b94a6>Ping from <#F0F8FF>%address%"
```
