# Project Structure

## Overview

RedMC is a Gradle multi-module project targeting **Folia 1.21.11** with **Java 21**.

```
RedMC/
├── build.gradle.kts          # Root build: runFolia task
├── settings.gradle.kts       # Module declarations
├── plugins/
│   ├── api/                  # Core utilities (no commands)
│   ├── permissions/          # Group and player permission management
│   ├── placeholders/         # Placeholder registry
│   ├── vault/                # Economy, prefixes, suffixes, alt names
│   ├── tab/                  # Tab list header/footer animations
│   ├── scoreboard/           # Sidebar scoreboard animations
│   ├── chat/                 # Chat, death, advancement, private messages
│   ├── npc/                  # NPC rendering via NMS packets
│   └── teleport/             # Spawn, warps, homes, back, RTP, TPA
└── Wiki/                     # This documentation
```

## Module Dependency Graph

```
api
 ├── permissions
 ├── placeholders
 │    ├── vault
 │    ├── tab
 │    ├── scoreboard
 │    └── chat
 ├── npc
 └── teleport
```

`vault` also depends on `permissions` for group resolution.

## Build Commands

```bash
./gradlew build                          # Build all plugins
./gradlew :plugins:permissions:build     # Build single plugin
./gradlew runFolia                       # Start local Folia server
./gradlew clean build                    # Clean + full build
```

## Config Version Backup

Every plugin ships a `config-version` field in `config.yml`. On startup, `ConfigManager` compares the on-disk version with the plugin version. If they differ:

1. All existing files in the data folder are archived to `backup_<old-version>_<timestamp>.zip`
2. Files are deleted
3. Defaults are recreated from the JAR

This ensures a clean migration without losing the old configuration.

## Each Plugin's Resource Layout

```
src/main/resources/
├── paper-plugin.yml       # Metadata, dependencies, permissions
├── config.yml             # Settings (always has config-version)
└── lang/
    ├── en_US.yml          # English messages
    └── ru_RU.yml          # Russian messages
```
