# RedMC Infrastructure
Plugin infrastructure for Minecraft servers in the Vanilla Survival Multiplayer (SMP) category, with a focus on the Folia core.

![Java 21](https://img.shields.io/badge/Java-21-1E90FF.svg)
![Folia 1.21.11](https://img.shields.io/badge/Folia-1.21.11-FF1493.svg)
![RedMC: 0.0.1-alpha](https://img.shields.io/badge/RedMC-0.0.1--alpha-C780FA.svg)
![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-3DDC97.svg)

## Documentation

Full documentation is available in the [Wiki](wiki/README.md), including plugin descriptions, command trees, and permission tables.

## Features

- API
  - MiniMessage text formatting with hex color and placeholder substitution
  - Configuration manager with YAML wrapping and automatic config-version backup (ZIP old files, recreate defaults)
  - Localization manager with per-player locale detection and configurable fallback language
  - Broadcast, player message, command sender, and console send utilities
- Placeholders
  - Priority-sorted registry: multiple plugins register their own `PlaceholderRegistry` with a numeric priority; higher wins on key conflict
  - Token parsing via configurable regex pattern (default `##Key##`) with per-player context resolution
  - Built-in general placeholders: player name, world, coordinates
- Permissions
  - Named permission groups with display names and weighted `PermissionEntry` lists
  - Per-player group membership and player-specific permission overrides
  - Weight-based conflict resolution: higher weight wins; player-level entries always take precedence
  - Permissions applied via `PermissionAttachment` on join and reloaded on demand
- Vault
  - Multi-currency economy: up to any number of currencies, each with display name, symbol, and starting balance
  - Optional rank tiers per currency (e.g., "Unemployed" at 0, "Millionaire" at 1 000 000)
  - Per-player balance storage in YAML; balance transfer between players
  - Leaderboard (`/baltop`) and personal balance check (`/mybalance`)
  - Prefix, suffix, and alt name management per group and per player
- MOTD
  - Random or sequential MOTD templates with MiniMessage formatting
  - Favicon rotation from a configurable images folder
  - Player sample list customization (names shown on server hover)
  - Version string override (protocol version label)
  - Ping logging to console
- Tab
  - Header and footer rendered with full placeholder support
  - Frame-based animations: each frame has its own text and dwell interval in ticks
  - Async scheduler ticking every 50 ms; frames advance when their interval is reached
  - Player row display name formatting with placeholders
- Scoreboard
  - Per-player FastBoard sidebar with configurable static lines and placeholder tokens
  - Animated title: frame-based with per-frame dwell intervals, same 50 ms async tick loop as tab
  - Per-player visibility toggle (`/scoreboard toggle`) persisted to YAML across reconnects
- Chat events
  - Join and leave messages with configurable locale templates
  - Local chat: message delivered only within a configurable radius (per-world, disable with `-1`)
  - Global chat: optional prefix (default `!`) broadcasts to all players regardless of distance
  - Private messages with `/msg`; `/reply` replies to the last player who messaged you
  - Death messages split into 14 cause groups; entity and weapon names translated per receiver's locale
  - Advancement announcements with configurable disable flag; custom announcement templates
  - Scheduled broadcast alerts: configurable interval, random selection with a no-repeat buffer
- NPC
  - Packet-based rendering via NMS — no actual entity spawned on the server
  - Custom display names with full MiniMessage formatting
  - Skin applied from any online player name (texture + signature fetched and stored)
  - All 6 equipment slots (helmet, chestplate, leggings, boots, main hand, off hand) configurable per NPC
  - Left-click and right-click command lists per NPC; commands run as CONSOLE or as the clicking PLAYER; supports `{player}`, `{uuid}`, `{world}`, `{x}`, `{y}`, `{z}` tokens
  - Optional look-at-player task: rotates NPC head toward the nearest player within a configurable range on a configurable interval
  - 500 ms interaction cooldown to prevent command spam
- Teleport events
  - Random Teleport — async safe-location finder: configurable min/max radius per world, up to 30 attempts, validates solid ground and non-suffocating space; per-interval use limit and cooldown
  - Spawns — separate main spawn and newbie spawn locations; optional vanilla respawn override; newbie spawn applied on first join or when configured
  - Respawns — on death, player can be sent to main spawn or newbie spawn based on config
  - Warps — admin-managed global destinations; CRUD commands (`create`, `delete`, `list`, `go`); stored in YAML
  - Homes — per-player named home list with configurable limit; `set`, `delete`, `list`, `go` commands; loaded on join, saved on quit
  - TPA — `/tpa <player>` and `/tpa here <player>` send teleport requests; `/tpaccept`, `/tpdeny`, `/tpcancel` to respond; configurable request timeout and cooldown
  - Back — returns to the last location before a teleport or death; configurable history stack depth and per-interval use limit
- Perks
  - Virtual crafting table, anvil, enchanting table, grindstone, stonecutter, smithing table, loom, cartography table, ender chest, trash bin
  - Item repair (single / all inventory)
  - Hat (wear any item as helmet)
  - Personal backpack (persistent, configurable size)
  - Item renaming with MiniMessage formatting
  - Flight toggle
  - Walk/fly speed levels (1–5)
  - Feed and heal
  - Fall damage protection toggle
  - Server-wide broadcast with cooldown
  - Custom join/quit messages per player
- Cosmetics
  - 11 placement slots: trail, head, back, feet, orbit, aura, wings, crown, halo, shoulders
  - 10 shape patterns: point, ring, sphere, spiral, double helix, star, wings, crown, halo, random
  - Full Bukkit Particle enum support including colored and gradient dust
  - Multi-layer templates with per-layer particle type, shape, tick rate, and color
  - 13 built-in premade templates
  - Template editor via commands with live tab-completion
  - Import / export via encrypted chat signature (GZIP + Base64)
- Holograms
  - Multi-line text display entities
  - MiniMessage formatting per line
  - Placeholder registry support with configurable refresh rate
  - CRUD management via commands

## Installation

Step-by-step instructions to install the plugin:
1. Download the latest release from the [Releases](https://github.com/RitzAtemo/RedMC/releases/) page
2. Place the `.jar` files into your server's `plugins` folder
3. Restart the server

## Configuration

All plugin settings are configured in their respective `config.yml` files. Messages can be customized in the corresponding `lang` folders, named according to the language (e.g., `en_US.yml`), which are automatically used based on the client’s language.

## Build
This project uses **Gradle** as its build system. To build all plugins, run:

```bash
./gradlew build
```

This will compile the source code, run the necessary tasks, and produce `.jar` files in the `build/libs/` directory.

> If you are using Windows, use `gradlew.bat build` instead of `./gradlew build`.

## Test
To debug the plugins with Folia, use:

```bash
./gradlew runFolia
```

This will start a local Folia server instance with your plugins loaded, allowing you to test features in a real server environment.

> Tip: You can modify the run configuration in `build.gradle` if you want to change server version or plugin paths.

## Roadmap

- Custom menus
- Friends
- Auctions
- Shops (dynamic price, stock auction slots)
- Regions (rent chunk)
- Custom enchants
- Entity frames
- Custom mobs (entity frames integration)
- Custom difficulty (entity frames integration)
- Custom items
- Event system (run console commands on event invoking, give item for example)
- Jobs
- Clans
- Season system (with battlepass and roadmap system)
- Quests (with npc, events and season linking)
- Skills

And of course - CI/CD.

## Contributing

Pull requests, suggestions, and feedback are welcome!

To contribute:

1. **Fork** this repository
2. Create a new branch for your improvement or fix
3. Write your code and commit the changes
4. Open a **pull request**

## License

This project is licensed under the [AGPL-3.0 License](LICENSE).  
Copyright © Ritz Atemo, aviora.red

## Acknowledgements

- Inspired by the Folia core project