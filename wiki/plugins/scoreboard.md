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

## Config Example

```yaml
config-version: "0.0.1-alpha"

title:
  animation: true
  interval: 100
  frames:
    - "<#1E90FF>═══ RedMC SMP ═══"
    - "<#FF1493>═══ RedMC SMP ═══"

lines:
  - text: " "
  - text: "<#F0F8FF>Player: <#1E90FF>##PlayerName##"
  - text: "<#F0F8FF>Balance: <#3DDC97>##PlayerBalance##"
  - text: "<#F0F8FF>Online: <#1E90FF>##CurrentServerOnline##"
  - text: " "
  - text: "<#9b94a6>play.redmc.example.com"
```
