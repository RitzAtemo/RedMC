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
  - Text utilities
  - Configuration manager
  - Localization manager
  - Update protection
- Placeholders
  - Default values
  - Registry API
- Permissions
  - Group permissions
  - Player permissions
  - Weights
  - Inheritance
- Vault
  - Prefixes
  - Postfixes
  - Alt names
  - Economics
- MOTD
  - Random or sequential templates
  - Favicon rotation
  - Player sample customization
  - Version string override
  - Ping logging
- Tab
  - Placeholders
  - Animations
- Scoreboard
  - Placeholders
  - Animations
- Chat events
  - Joins, disconnects
  - Local and global chat
  - Whispers
  - Replies
  - Deaths
  - Cycle alerts
  - Advancements
- NPC
  - Names
  - Skins
  - Commands
  - Equipment
- Teleport events
  - Random Teleport
  - Spawns
  - Respawns
  - Warps
  - Homes
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