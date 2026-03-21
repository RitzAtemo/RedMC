# Playtime Plugin

Tracks per-player playtime and detects AFK players. AFK time is excluded from counted playtime.

## Command Tree

```
/playtime
├── [player]
└── reload
    ├── config
    ├── data
    └── all
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.playtime` | true |
| `redmc.playtime.others` | op |
| `redmc.playtime.reload` | op |
| `redmc.playtime.reload.config` | op |
| `redmc.playtime.reload.data` | op |
| `redmc.playtime.reload.all` | op |
| `redmc.playtime.afk.bypass` | false |

`redmc.playtime.afk.bypass` — player is never marked AFK regardless of inactivity.

## Playtime Tracking

On `PlayerJoinEvent` a session start timestamp is recorded. On `PlayerQuitEvent` the elapsed session time minus accumulated AFK time is added to the player's stored total and flushed to `playtime.yml`.

`/playtime` shows the calling player's own total. `/playtime <player>` shows another online player's total and requires `redmc.playtime.others`. Playtime is formatted as `Xd Xh Xm Xs`, omitting leading zero units.

## AFK Detection

A background task runs every second via `getGlobalRegionScheduler().runAtFixedRate()`. If a player's last recorded activity was more than `afk.timeout` seconds ago, they are marked AFK. Activity is recorded on `PlayerMoveEvent` (only if the player moved more than `afk.movement-threshold` blocks from the last recorded position), `PlayerInteractEvent`, `AsyncChatEvent`, and `PlayerCommandPreprocessEvent`.

When a player goes AFK the time is noted. When they return, the elapsed AFK duration is subtracted from their session so it does not count toward playtime.

If `afk.kick.enabled` is `true`, a player who has been AFK for longer than `afk.kick.delay` seconds is kicked with the configured reason.

## Configuration

`config.yml`:

| Key | Default | Description |
|---|---|---|
| `afk.enabled` | `true` | Enable AFK detection |
| `afk.timeout` | `300` | Seconds of inactivity before AFK |
| `afk.movement-threshold` | `2.0` | Minimum movement in blocks to reset AFK timer |
| `afk.broadcast.on-afk` | `true` | Broadcast to all players when someone goes AFK |
| `afk.broadcast.on-return` | `true` | Broadcast when they return |
| `afk.kick.enabled` | `false` | Kick players after extended AFK |
| `afk.kick.delay` | `600` | Seconds after going AFK before kick |
| `afk.kick.reason` | `<red>You were kicked for being AFK.` | MiniMessage kick screen |
| `playtime.save-interval` | `6000` | Ticks between auto-saves |

## Placeholders

Registered into RedMC-Placeholders at priority 5.

| Token | Value |
|---|---|
| `##PlayerPlaytime##` | Formatted playtime, e.g. `2d 3h 15m 4s` |
| `##PlayerPlaytimeSeconds##` | Raw total playtime in seconds |
| `##PlayerAFK##` | `true` or `false` |

## Data Storage

`PlaytimeDataStorage` reads and writes `playtime.yml` directly via `YamlConfiguration`. Player entries are keyed by UUID under `players.<uuid>` with fields `playtime-seconds`, `first-join`, and `last-seen` (Unix epoch seconds).
