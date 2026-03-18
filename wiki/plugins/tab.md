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
