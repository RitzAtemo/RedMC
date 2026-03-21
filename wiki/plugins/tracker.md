# Tracker Plugin

Admin command to track a player's coordinates in real time. Saves and restores player positions across sessions.

## Command Tree

```
/tracker
├── start <player>
├── stop
└── reload
    ├── config
    ├── data
    └── all
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.tracker` | op |
| `redmc.tracker.reload` | op |
| `redmc.tracker.reload.config` | op |
| `redmc.tracker.reload.data` | op |
| `redmc.tracker.reload.all` | op |
| `redmc.tracker.bypass` | false |

`redmc.tracker.bypass` — skips position restore on join for the player who holds it.

## Tracking

`/tracker start <player>` begins a tracking session for the executing admin. Every `update-interval` ticks the target player's current coordinates and world are displayed in the admin's action bar using the `tracker-format` from `config.yml`. Only one target can be tracked per admin at a time — starting a new session replaces the previous one.

The session ends automatically if the target player goes offline. `/tracker stop` ends it manually.

## Position Save / Restore

On `PlayerQuitEvent` the player's location is written to `tracker.yml` (keyed by UUID) when `save-on-quit: true`. On `PlayerJoinEvent` the restore is scheduled via `getGlobalRegionScheduler().runDelayed()` with a 1-tick delay to allow Folia to complete the player spawn sequence, then executed via `teleportAsync()`. The delay is required because calling `teleportAsync` directly inside `PlayerJoinEvent` causes a `Player is already removed from player chunk loader` error in Folia. If the player goes offline during the delay, the teleport is skipped. If no entry exists for the player, the server's default spawn behaviour is used unchanged. Players holding `redmc.tracker.bypass` skip the restore entirely.

## Configuration

`config.yml`:

| Key | Default | Description |
|---|---|---|
| `update-interval` | `20` | Ticks between action bar updates |
| `tracker-format` | `<#F0F8FF>%player% ...` | MiniMessage format. Placeholders: `%player%`, `%x%`, `%y%`, `%z%`, `%world%` |
| `save-on-quit` | `true` | Save position on disconnect |
| `restore-on-join` | `true` | Teleport to saved position on join |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Tracker<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player '<#F0F8FF>%player%<#FF6B6B>' not found.` |
| `tracking-started` | `%prefix%<#3DDC97>Now tracking '<#F0F8FF>%player%<#3DDC97>'.` |
| `tracking-stopped` | `%prefix%<#3DDC97>Tracking stopped.` |
| `already-tracking` | `%prefix%<#FF6B6B>You are already tracking '<#F0F8FF>%player%<#FF6B6B>'.` |
| `not-tracking` | `%prefix%<#FF6B6B>You are not tracking anyone.` |
| `position-restored` | `<#3DDC97>Your last position has been restored.` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |

## Data Storage

`TrackerDataStorage` reads and writes `tracker.yml` directly via `YamlConfiguration`. Player entries are keyed by UUID. The file is not subject to the config-version backup mechanism.
