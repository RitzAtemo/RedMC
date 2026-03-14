# RedMC Infrastructure
Plugin infrastructure for Minecraft servers in the Vanilla Survival Multiplayer (SMP) category, with a focus on the Folia core.

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Folia 1.21.11](https://img.shields.io/badge/Folia-1.21.11-yellow.svg)
![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-green.svg)

## Features

- API
  - Text utilities
  - Configuration manager
  - Localization manager
- Permissions
  - Group permissions
  - Player permissions
  - Weights
  - Inheritance
- Placeholders system

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

- Backup
- Vault
  - Prefixes
  - Postfixes
  - Alt names
  - Economics
- Chat events
  - Joins, disconnects
  - Local and global chat
  - Whispers
  - Replies
  - Deaths
- Teleport events
  - Random Teleport
  - Spawns
  - Respawns
  - Warps
  - Homes
- Tab
- Scoreboard
- MOTD
- Holograms
- Menus
- Friends
- Clans
- Jobs
- Auctions
- Shops
- Regions (rent chunk)

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