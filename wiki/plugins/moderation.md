# Moderation Plugin

Player moderation: warnings, mutes, bans with history tracking, and a support ticket system with GUI management.

## Command Tree

```
/warn <player> <reason>
/mute <player> <duration> <reason>
/unmute <player>
/ban <player> <duration> <reason>
/unban <player>
/history <player>

/ticket
├── create <message>
├── list
└── close <id>

/tickets
├── list
├── view <id>
├── close <id>
└── reply <id> <message>

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
| `redmc.moderation.warn` | op |
| `redmc.moderation.mute` | op |
| `redmc.moderation.unmute` | op |
| `redmc.moderation.ban` | op |
| `redmc.moderation.unban` | op |
| `redmc.moderation.history` | op |
| `redmc.ticket` | true |
| `redmc.ticket.close.own` | true |
| `redmc.tickets` | op |
| `redmc.moderation.reload` | op |
| `redmc.moderation.reload.config` | op |
| `redmc.moderation.reload.data` | op |
| `redmc.moderation.reload.all` | op |

## Warn

`/warn <player> <reason>` — stores a warning in the player's moderation history. The target receives a private notification, and if `moderation.broadcast-warns` is `true`, all online players are notified. Warns are always active (no duration or expiry).

## Mute

`/mute <player> <duration> <reason>` — prevents the player from sending chat messages. The mute is stored as an active action and loaded into memory on startup.

`/unmute <player>` — deactivates the active mute and clears it from memory.

### Metadata integration

Mute enforcement is implemented via the `redmc:muted` Bukkit metadata key, so the Chat plugin (or any other plugin) can block chat without depending on Moderation directly:

- `MuteManager` calls `player.setMetadata("redmc:muted", FixedMetadataValue(true))` when a mute is applied to an online player, and `player.removeMetadata("redmc:muted", ...)` on unmute or automatic expiry.
- `ModerationPlayerJoinListener` (priority `MONITOR`) restores the metadata on login if the player has an active mute.
- `PlayerChatListener` in the Moderation plugin runs at `HIGHEST` priority: if the player is muted, it sends the "you are muted" message with remaining time and auto-expires if needed.
- `MutedCommandListener` in the Moderation plugin runs at `LOWEST` priority on `PlayerCommandPreprocessEvent`: if the player is muted and the command matches any entry in `moderation.mute.blocked-commands`, the event is cancelled and the same mute message (with remaining time) is sent.
- The Chat plugin's `ChatListener` checks `player.hasMetadata("redmc:muted")` and skips `processChat` if the key is present. `/msg` and `/reply` also check the key and return silently, leaving the notification entirely to the moderation plugin.

Expiry is checked on every chat attempt by `PlayerChatListener`: if the mute has expired, it is automatically deactivated, metadata is cleared, and the message is allowed through.

## Ban

`/ban <player> <duration> <reason>` — stores a ban and loads it into memory. `PlayerLoginEvent` is intercepted at `LOWEST` priority: if the player has an active ban, `event.disallow(KICK_BANNED, message)` is called with the formatted ban screen showing reason and expiry.

`/unban <player>` — deactivates the active ban.

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

## Ticket System

### Player commands (`redmc.ticket`)

- `/ticket create <message>` — opens a support ticket; notifies all online players with `redmc.tickets` permission
- `/ticket list` — shows the player's own tickets with short ID, status, and message snippet
- `/ticket close <id>` — closes own ticket (`redmc.ticket.close.own`)

### Staff commands (`redmc.tickets`)

- `/tickets list` — opens the Ticket List GUI
- `/tickets view <id>` — opens the Ticket View GUI for a specific ticket
- `/tickets close <id>` — closes a ticket
- `/tickets reply <id> <message>` — appends a reply; notifies all staff online

### Ticket List GUI (`TicketListHolder`)

- **Slots 0–44** — ticket items
  - OPEN tickets → WRITABLE_BOOK (`Ticket #<short-id>` in green)
  - CLOSED tickets → WRITTEN_BOOK (`Ticket #<short-id>` in gray)
  - Lore: author, message snippet, date, status, reply count
  - Click → opens Ticket View GUI
- **Slot 45** — previous page
- **Slot 49** — filter toggle (HOPPER): ALL / OPEN only
- **Slot 53** — next page

### Ticket View GUI (`TicketViewHolder`)

