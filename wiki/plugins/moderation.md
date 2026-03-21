# Moderation Plugin

Player moderation: warnings, mutes, bans, and history tracking.

## Command Tree

```
/warn <player> <reason>
/mute <player> <duration> <reason>
/unmute <player>
/ban <player> <duration> <reason>
/unban <player>
/history <player>

/moderation
└── reload
    ├── config
    ├── data
    └── all
```

Duration format: `1d`, `2h`, `30m`, `60s`, `perm` / `permanent` (permanent).

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.moderation` | op |
| `redmc.moderation.warn` | op |
| `redmc.moderation.mute` | op |
| `redmc.moderation.unmute` | op |
| `redmc.moderation.ban` | op |
| `redmc.moderation.unban` | op |
| `redmc.moderation.history` | op |
| `redmc.moderation.reload` | op |
| `redmc.moderation.reload.config` | op |
| `redmc.moderation.reload.data` | op |
| `redmc.moderation.reload.all` | op |

## Warn

`/warn <player> <reason>` — stores a warning in the player's moderation history. The target receives a private notification, and if `moderation.broadcast-warns` is `true`, all online players are notified. Warns are always active (no duration or expiry).

## Mute

`/mute <player> <duration> <reason>` — prevents the player from sending chat messages. The mute is stored as an active action and loaded into memory on startup.

`/unmute <player>` — deactivates the active mute. Works for offline players: UUID is resolved from the active mutes map by name, with a fallback to `Bukkit.getOfflinePlayer()` (usercache.json).

### Metadata integration

Mute enforcement is implemented via the `redmc:muted` Bukkit metadata key, so the Chat plugin (or any other plugin) can block chat without depending on Moderation directly:

- `MuteManager` calls `player.setMetadata("redmc:muted", FixedMetadataValue(true))` when a mute is applied to an online player, and `player.removeMetadata("redmc:muted", ...)` on unmute or automatic expiry.
- `ModerationPlayerJoinListener` (priority `MONITOR`) restores the metadata on login if the player has an active mute, and **removes** it if they do not — ensuring offline unmutes take effect at next login.
- `PlayerChatListener` in the Moderation plugin runs at `HIGHEST` priority: if the player is muted, it sends the "you are muted" message with remaining time and auto-expires if needed.
- `MutedCommandListener` in the Moderation plugin runs at `LOWEST` priority on `PlayerCommandPreprocessEvent`: if the player has the `redmc:muted` metadata and the command matches any entry in `moderation.mute.blocked-commands`, the event is cancelled.
- The Chat plugin's `ChatListener` checks `player.hasMetadata("redmc:muted")` and skips `processChat` if the key is present.

Expiry is checked on every chat attempt by `PlayerChatListener`: if the mute has expired, it is automatically deactivated, metadata is cleared, and the message is allowed through.

## Ban

`/ban <player> <duration> <reason>` — stores a ban and loads it into memory. `PlayerLoginEvent` is intercepted at `LOWEST` priority: if the player has an active ban, `event.disallow(KICK_BANNED, message)` is called with the formatted ban screen showing reason and expiry.

`/unban <player>` — deactivates the active ban. Works for offline players: UUID is resolved from the active bans map by name, with a fallback to `Bukkit.getOfflinePlayer()`.

Expiry is checked at login: expired bans are automatically deactivated.

## History GUI

`/history <player>` opens a 54-slot paginated inventory (`HistoryHolder`):

- **Slots 0–44** — moderation actions, newest first
  - WARN → PAPER (`⚠ Warning`)
  - MUTE → BOOK (`Mute`)
  - BAN → BARRIER (`Ban`)
  - Lore: reason, staff name, date, duration, status (Active / Expired / Pardoned)
- **Slot 45** — previous page (ARROW)
- **Slot 48** — info item: player name + total action count (BOOK)
- **Slot 49** — close button (BARRIER)
- **Slot 53** — next page (ARROW)
- All other slots filled with BLACK_STAINED_GLASS_PANE

## Data Storage

Actions are stored in `actions.yml`, keyed by player UUID, and loaded fully into memory on startup.

```yaml
actions:
  <uuid>:
    - id: <uuid>
      type: WARN|MUTE|BAN
      staff-uuid: <uuid>
      staff-name: <name>
      target-name: <name>
      reason: <text>
      timestamp: <millis>
      duration: -1|<seconds>
      active: true|false
```

`target-name` is stored alongside each action to support offline player lookup for unban/unmute suggestions and UUID resolution.

## Configuration

| Key | Default | Description |
|---|---|---|
| `moderation.broadcast-warns` | `true` | Broadcast warn notifications to all online players |
| `moderation.ban-screen.appeal-url` | `discord.gg/server` | URL shown on the ban screen for appeals |
| `moderation.mute.blocked-commands` | `[say, msg, tell, w, whisper, pm, reply, r, re]` | Commands cancelled for muted players via `PlayerCommandPreprocessEvent` |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Moderation<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player not found.` |
| `error.invalid-duration` | `%prefix%<#FF6B6B>Invalid duration. Use: 1d, 2h, 30m, 60s, perm.` |
| `warn.success` | `%prefix%<#3DDC97>%player% has been warned. Reason: <#F0F8FF>%reason%` |
| `warn.received` | `%prefix%<#FFB800>You have been warned. Reason: <#F0F8FF>%reason%` |
| `warn.notify` | `<#9b94a6>[Moderation] <#FFB800>%target_prefix%%target_altname%%target_suffix% <#9b94a6>was warned by <#F0F8FF>%sender_prefix%%sender_altname%%sender_suffix%<#9b94a6>. Reason: <#F0F8FF>%reason%` |
| `mute.success` | `%prefix%<#3DDC97>%player% has been muted for <#F0F8FF>%duration%<#3DDC97>. Reason: <#F0F8FF>%reason%` |
| `mute.success-perm` | `%prefix%<#3DDC97>%player% has been permanently muted. Reason: <#F0F8FF>%reason%` |
| `mute.received` | `%prefix%<#FF6B6B>You have been muted for <#F0F8FF>%duration%<#FF6B6B>. Reason: <#F0F8FF>%reason%` |
| `mute.received-perm` | `%prefix%<#FF6B6B>You have been permanently muted. Reason: <#F0F8FF>%reason%` |
| `mute.blocked` | `%prefix%<#FF6B6B>You are muted. Remaining: <#F0F8FF>%remaining%` |
| `mute.blocked-perm` | `%prefix%<#FF6B6B>You are permanently muted.` |
| `mute.unmute-success` | `%prefix%<#3DDC97>%player% has been unmuted.` |
| `mute.not-muted` | `%prefix%<#FF6B6B>%player% is not muted.` |
| `ban.success` | `%prefix%<#3DDC97>%player% has been banned for <#F0F8FF>%duration%<#3DDC97>. Reason: <#F0F8FF>%reason%` |
| `ban.success-perm` | `%prefix%<#3DDC97>%player% has been permanently banned. Reason: <#F0F8FF>%reason%` |
| `ban.screen` | `<red><bold>You are banned!\n\n<white>Reason: <yellow>%reason%\n<white>Expires: <yellow>%expiry%\n\n<gray>Appeal at <white>discord.gg/server` |
| `ban.screen-perm` | `<red><bold>You are permanently banned!\n\n<white>Reason: <yellow>%reason%\n\n<gray>Appeal at <white>discord.gg/server` |
| `ban.unban-success` | `%prefix%<#3DDC97>%player% has been unbanned.` |
| `ban.not-banned` | `%prefix%<#FF6B6B>%player% is not banned.` |
| `history.no-history` | `%prefix%<#9b94a6>No moderation history for <#F0F8FF>%player%<#9b94a6>.` |
| `gui.history.title` | `<#1E90FF>History: <#F0F8FF>%player%` |
| `gui.history.prev-page` | `<#9b94a6>← Previous Page` |
| `gui.history.next-page` | `<#9b94a6>Next Page →` |
| `gui.history.close` | `<#FF6B6B>Close` |
| `gui.history.info-name` | `<#1E90FF>%player% History` |
| `gui.history.info-lore` | `<#9b94a6>Total actions: <#F0F8FF>%total%` |
| `gui.history.warn-name` | `<#FFB800>⚠ Warning` |
| `gui.history.mute-name` | `<#FF6B6B>Mute` |
| `gui.history.ban-name` | `<#FF1493>Ban` |
| `gui.history.lore-reason` | `<#9b94a6>Reason: <#F0F8FF>%reason%` |
| `gui.history.lore-staff` | `<#9b94a6>By: <#F0F8FF>%staff%` |
| `gui.history.lore-date` | `<#9b94a6>Date: <#F0F8FF>%date%` |
| `gui.history.lore-duration` | `<#9b94a6>Duration: <#F0F8FF>%duration%` |
| `gui.history.lore-status-active` | `<#FF6B6B>● Active` |
| `gui.history.lore-status-expired` | `<#9b94a6>● Expired` |
| `gui.history.lore-status-pardoned` | `<#3DDC97>● Pardoned` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
