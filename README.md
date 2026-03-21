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
- Placeholders
  - `##Key##` token resolution shared across chat, scoreboard, tab list, holograms, and MOTD
  - Built-in tokens: player name, prefix, suffix, balance, online count, server max
  - Currency-specific balance tokens per currency (`##PlayerBalance_credits##`)
  - Priority-sorted registry: multiple plugins register tokens independently without conflicts
- Permissions
  - Named groups with per-group permission entries, each carrying a weight and allow/deny flag
  - Players can belong to multiple groups simultaneously; highest weight wins on conflicts
  - Per-player permission overrides applied on top of group permissions
  - Full CRUD management for groups and player assignments via commands
- Vault
  - Multi-currency economy with configurable currencies, starting balances, and `/balance` / `/baltop`
  - Player-to-player transfers with `/pay`
  - Balance rank tiers: configurable thresholds map balance ranges to cosmetic rank labels
  - Weight-based prefix and suffix system per player and per group; alt display name support
- MOTD
  - Randomized or sequential MOTD templates with full color and formatting support
  - Favicon rotation from multiple 64×64 PNG files (random or sequential)
  - Custom hover text displayed when hovering over the player count in the server browser
  - Version string and protocol override to show a branded label regardless of client version
- Tab
  - Animated tab list header and footer with configurable per-frame interval
  - Placeholder-filled frames: prefix, player name, online count, and any `##Key##` token
  - Per-player row format with prefix and name
  - Header and footer animations are toggled and timed independently
- Scoreboard
  - Per-player animated sidebar title with configurable frame interval
  - Placeholder-filled lines: balance, name, online count, and any `##Key##` token
  - Each player's sidebar renders their own personal data context
  - Players can toggle their own scoreboard on and off with `/sb toggle`
- Chat events
  - Radius-based local chat per world and global chat with `!` prefix
  - Private messages (`/msg`, `/reply`) with quoted reply history in the current session
  - Broadcast join/leave notifications; first-join newbie announcement sent separately to all players
  - Personal welcome messages on join: one for returning players and one for first-timers, with configurable display order
  - Custom death messages by 14 cause types, advancement announcements with hover title, and scheduled rotating alert broadcasts
- NPC
  - Packet-rendered fake players with custom player skins, names, and full equipment (head, chest, legs, feet, both hands)
  - Left-click and right-click each trigger independent command lists (console-dispatched or player-executed)
  - Auto-rotation toward the nearest player within a configurable range
  - Commands support player context placeholders: `{player}`, `{uuid}`, `{world}`, `{x}`, `{y}`, `{z}`
- Teleport events
  - Named homes per player with permission-based quantity limits; `/back` stack to undo recent teleports and optionally deaths
  - Named server-wide warps with listing; `/warp list` to browse all available destinations
  - Random teleport (`/rtp`) into a configurable distance ring with safe-surface landing validation and per-interval use limits
  - Teleport requests between players (`/tpa`, `/tpahere`) with accept/deny/cancel flow, auto-timeout, and request cooldown
  - Respawn location override; dedicated newbie first-join spawn separate from the regular spawn point
- Perks
  - Virtual crafting interfaces anywhere: crafting table, anvil, enchanter, grindstone, stonecutter, smithing table, ender chest, loom, cartography table, trash bin — no physical block required
  - Item utilities: repair held item or full inventory, hat slot swap, personal 54-slot persistent backpack, item rename with full MiniMessage formatting
  - Movement perks: flight toggle, walk and fly speed levels 1–5, fall damage immunity toggle
  - Instant feed and heal; server-wide broadcast with cooldown; custom join and quit messages per player
  - Admin tools: inventory and ender chest inspection for online and offline players (via NBT), vanish, god mode, player freeze, force teleport, sudo command execution
- Cosmetics
  - 11 independent cosmetic slots — trail, head, back, feet, orbit, aura, wings, crown, halo, left and right shoulders
  - 10 particle shape patterns: point, ring, sphere, spiral, double helix, star, wings, crown, halo, and random sphere
  - Multi-layer templates: each layer has independent particle type, color and gradient, shape, count, speed, and tick rate
  - 13 premade templates — fire/frost/void trails, portal/spiral orbits, angel/devil wings, golden crown, soul halo, rainbow feet, nature head, smoke back
  - Export and import templates via encrypted signature strings; admin commands to give templates to players and reset their cosmetics
- Holograms
  - Floating multi-line text displays anchored to fixed world coordinates; line spacing is configurable
  - Full MiniMessage formatting per line including colors, gradients, and decorations
  - Real-time placeholder refresh: `##Key##` tokens update at a configurable tick interval
  - Lines can be added, edited by index, or removed in-game; changes persist to YAML
- Tracker
  - Admin real-time coordinate display: target player's X, Y, Z, and world shown in the action bar at a configurable tick interval
  - One active session per admin; starting a new session automatically replaces the previous target
  - Session ends automatically when the tracked player goes offline
  - Position saved on logout and restored on next login; bypass permission to opt out of restore

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

- Bot filter
- Custom menus
- Friends
- Mail (send items and messages to offline players)
- Party system
- Marriage
- Auctions
- Shops (dynamic price, stock auction slots)
- Chest shops (player-owned sign shops)
- Crates / keys (loot boxes)
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
- Moderation tools (warn, mute, ban, history, tickets)
- Anti-cheat
- Playtime tracking and AFK detection
- Leaderboards and player statistics
- Announcements (scheduled auto-broadcast)

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