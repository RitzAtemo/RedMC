# Teleport Plugin

Spawn management, warps, homes, back stack, random teleport, and player teleport requests.

## Command Tree

```
/teleport
└── reload
    ├── config
    ├── data
    └── all

/spawn
/setspawn
└── newbie

/warp [name]
├── create <name>
├── delete <name>
└── list

/home [name]
├── set [name]
├── delete <name>
└── list

/back

/rtp [world]

/tpa <player>
/tpahere <player>
/tpaccept
/tpdeny
/tpcancel
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.teleport.spawn` | op |
| `redmc.teleport.spawn.set` | op |
| `redmc.teleport.spawn.set.newbie` | op |
| `redmc.teleport.warp` | op |
| `redmc.teleport.warp.create` | op |
| `redmc.teleport.warp.delete` | op |
| `redmc.teleport.home` | op |
| `redmc.teleport.home.set` | op |
| `redmc.teleport.home.delete` | op |
| `redmc.teleport.back` | op |
| `redmc.teleport.rtp` | op |
| `redmc.teleport.tpa` | op |
| `redmc.teleport.tpa.here` | op |
| `redmc.teleport.tpa.accept` | op |
| `redmc.teleport.tpa.deny` | op |
| `redmc.teleport.tpa.cancel` | op |
| `redmc.teleport.reload.config/data/all` | op |
| `redmc.teleport.homes.bypass` | op |
| `redmc.teleport.back.bypass` | op |
| `redmc.teleport.rtp.bypass` | op |

### Numeric Limits via Permissions

Three features accept a numeric suffix `N` on the permission node. The plugin reads all effective permissions and uses the highest `N`. Players with the corresponding `.bypass` permission get unlimited access (`-1`). A player without any matching node gets `0` (feature locked).

| Node pattern | Controls |
|---|---|
| `redmc.teleport.homes.N` | Maximum number of homes |
| `redmc.teleport.back.N` | `/back` uses per `back.cooldown-interval` |
| `redmc.teleport.rtp.N` | `/rtp` uses per `rtp.cooldown-interval` |

| Bypass node | Effect |
|---|---|
| `redmc.teleport.homes.bypass.limit` | Unlimited homes |
| `redmc.teleport.back.bypass.limit` | Unlimited `/back` uses (no count cap) |
| `redmc.teleport.back.bypass.cooldown` | `/back` count is per-session, not per-interval |
| `redmc.teleport.rtp.bypass.limit` | Unlimited `/rtp` uses (no count cap) |
| `redmc.teleport.rtp.bypass.cooldown` | `/rtp` count is per-session, not per-interval |

Without any `.N` permission and without `.bypass.limit`, a player who has the base feature permission gets a default limit of **1** per interval/session.

## Spawn

`/setspawn` and `/setspawn newbie` save locations to `spawns.yml`. On `PlayerRespawnEvent`, `SpawnManager` overrides the respawn location when `spawn.override-original: true`. The newbie spawn is applied once on first join (`hasPlayedBefore() == false`). The teleport is scheduled via `getGlobalRegionScheduler().runDelayed()` with a 1-tick delay to avoid a Folia `Player is already removed from player chunk loader` error that occurs when `teleportAsync` is called directly inside `PlayerJoinEvent`.

## Warps

Named server-wide teleport points stored in `warps.yml`. `/warp <name>` teleports, `/warp` or `/warp list` shows all available warps.

## Homes

Per-player named locations stored in `homes.yml` keyed by UUID. Loaded on `PlayerJoinEvent`, saved on `PlayerQuitEvent`. The default home name is `home`. Limit enforced by `redmc.teleport.homes.N`.

## Back

`BackManager` maintains a per-session `Deque<Location>` per player. The current location is pushed before every plugin-initiated teleport. On `PlayerDeathEvent` (if `back.include-death: true`) the death location is pushed. `/back` pops the top entry. Stack size is capped at `back.stack-size`. Uses are counted per interval and checked against `redmc.teleport.back.N`.

## Random Teleport

`RtpManager.findLocation()` picks a random point in a ring between `rtp.min-distance` and `rtp.max-distance` from origin. Chunk is loaded via `world.getChunkAtAsync()` so `getHighestBlockYAt()` and block material checks run on the correct region thread. Retries up to `rtp.max-attempts` times. Uses are tracked per interval (`rtp.cooldown-interval` seconds) via `redmc.teleport.rtp.N`. Counter resets when the interval expires. `redmc.teleport.rtp.bypass` skips the check entirely.

## Teleport Requests

`TpaManager` holds two maps: `pendingByTarget` (incoming request to accept/deny) and `outgoingByRequester` (for cancel). A new outgoing request replaces the previous one from the same requester. Timeout is scheduled with `AsyncScheduler.runDelayed`. Cooldown between requests is enforced via `tpa.cooldown`.

| Command | Who moves |
|---|---|
| `/tpa <player>` | Sender → target |
| `/tpahere <player>` | Target → sender |

## Configuration

