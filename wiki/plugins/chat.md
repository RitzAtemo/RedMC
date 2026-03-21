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

## Configuration

| Key | Default | Description |
|---|---|---|
| `chat.global.enabled` | `true` | Enable global chat |
| `chat.global.prefix` | `!` | Character prefix that switches a message to global chat |
| `chat.global.format` | `##PlayerPrefix## <#1E90FF>[G]<#F0F8FF> %player% <#9b94a6>»<#F0F8FF> %message%` | MiniMessage format for global messages |
| `chat.local.enabled` | `true` | Enable local chat |
| `chat.local.format` | `##PlayerPrefix## <#F0F8FF>%player% <#9b94a6>»<#F0F8FF> %message%` | MiniMessage format for local messages |
| `chat.local.default-radius` | `100` | Default radius in blocks for worlds not listed explicitly |
| `chat.local.worlds.<world>.radius` | — | Per-world radius override; `-1` means entire world |
| `chat.private.enabled` | `true` | Enable `/msg` and `/reply` |
| `join-leave.join.enabled` | `true` | Broadcast join message |
| `join-leave.newbie.enabled` | `true` | Broadcast first-join message |
| `join-leave.leave.enabled` | `true` | Broadcast leave message |
| `welcome.enabled` | `true` | Send returning-player welcome message |
| `welcome.newbie.enabled` | `true` | Send first-join welcome message |
| `welcome.newbie.priority-first` | `false` | Send newbie welcome before returning welcome when both apply |
| `death.enabled` | `true` | Enable custom death messages |
| `advancement.enabled` | `true` | Enable custom advancement announcements |
| `advancement.disable-vanilla` | `true` | Suppress vanilla advancement broadcasts |
| `alerts.enabled` | `true` | Enable scheduled alert broadcasts |
| `alerts.interval` | `300` | Seconds between alert broadcasts |
| `alerts.no-repeat-count` | `3` | Number of recent indices tracked to avoid consecutive repeats |
| `alerts.messages` | `[...]` | List of MiniMessage alert strings cycled in round-robin |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Chat<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player not found.` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.alerts-success` | `%prefix%<#3DDC97>Alerts reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
| `chat.no-reply-target` | `%prefix%<#FF6B6B>No one to reply to.` |
| `chat.msg-sent` | `<#9b94a6>[<#F0F8FF>→ %target%<#9b94a6>]<#F0F8FF> %message%` |
| `chat.msg-received` | `<#9b94a6>[<#F0F8FF>%sender% →<#9b94a6>]<#F0F8FF> %message%` |
| `chat.reply-sent` | `<#9b94a6>[<#F0F8FF>→ %target%<#9b94a6>]<#F0F8FF> %message% <#888888>(↩ "%quoted%")` |
| `chat.reply-received` | `<#9b94a6>[<#F0F8FF>%sender% →<#9b94a6>]<#F0F8FF> %message% <#888888>(↩ "%quoted%")` |
| `welcome.returning` | `<#1E90FF>Welcome back, <#F0F8FF>%player%<#1E90FF>!` |
| `welcome.newbie` | `<#FFB800>Welcome to the server, <#F0F8FF>%player%<#FFB800>! Use <#3DDC97>/help<#FFB800> to get started.` |
| `join-leave.join` | `<#3DDC97>+<#F0F8FF> %player% <#9b94a6>joined the server` |
| `join-leave.newbie` | `<#FFB800>★<#F0F8FF> %player% <#9b94a6>joined the server for the first time!` |
| `join-leave.leave` | `<#FF6B6B>-<#F0F8FF> %player% <#9b94a6>left the server` |
| `death.groups.<group>.messages` | List of MiniMessage strings; one is chosen randomly at death. See death groups table above. |
| `advancement.task` | `<#9b94a6>[<#3DDC97>Advancement<#9b94a6>]<#F0F8FF> %player%<#9b94a6> has made the advancement <#F0F8FF>[<#3DDC97>%title%<#F0F8FF>]` |
| `advancement.goal` | `<#9b94a6>[<#1E90FF>Advancement<#9b94a6>]<#F0F8FF> %player%<#9b94a6> has reached the goal <#1E90FF>[<#F0F8FF>%title%<#1E90FF>]` |
| `advancement.challenge` | `<#9b94a6>[<#FFB800>Advancement<#9b94a6>]<#F0F8FF> %player%<#9b94a6> has completed the challenge <#FFB800>[<#F0F8FF>%title%<#FFB800>]` |

## Mute Integration

`ChatListener` checks the `redmc:muted` metadata key on the player before dispatching to `ChatManager.processChat()`. If the key is present, the message is silently dropped — `processChat` is never called. This check is intentionally decoupled: Chat does not depend on the Moderation plugin. Any plugin that wants to block a player from chatting can set the `redmc:muted` metadata; removing it re-enables chat.

The Moderation plugin manages this metadata:
- Set on `/mute` (if the player is online) and on `PlayerJoinEvent` (if the player has an active mute on join).
- Removed on `/unmute` and on automatic expiry (checked at the next chat attempt by `PlayerChatListener` in the Moderation plugin, which also sends the "you are muted" message with remaining time).

`MsgCommand` and `ReplyCommand` also check `redmc:muted` before sending a private message. If set, the command returns silently — no message is sent and no feedback is shown. The Moderation plugin's `PlayerChatListener` is responsible for all "you are muted" notifications.

## Priority and Override

`JoinLeaveListener` runs at `EventPriority.HIGH`. If the Perks plugin has already handled the join message for the player (signalled via `redmc:join-override` metadata), Chat skips its broadcast and clears the flag. The same mechanism applies to quit messages via `redmc:quit-override`.
