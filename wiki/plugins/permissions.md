# Permissions Plugin

Manages group and player permissions with weight-based resolution.

## Command Tree

```
/permissions (alias: /pm)
├── reload
│   ├── config
│   ├── data
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

## Configuration

| Key | Default | Description |
|---|---|---|
| `default-group` | `default` | ID of the group automatically assigned to new players |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Permissions<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `success` | `%prefix%<#3DDC97>Done.` |
| `already-exists` | `%prefix%<#FF6B6B>Element <#9b94a6>%name% <#FF6B6B>already exists.` |
| `not-found` | `%prefix%<#FF6B6B>Element <#9b94a6>%name% <#FF6B6B>not found.` |
| `ensure-default-group` | `%prefix%<#FFB800>Default group not found. A new default group has been created.` |
| `empty-list` | `%prefix%<#FF6B6B>No elements found.` |
| `group-list-header` | `%prefix%<#9b94a6>Groups:` |
| `group-list-item` | `<#FFB800> - <#9b94a6>ID: <#F0F8FF>%id% <#9b94a6>\| Name: <#F0F8FF>%name%` |
| `permissions-list-header` | `%prefix%<#9b94a6>Permissions:` |
| `permissions-list-item` | `<#FFB800> - <#F0F8FF>%permission% <#9b94a6>[allowed: %allowed%, weight: %weight%]` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |

## Resolution Logic

`PermissionResolver.resolve(PlayerData, groups)`:

1. Collect all permissions from all groups the player belongs to
2. Apply player-specific permissions on top (higher priority)
3. When two entries share the same name, the one with the higher `weight` wins

Data is stored in YAML files and loaded into memory on startup and player join.
