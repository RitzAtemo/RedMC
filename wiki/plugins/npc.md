# NPC Plugin

Packet-based NPC rendering using NMS. NPCs are fake players rendered via Brigadier commands and stored in YAML.

## Command Tree

```
/npc
├── create <id>
├── read
├── delete <id>
├── reload
│   ├── config
│   ├── data
│   └── all
└── update <id>
    ├── name <name>
    ├── skin <player>
    ├── teleport <player>
    ├── equipment <slot> <item>
    └── commands
        ├── leftclick
        │   ├── add <type> <command>
        │   └── clear
        └── rightclick
            ├── add <type> <command>
            └── clear
```

Equipment slots: `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`

Command types: `console` (dispatched as console), `player` (executed as the clicking player)

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.npc` | op |
| `redmc.npc.create` | op |
| `redmc.npc.read` | op |
| `redmc.npc.delete` | op |
| `redmc.npc.reload` | op |
| `redmc.npc.reload.config` | op |
| `redmc.npc.reload.data` | op |
| `redmc.npc.reload.all` | op |
| `redmc.npc.update.name` | op |
| `redmc.npc.update.skin` | op |
| `redmc.npc.update.teleport` | op |
| `redmc.npc.update.equipment` | op |
| `redmc.npc.update.commands` | op |

## Look-at-Player

An async task checks nearby players every `look-at-player.interval-ms` ms and rotates NPCs toward the nearest player within `look-at-player.range` blocks.

## Command Placeholders

Commands support: `{player}`, `{uuid}`, `{world}`, `{x}`, `{y}`, `{z}`

## Configuration

| Key | Default | Description |
|---|---|---|
| `look-at-player.enabled` | `true` | Enable NPC rotation toward nearby players |
| `look-at-player.range` | `16.0` | Detection range in blocks |
| `look-at-player.interval-ms` | `500` | Check interval in milliseconds |
| `interaction.cooldown-ms` | `500` | Minimum milliseconds between NPC interactions per player |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>NPC<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player '<#F0F8FF>%name%<#FF6B6B>' not found.` |
| `success` | `%prefix%<#3DDC97>Done.` |
| `npc-not-found` | `%prefix%<#FF6B6B>NPC with id '<#F0F8FF>%id%<#FF6B6B>' not found.` |
| `npc-already-exists` | `%prefix%<#FF6B6B>NPC with id '<#F0F8FF>%id%<#FF6B6B>' already exists.` |
| `npc-created` | `%prefix%<#3DDC97>NPC '<#F0F8FF>%id%<#3DDC97>' created at your location.` |
| `npc-deleted` | `%prefix%<#3DDC97>NPC '<#F0F8FF>%id%<#3DDC97>' deleted.` |
| `npc-name-set` | `%prefix%<#3DDC97>Display name of NPC '<#F0F8FF>%id%<#3DDC97>' set.` |
| `npc-skin-fetching` | `%prefix%<#F0E68C>Fetching skin for '<#F0F8FF>%name%<#F0E68C>', please wait...` |
| `npc-skin-set` | `%prefix%<#3DDC97>Skin of NPC '<#F0F8FF>%id%<#3DDC97>' updated from player '<#F0F8FF>%name%<#3DDC97>'.` |
| `npc-skin-error` | `%prefix%<#FF6B6B>Could not fetch skin for '<#F0F8FF>%name%<#FF6B6B>'. Player may not exist.` |
| `npc-teleported` | `%prefix%<#3DDC97>NPC '<#F0F8FF>%id%<#3DDC97>' teleported to player '<#F0F8FF>%player%<#3DDC97>'.` |
| `npc-read-header` | `%prefix%<#F0F8FF>NPCs (<#3DDC97>%count%<#F0F8FF>):` |
| `npc-read-entry` | `  <#1E90FF>- <#F0F8FF>%id% <#888888>@ %world% %.1f%x% %.1f%y% %.1f%z%` |
| `npc-command-added` | `%prefix%<#3DDC97>Command added to NPC <#F0F8FF>%id%<#3DDC97> (%click%): <#F0F8FF>%command%` |
| `npc-commands-cleared` | `%prefix%<#3DDC97>Commands cleared for NPC <#F0F8FF>%id%<#3DDC97> (%click%)` |
| `npc-equipment-set` | `%prefix%<#3DDC97>Equipment for NPC <#F0F8FF>%id%<#3DDC97> slot <#F0F8FF>%slot%<#3DDC97> set to <#F0F8FF>%item%` |
| `invalid-command-type` | `%prefix%<#FF6B6B>Invalid command type '<#F0F8FF>%type%<#FF6B6B>'. Use: console, player` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |
