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

`/setspawn` and `/setspawn newbie` save locations to `spawns.yml`. On `PlayerRespawnEvent`, `SpawnManager` overrides the respawn location when `spawn.override-original: true`. The newbie spawn is applied once on first join (`hasPlayedBefore() == false`) via `player.teleportAsync()`.

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

## Data Storage

`TeleportDataStorage` reads and writes `warps.yml`, `homes.yml`, and `spawns.yml` directly via `YamlConfiguration`, independent of `ConfigManager`. These files are not subject to the config-version backup mechanism.
