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

## Animation System

- Header and footer each have an `animation` flag, an `interval` (ticks), and a list of `frames`
- `TabManager.tick()` is called every 50ms via `GlobalRegionScheduler`
- Each tick, the counter increments; the frame advances only when it reaches the frame's interval
- On player join, the current frame is sent immediately via `applyTab(player)`
- `reload all` reloads config and applies the new tab to all online players

## Config Example

```yaml
config-version: "0.0.1-alpha"

header:
  animation: true
  interval: 100
  frames:
    - "<#1E90FF>═══ RedMC SMP ═══\n<#F0F8FF>Players: ##CurrentServerOnline##/##ServerOnlineMaximum##"
    - "<#FF1493>═══ RedMC SMP ═══\n<#F0F8FF>Players: ##CurrentServerOnline##/##ServerOnlineMaximum##"

footer:
  animation: false
  interval: 20
  frames:
    - "<#9b94a6>play.redmc.example.com"

player-row:
  format: "<#1E90FF>##PlayerPrefix## ##PlayerName##"
  interval: 20
```
