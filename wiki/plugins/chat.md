# Chat Plugin

Chat formatting (local/global), private messaging, death messages, advancement announcements, join/leave notifications, and scheduled alerts.

## Command Tree

```
/chat
└── reload
    ├── config
    ├── alerts
    └── all

/msg <player> <message>    (alias: /tell)
/reply <message>           (alias: /r)
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.chat` | true |
| `redmc.chat.global` | true |
| `redmc.chat.msg` | true |
| `redmc.chat.reload` | op |
| `redmc.chat.reload.config` | op |
| `redmc.chat.reload.alerts` | op |
| `redmc.chat.reload.all` | op |

## Chat Modes

- **Local chat** — default, visible within a configurable radius per world
- **Global chat** — player types the prefix character (default `!`) to broadcast server-wide

Format strings support `##Placeholder##` tokens and `%player%` / `%message%` variables.

## Private Messages

`/msg <player> <message>` sends a private message. `/reply <message>` responds to the last person who messaged you. `SessionManager` tracks the last sender per player UUID.

## Death Messages

`ChatManager` loads death message groups from locale files. Each group has a list of `messages` (chosen randomly).

| Group | Trigger | Placeholders |
|---|---|---|
| `default` | Generic death | `%player%` |
| `by_player` | Killed by a player (bare hands) | `%player%`, `%killer%` |
| `by_player_weapon` | Killed by a player holding an item | `%player%`, `%killer%`, `%weapon%` |
| `by_fall` | Fall damage | `%player%` |
| `by_fire` | Fire/burning | `%player%` |
| `by_lava` | Lava | `%player%` |
| `by_drowning` | Drowning | `%player%` |
| `by_explosion` | Explosion | `%player%` |
| `by_void` | Void | `%player%` |
| `by_entity` | Killed by a mob | `%player%`, `%killer%` |
| `by_magic` | Magic damage | `%player%` |
| `by_wither` | Wither effect | `%player%` |
| `by_starve` | Starvation | `%player%` |
| `by_lightning` | Lightning strike | `%player%` |

## Advancement Announcements

Replaces vanilla advancement broadcasts. Three types: `task`, `goal`, `challenge` — each with a distinct color scheme.
Vanilla announcements can be disabled with `advancement.disable-vanilla: true` in config.

## Alerts

Scheduled rotating broadcasts loaded from `config.yml`. `AlertManager` picks messages in order, skipping the last `no-repeat-count` messages to avoid repetition.