- **Slot 4** — ticket info item (PAPER): author, full message, date, status
- **Slots 9–35** — reply items (BOOK): staff name, message, date
- **Slot 45** — back button (ARROW) → returns to Ticket List GUI
- **Slot 49** — close ticket button (BARRIER) if OPEN; disabled if CLOSED
- **Slot 53** — reply hint (FEATHER): lore shows `/tickets reply <id> <message>`

## Data Storage

Actions and tickets are stored in separate YAML files. Both are keyed for fast lookup and loaded fully into memory on startup.

**`actions.yml`** — actions keyed by player UUID:
```yaml
actions:
  <uuid>:
    - id: <uuid>
      type: WARN|MUTE|BAN
      staff-uuid: <uuid>
      staff-name: <name>
      reason: <text>
      timestamp: <millis>
      duration: -1|<seconds>
      active: true|false
```

**`tickets.yml`** — flat list of all tickets:
```yaml
tickets:
  - id: <uuid>
    author-uuid: <uuid>
    author-name: <name>
    message: <text>
    status: OPEN|CLOSED
    timestamp: <millis>
    replies:
      - staff-uuid: <uuid>
        staff-name: <name>
        message: <text>
        timestamp: <millis>
```

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
| `error.ticket-not-found` | `%prefix%<#FF6B6B>Ticket not found.` |
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
| `ticket.created` | `%prefix%<#3DDC97>Ticket created. ID: <#F0F8FF>%id%` |
| `ticket.closed` | `%prefix%<#3DDC97>Ticket <#F0F8FF>%id%<#3DDC97> has been closed.` |
| `ticket.already-closed` | `%prefix%<#FF6B6B>Ticket <#F0F8FF>%id%<#FF6B6B> is already closed.` |
| `ticket.reply-sent` | `%prefix%<#3DDC97>Reply sent to ticket <#F0F8FF>%id%<#3DDC97>.` |
| `ticket.notify-staff` | `<#1E90FF>[Ticket] <#F0F8FF>%player% <#9b94a6>opened ticket <#F0F8FF>%id%<#9b94a6>: <#F0F8FF>%message%` |
| `ticket.notify-reply` | `<#1E90FF>[Ticket] <#F0F8FF>%sender_prefix%%sender_altname%%sender_suffix% <#9b94a6>replied to ticket <#F0F8FF>%id%<#9b94a6>: <#F0F8FF>%message%` |
| `ticket.list-header` | `%prefix%<#1E90FF>Your tickets:` |
| `ticket.list-entry` | `<#9b94a6> - [<#F0F8FF>%id%<#9b94a6>] <#F0F8FF>%status% <#9b94a6>- %message%` |
| `ticket.no-tickets` | `%prefix%<#9b94a6>You have no tickets.` |
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
| `gui.tickets.title` | `<#1E90FF>Support Tickets` |
| `gui.tickets.title-view` | `<#1E90FF>Ticket <#F0F8FF>%id%` |
| `gui.tickets.prev-page` | `<#9b94a6>← Previous Page` |
| `gui.tickets.next-page` | `<#9b94a6>Next Page →` |
| `gui.tickets.filter` | `<#9b94a6>Filter: <#F0F8FF>%filter%` |
| `gui.tickets.back` | `<#9b94a6>← Back` |
| `gui.tickets.close-ticket` | `<#FF6B6B>Close Ticket` |
| `gui.tickets.already-closed` | `<#9b94a6>Ticket Closed` |
| `gui.tickets.reply-hint` | `<#9b94a6>Use <#F0F8FF>/tickets reply %id% <message><#9b94a6> to reply.` |
| `gui.tickets.open-name` | `<#3DDC97>Ticket #%id%` |
| `gui.tickets.closed-name` | `<#9b94a6>Ticket #%id%` |
| `gui.tickets.lore-author` | `<#9b94a6>Author: <#F0F8FF>%author%` |
| `gui.tickets.lore-message` | `<#9b94a6>Message: <#F0F8FF>%message%` |
| `gui.tickets.lore-date` | `<#9b94a6>Date: <#F0F8FF>%date%` |
| `gui.tickets.lore-status` | `<#9b94a6>Status: <#F0F8FF>%status%` |
| `gui.tickets.lore-replies` | `<#9b94a6>Replies: <#F0F8FF>%count%` |
| `gui.tickets.reply-name` | `<#1E90FF>Reply by <#F0F8FF>%staff%` |
| `gui.tickets.reply-lore-message` | `<#9b94a6>%message%` |
| `gui.tickets.reply-lore-date` | `<#9b94a6>Date: <#F0F8FF>%date%` |
| `gui.tickets.info-name` | `<#1E90FF>Ticket Info` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
