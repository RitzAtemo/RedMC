# Permissions Plugin

Manages group and player permissions with weight-based resolution.

## Command Tree

```
/permissions (alias: /pm)
├── reload
│   ├── config
│   ├── datasource
│   └── all
├── group
│   ├── create <group>
│   ├── read
│   ├── update <group>
│   │   ├── permissions
│   │   │   ├── create <permission> <weight> <allowed>
│   │   │   ├── read
│   │   │   └── delete <permission>
│   │   └── name <name>
│   └── delete <group>
└── player
    └── <player>
        ├── permissions
        │   ├── create <permission> <weight> <allowed>
        │   ├── read
        │   └── delete <permission>
        └── groups
            ├── create <group>
            ├── read
            └── delete <group>
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.permissions` | op |
| `redmc.permissions.group.create` | op |
| `redmc.permissions.group.read` | op |
| `redmc.permissions.group.update.name` | op |
| `redmc.permissions.group.update.permissions.read` | op |
| `redmc.permissions.group.update.permissions.create` | op |
| `redmc.permissions.group.update.permissions.delete` | op |
| `redmc.permissions.group.delete` | op |
| `redmc.permissions.player.permissions.read` | op |
| `redmc.permissions.player.permissions.create` | op |
| `redmc.permissions.player.permissions.delete` | op |
| `redmc.permissions.player.groups.read` | op |
| `redmc.permissions.player.groups.create` | op |
| `redmc.permissions.player.groups.delete` | op |
| `redmc.permissions.reload` | op |

## Resolution Logic

`PermissionResolver.resolve(PlayerData, groups)`:

1. Collect all permissions from all groups the player belongs to
2. Apply player-specific permissions on top (higher priority)
3. When two entries share the same name, the one with the higher `weight` wins

Data is stored in YAML files and loaded into memory on startup and player join.
