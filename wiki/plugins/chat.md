# Chat Plugin

Chat formatting (local/global), private messaging, death messages, advancement announcements, join/leave notifications, welcome messages, and scheduled alerts.

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

- **Local chat** — default, visible within a configurable radius per world (`chat.local.worlds.<world>.radius`). A radius of `-1` means the entire world.
- **Global chat** — player prefixes the message with the global prefix character (default `!`) to broadcast server-wide.
- Format strings support `##Placeholder##` tokens and `%player%` / `%message%` variables.
- `ChatListener` runs at `HIGHEST` priority, cancels `AsyncChatEvent`, and delegates to `ChatManager.processChat()` via sync scheduler.

## Private Messages

`/msg <player> <message>` sends a private message visible only to the sender and target. `/reply <message>` responds to the last person who messaged you in the current session. `SessionManager` tracks the last sender per player UUID in a `ConcurrentHashMap` and is cleared on quit.

## Join / Leave Notifications

Broadcast messages sent to all online players when someone joins or leaves. Controlled per direction via `join-leave.join.enabled` and `join-leave.leave.enabled`. Messages come from the locale file under `join-leave.join` / `join-leave.leave`. Vanilla join/quit messages are always suppressed.

When a player joins for the first time (`hasPlayedBefore() == false`), an additional broadcast is sent to all players using `join-leave.newbie` — enabled independently via `join-leave.newbie.enabled`.

## Welcome Messages

Private messages delivered directly to the joining player (not broadcast). Two independent messages:

- **`welcome.returning`** — sent to every joining player; enabled via `welcome.enabled`.
- **`welcome.newbie`** — sent only on first join; enabled via `welcome.newbie.enabled`.

When both apply (first-time player), order is controlled by `welcome.newbie.priority-first`:
- `false` (default) — returning message first, then newbie message
- `true` — newbie message first, then returning message

## Death Messages

`ChatManager` loads death message groups from locale files. Each group has a list of `messages` entries (chosen randomly at death time).

| Group | Trigger | Placeholders |
|---|---|---|
| `default` | Generic death | `%player%` |
| `by_player` | Killed by a player (bare hands) | `%player%`, `%killer%` |
| `by_player_weapon` | Killed by a player holding an item | `%player%`, `%killer%`, `%weapon%` |
| `by_fall` | Fall damage | `%player%` |
| `by_fire` | Fire / burning | `%player%` |
| `by_lava` | Lava | `%player%` |
| `by_drowning` | Drowning | `%player%` |
| `by_explosion` | Explosion | `%player%` |
| `by_void` | Void | `%player%` |
| `by_entity` | Killed by a mob | `%player%`, `%killer%` |
| `by_magic` | Magic damage | `%player%` |
| `by_wither` | Wither effect | `%player%` |
| `by_starve` | Starvation | `%player%` |
| `by_lightning` | Lightning strike | `%player%` |

Death messages are broadcast to all players within local chat radius. `DeathListener` determines the group by inspecting `EntityDamageEvent.getCause()` and the last damage cause stored on the player.

## Advancement Announcements

Replaces vanilla advancement broadcasts with formatted per-player locale messages. Three frame types — `task`, `goal`, `challenge` — each rendered with a distinct color scheme. Vanilla announcements are suppressed when `advancement.disable-vanilla: true`. The advancement title is included as hover text.

## Alerts

`AlertManager` runs an async repeating task that fires every `alerts.interval` seconds. Messages are taken from the `alerts.messages` list in config in round-robin order. The last `alerts.no-repeat-count` indices are tracked to prevent consecutive repeats. The task is stopped and restarted on reload.

## Priority and Override

`JoinLeaveListener` runs at `EventPriority.HIGH`. If the Perks plugin has already handled the join message for the player (signalled via `redmc:join-override` metadata), Chat skips its broadcast and clears the flag. The same mechanism applies to quit messages via `redmc:quit-override`.
