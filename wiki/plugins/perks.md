# Perks Plugin

Virtual block access, item utilities, movement, broadcast, custom join/quit messages, and administrator tools.

## Command Tree

```
/perks
├── setjoin <message>
│   └── reset
├── setquit <message>
│   └── reset
└── reload
    ├── config
    ├── data
    └── all

/workbench                      (alias: /wb)
/anvil
/enchant
/grindstone
/stonecutter
/smithing
/loom
/cartography
/enderchest                     (alias: /ec)
/dispose                        (alias: /trash)

/repair
└── all

/hat
/backpack                       (alias: /bp)
/rename <name>

/fly
/speed <1-5>

/feed
/heal
/nofall

/broadcast <message>            (alias: /bc)

/invsee <player> [--as-offline]
/ecsee <player> [--as-offline]
/vanish                         (alias: /v)
/god
/freeze <player>
/tpo <player>
/tpohere <player>
/sudo <player> <command>
```

## Permission Nodes

### User Perks

| Node | Default |
|---|---|
| `redmc.perks.workbench` | op |
| `redmc.perks.anvil` | op |
| `redmc.perks.enchant` | op |
| `redmc.perks.grindstone` | op |
| `redmc.perks.stonecutter` | op |
| `redmc.perks.smithing` | op |
| `redmc.perks.loom` | op |
| `redmc.perks.cartography` | op |
| `redmc.perks.enderchest` | op |
| `redmc.perks.dispose` | op |
| `redmc.perks.repair` | op |
| `redmc.perks.repair.all` | op |
| `redmc.perks.hat` | op |
| `redmc.perks.backpack` | op |
| `redmc.perks.rename` | op |
| `redmc.perks.fly` | op |
| `redmc.perks.speed` | op |
| `redmc.perks.feed` | op |
| `redmc.perks.heal` | op |
| `redmc.perks.nofall` | op |
| `redmc.perks.broadcast` | op |
| `redmc.perks.setjoin` | op |
| `redmc.perks.setquit` | op |
| `redmc.perks.reload` | op |
| `redmc.perks.reload.config` | op |
| `redmc.perks.reload.data` | op |
| `redmc.perks.reload.all` | op |

### Admin Perks

| Node | Default |
|---|---|
| `redmc.perks.admin.invsee` | op |
| `redmc.perks.admin.ecsee` | op |
| `redmc.perks.admin.vanish` | op |
| `redmc.perks.admin.god` | op |
| `redmc.perks.admin.freeze` | op |
| `redmc.perks.admin.tpo` | op |
| `redmc.perks.admin.tpohere` | op |
| `redmc.perks.admin.sudo` | op |

## Virtual Blocks

Open any crafting interface anywhere without a physical block nearby. Each command opens the corresponding GUI:

| Command | Interface |
|---|---|
| `/workbench` | Crafting table (3×3) |
| `/anvil` | Anvil (rename, repair, enchant books) |
| `/enchant` | Enchanting table |
| `/grindstone` | Grindstone (remove enchantments) |
| `/stonecutter` | Stonecutter |
| `/smithing` | Smithing table (trim, upgrade) |
| `/loom` | Loom (banner patterns) |
| `/cartography` | Cartography table |
| `/enderchest` | Own ender chest |
| `/dispose` | 27-slot trash bin (contents cleared on close) |

All virtual block commands support individual cooldowns configured under `cooldowns.*` in `config.yml`. Setting a value to `0` disables the cooldown.

## Item Utilities

### `/repair [all]`

- `/repair` — sets damage to 0 on the item in the main hand. No-ops silently if the item has no `Damageable` meta or is already at full durability.
- `/repair all` — iterates the full inventory and repairs every damageable item. Reports the number of items repaired.

Both have separate cooldown keys: `repair` and `repair-all`.

### `/hat`

Swaps the item in the main hand into the helmet slot. The current helmet (if any) moves to the main hand. Works with any item type, not just blocks.

### `/backpack`

Opens a personal inventory (size configured in `backpack.size`, default `54`) stored per player UUID in `perks_players.yml`. Contents persist across sessions and server restarts. Saves automatically on inventory close via `InventoryCloseEvent`. The data file is also saved on plugin disable.

`BackpackHolder` is the custom `InventoryHolder` used to identify the inventory in close events.

### `/rename <name>`

Renames the item in the main hand. The `<name>` argument supports full MiniMessage formatting including gradients and decorations, e.g. `/rename <gradient:#FF1493:#1E90FF>Legendary Sword</gradient>`.

## Movement

### `/fly`

Toggles survival flight via `player.setAllowFlight()`. Tracked per session in `FlyManager`. On player quit, flight is disabled and the player is removed from the tracked set.

### `/speed <1-5>`

Sets walk and fly speed simultaneously. Mapping:

| Level | Walk speed | Fly speed |
|---|---|---|
| 1 | 0.2 (default) | 0.1 |
| 2 | 0.4 | 0.2 |
| 3 | 0.6 | 0.3 |
| 4 | 0.8 | 0.4 |
| 5 | 1.0 | 0.5 |