| Key | Default | Description |
|---|---|---|
| `spawn.override-original` | `true` | Override respawn location with the configured spawn |
| `spawn.newbie-spawn-enabled` | `true` | Teleport first-time players to the newbie spawn instead of main spawn |
| `back.cooldown-interval` | `3600` | Seconds in a period for `/back` use counting |
| `back.stack-size` | `10` | Maximum locations kept in the back stack per player |
| `back.include-death` | `true` | Push death location to the back stack |
| `rtp.cooldown-interval` | `3600` | Seconds in a period for `/rtp` use counting |
| `rtp.min-distance` | `500` | Minimum distance from origin for RTP landing |
| `rtp.max-distance` | `5000` | Maximum distance from origin for RTP landing |
| `rtp.max-attempts` | `30` | Maximum retry attempts to find a safe surface |
| `rtp.worlds` | `[world]` | List of world names where `/rtp` is allowed |
| `tpa.timeout` | `60` | Seconds before an unaccepted TPA request expires |
| `tpa.cooldown` | `30` | Seconds between TPA requests from the same sender |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Teleport<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>Only players can use this command.` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player not found.` |
| `spawn.set-success` | `%prefix%<#3DDC97>Spawn has been set.` |
| `spawn.set-newbie-success` | `%prefix%<#3DDC97>Newbie spawn has been set.` |
| `spawn.not-set` | `%prefix%<#FF6B6B>Spawn has not been set yet.` |
| `spawn.teleport-success` | `%prefix%<#3DDC97>Teleported to spawn.` |
| `warp.create-success` | `%prefix%<#3DDC97>Warp <#F0F8FF>%warp%<#3DDC97> has been created.` |
| `warp.create-exists` | `%prefix%<#FF6B6B>A warp with that name already exists.` |
| `warp.delete-success` | `%prefix%<#3DDC97>Warp has been deleted.` |
| `warp.delete-not-found` | `%prefix%<#FF6B6B>Warp not found.` |
| `warp.teleport-success` | `%prefix%<#3DDC97>Teleported to warp.` |
| `warp.teleport-not-found` | `%prefix%<#FF6B6B>Warp not found.` |
| `warp.list-header` | `%prefix%<#1E90FF>Available warps:` |
| `warp.list-entry` | `<#9b94a6> - <#F0F8FF>%warp%` |
| `warp.list-empty` | `%prefix%<#9b94a6>No warps have been created yet.` |
| `home.set-success` | `%prefix%<#3DDC97>Home <#F0F8FF>%home%<#3DDC97> has been set.` |
| `home.set-limit` | `%prefix%<#FF6B6B>You have reached your home limit of <#F0F8FF>%limit%<#FF6B6B>.` |
| `home.delete-success` | `%prefix%<#3DDC97>Home has been deleted.` |
| `home.delete-not-found` | `%prefix%<#FF6B6B>Home not found.` |
| `home.teleport-success` | `%prefix%<#3DDC97>Teleported to home.` |
| `home.teleport-not-found` | `%prefix%<#FF6B6B>Home not found.` |
| `home.list-header` | `%prefix%<#1E90FF>Your homes:` |
| `home.list-entry` | `<#9b94a6> - <#F0F8FF>%home%` |
| `home.list-empty` | `%prefix%<#9b94a6>You have no homes set.` |
| `back.success` | `%prefix%<#3DDC97>Teleported back to your previous location.` |
| `back.no-location` | `%prefix%<#FF6B6B>No previous location to return to.` |
| `back.limit-reached` | `%prefix%<#FF6B6B>You have reached your /back usage limit for this period.` |
| `rtp.searching` | `%prefix%<#FFB800>Searching for a safe location...` |
| `rtp.success` | `%prefix%<#3DDC97>Teleported to a random location.` |
| `rtp.failed` | `%prefix%<#FF6B6B>Could not find a safe location. Please try again.` |
| `rtp.limit-reached` | `%prefix%<#FF6B6B>You have reached your random teleport limit for this session.` |
| `tpa.request-sent` | `%prefix%<#3DDC97>Teleport request sent to <#F0F8FF>%player%<#3DDC97>.` |
| `tpa.request-received` | `%prefix%<#FFB800>%player%<#F0F8FF> has sent you a teleport request. Use <#3DDC97>/tpaccept<#F0F8FF> or <#FF6B6B>/tpdeny<#F0F8FF>.` |
| `tpa.request-here-sent` | `%prefix%<#3DDC97>Teleport request sent to <#F0F8FF>%player%<#3DDC97>.` |
| `tpa.request-here-received` | `%prefix%<#FFB800>%player%<#F0F8FF> wants you to teleport to them. Use <#3DDC97>/tpaccept<#F0F8FF> or <#FF6B6B>/tpdeny<#F0F8FF>.` |
| `tpa.accept-success` | `%prefix%<#3DDC97>Teleport request accepted.` |
| `tpa.deny-success` | `%prefix%<#3DDC97>Teleport request denied.` |
| `tpa.cancel-success` | `%prefix%<#3DDC97>Teleport request cancelled.` |
| `tpa.no-request` | `%prefix%<#FF6B6B>You have no pending teleport request.` |
| `tpa.request-expired` | `%prefix%<#FF6B6B>Your teleport request has expired.` |
| `tpa.request-accepted` | `%prefix%<#3DDC97>%player%<#3DDC97> accepted your teleport request.` |
| `tpa.request-denied` | `%prefix%<#FF6B6B>%player%<#FF6B6B> denied your teleport request.` |
| `tpa.cooldown` | `%prefix%<#FF6B6B>You must wait before sending another teleport request.` |
| `tpa.cannot-self` | `%prefix%<#FF6B6B>You cannot send a teleport request to yourself.` |
| `tpa.teleport-success` | `%prefix%<#3DDC97>Teleported to <#F0F8FF>%player%<#3DDC97>.` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |

## Data Storage

`TeleportDataStorage` reads and writes `warps.yml`, `homes.yml`, and `spawns.yml` directly via `YamlConfiguration`, independent of `ConfigManager`. These files are not subject to the config-version backup mechanism.
