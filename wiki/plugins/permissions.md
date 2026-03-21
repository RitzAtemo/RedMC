# Permissions Plugin

Manages group and player permissions with weight-based resolution, group inheritance, and wildcard expansion.

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
│   │   ├── inherits
│   │   │   ├── create <parent>
│   │   │   ├── read
│   │   │   └── delete <parent>
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
| `redmc.permissions.group` | op |
| `redmc.permissions.group.create` | op |
| `redmc.permissions.group.read` | op |
| `redmc.permissions.group.update` | op |
| `redmc.permissions.group.update.name` | op |
| `redmc.permissions.group.update.permissions` | op |
| `redmc.permissions.group.update.permissions.read` | op |
| `redmc.permissions.group.update.permissions.create` | op |
| `redmc.permissions.group.update.permissions.delete` | op |
| `redmc.permissions.group.update.inherits` | op |
| `redmc.permissions.group.update.inherits.read` | op |
| `redmc.permissions.group.update.inherits.create` | op |
| `redmc.permissions.group.update.inherits.delete` | op |
| `redmc.permissions.group.delete` | op |
| `redmc.permissions.player` | op |
| `redmc.permissions.player.permissions` | op |
| `redmc.permissions.player.permissions.read` | op |
| `redmc.permissions.player.permissions.create` | op |
| `redmc.permissions.player.permissions.delete` | op |
| `redmc.permissions.player.groups` | op |
| `redmc.permissions.player.groups.read` | op |
| `redmc.permissions.player.groups.create` | op |
| `redmc.permissions.player.groups.delete` | op |
| `redmc.permissions.reload` | op |

## Configuration

| Key | Default | Description |
|---|---|---|
| `default-group` | `default` | ID of the group automatically assigned to new players |

## Groups

Five predefined groups with MiniMessage hex colors and a fantasy theme:

| ID | Display Name | Color | Inherits |
|---|---|---|---|
| `default` | Странник | `#9b94a6` | — |
| `adventurer` | Авантюрист | `#3DDC97` | default |
| `knight` | Рыцарь | `#FFB800` | adventurer |
| `guardian` | Страж | `#FF6B6B` | knight |
| `archon` | Архонт | `#C780FA` | — |

The `default` group has `* false` (deny all) as its first permission entry, then explicit grants for basic features. Each higher group inherits its parent's permissions and adds its own.

`archon` is a standalone admin group with only a prefix permission — no redmc.* permissions of its own (managed per-player as needed).

## Resolution Logic

`PermissionResolver.resolve(PlayerData, groups)`:

1. For each group the player belongs to, recursively resolve inherited groups (depth-first, cycle-safe via visited set). Child permissions override parent permissions when weights are equal — parents are resolved first, children applied on top.
2. Apply player-specific permissions on top.
3. When two entries share the same name, the one with the higher `weight` wins.
4. Expand wildcard entries (`*`, `prefix.*`) against all permissions registered in `Bukkit.getPluginManager().getPermissions()`. Wildcards are sorted longest-first so more specific patterns take precedence.
5. Non-wildcard entries are added directly to the final map.

Parent nodes (e.g. `redmc.chat` for `redmc.chat.global`) are **not** auto-granted. They must be declared explicitly in `groups.yml` where needed.

## Data Storage

`groups.yml` — group definitions:
```yaml
groups:
  <id>:
    displayName: "<name>"
    inherits:
      - <parent-id>
    permissions:
      - name: <permission>
        weight: <int>
        allowed: true|false
```

`players.yml` — per-player overrides:
```yaml
players:
  <uuid>:
    name: <name>
    groups:
      - <group-id>
    permissions:
      - name: <permission>
        weight: <int>
        allowed: true|false
```

Both files are loaded fully into memory on startup and player join.

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
| `inherits-list-header` | `%prefix%<#9b94a6>Inherits:` |
| `inherits-list-item` | `<#FFB800> - <#F0F8FF>%group%` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