## Quality of Life

### `/feed`

Sets food level to 20 and saturation to 20. Subject to `cooldowns.feed`.

### `/heal`

Restores health to the player's max health attribute value and fully repairs all equipped armor pieces. Subject to `cooldowns.heal`.

### `/nofall`

Toggles fall damage immunity. Tracked in `NoFallManager`. `EntityDamageEvent` with cause `FALL` is cancelled when the player is in the protected set. Removed on player quit.

## Broadcast

### `/broadcast <message>`

Broadcasts a formatted message to all online players. Format is defined by `broadcast.format` in the locale file using `%player%` and `%message%` placeholders. Subject to `cooldowns.broadcast` (default 300 seconds).

## Custom Join / Quit Messages

Players with `redmc.perks.setjoin` / `redmc.perks.setquit` can set their own join and quit messages.

- `/perks setjoin <message>` — sets a custom join message supporting MiniMessage and `%player%`
- `/perks setjoin reset` — reverts to the default message set by the Chat plugin
- `/perks setquit <message>` / `reset` — same for quit

Messages are stored per player UUID in `perks_players.yml`. The override is applied in a `HIGHEST` priority `PlayerJoinEvent` / `PlayerQuitEvent` listener, so it runs after normal listeners (including Chat plugin).

## Admin Tools

### `/invsee <player> [--as-offline]`

Opens the target player's inventory.

- **Online player** — opens the live `PlayerInventory` directly; changes take effect immediately for the target.
- **Offline player** — reads `world/playerdata/<uuid>.dat` via NMS NBT, displays a 54-slot snapshot (hotbar rows 0–8, main rows 9–35, armor slots 36–39, offhand slot 40). The snapshot is read-only: changes are not saved back.
- **`--as-offline` flag** — forces NBT reading even when the target is online. Useful for inspecting the last saved state on disk (e.g. before an unsaved session is flushed). The result is always a read-only snapshot.

Tab-completion includes both online and offline players.

### `/ecsee <player> [--as-offline]`

Opens the target player's ender chest.

- **Online player** — opens the live ender chest; changes take effect immediately.
- **Offline player** — reads `EnderItems` from `world/playerdata/<uuid>.dat`, displays a 27-slot snapshot. Read-only; changes are not saved back.
- **`--as-offline` flag** — forces NBT reading even when the target is online. Always produces a read-only snapshot.

Tab-completion includes both online and offline players.

### `/vanish`

Toggles invisibility. When vanished:

- The player is hidden from all currently online players via `Player.hidePlayer()`
- The join message is suppressed (`event.joinMessage(null)`)
- The quit message is suppressed (`event.quitMessage(null)`)
- Players who join while the admin is vanished automatically cannot see them (handled in `PlayerJoinEvent`)
- State is in-memory only; resets on server restart or reconnect

### `/god`

Toggles god mode. All `EntityDamageEvent` instances targeting the player are cancelled. Tracked in `GodManager`, removed on player quit.

### `/freeze <player>`

Toggles movement freeze on a target player. Frozen players cannot change their block position (camera rotation is still allowed). Both the admin and the target receive feedback messages. Implemented via `PlayerMoveEvent` with an early-exit when the frozen set is empty. Removed on target quit.

### `/tpo <player>` / `/tpohere <player>`

Admin teleportation without a request flow.

- `/tpo <player>` — teleports the admin to the target
- `/tpohere <player>` — teleports the target to the admin; the target receives a notification message

Both use `player.teleportAsync()` for Folia cross-region compatibility. The success message is sent in the async completion callback.

### `/sudo <player> <command>`

Makes the target player execute a command. The command string is passed to `player.performCommand()` scheduled on the target's region via `RegionScheduler` for Folia thread-safety.

## Configuration

`config.yml` keys:

```yaml
cooldowns:
  workbench: 0        # seconds; 0 = no cooldown
  anvil: 0
  enchant: 0
  grindstone: 0
  stonecutter: 0
  smithing: 0
  loom: 0
  cartography: 0
  enderchest: 0
  dispose: 0
  repair: 30
  repair-all: 60
  hat: 0
  backpack: 0
  rename: 10
  fly: 0
  speed: 0
  feed: 60
  heal: 120
  nofall: 0
  broadcast: 300

backpack:
  size: 54             # must be a multiple of 9, max 54
```

## Data Storage

Player data is persisted in `perks_players.yml`:

```yaml
players:
  <uuid>:
    join-message: "<#3DDC97>%player% joined!"   # optional
    quit-message: "<#FF6B6B>%player% left."     # optional
    backpack:
      0: <ItemStack>   # slot index → serialized item
      1: <ItemStack>
      ...
```

`PerksDataStorage` uses `ConcurrentHashMap` internally. Data is loaded on `onEnable`, saved on `onDisable`, and can be reloaded with `/perks reload data`.
