# Vault Plugin

Economy system, player/group prefixes, suffixes, and alt names. Integrates with Permissions for group resolution.

## Command Tree

```
/vault (alias: /v)
├── reload
│   ├── config
│   ├── data
│   └── all
├── group <group>
│   ├── prefix
│   │   ├── set <weight> <prefix>
│   │   ├── get
│   │   └── remove
│   └── suffix
│       ├── set <weight> <suffix>
│       ├── get
│       └── remove
└── player <player>
    ├── prefix
    │   ├── set <prefix> <weight>
    │   ├── get
    │   └── remove
    ├── suffix
    │   ├── set <suffix> <weight>
    │   ├── get
    │   └── remove
    ├── altname
    │   ├── set <name> <weight>
    │   ├── get
    │   └── remove
    └── economy
        ├── balance [currency]
        ├── set <amount> [currency]
        ├── add <amount> [currency]
        └── subtract <amount> [currency]

/pay <player> <amount> [currency]
/balance [currency]
/baltop [currency]
```

## Permission Nodes

| Node | Default |
|---|---|
| `redmc.vault` | op |
| `redmc.vault.reload` | op |
| `redmc.vault.group.prefix.set/get/remove` | op |
| `redmc.vault.group.suffix.set/get/remove` | op |
| `redmc.vault.player.prefix.set/get/remove` | op |
| `redmc.vault.player.suffix.set/get/remove` | op |
| `redmc.vault.player.altname.set/get/remove` | op |
| `redmc.vault.player.economy.balance/set/add/subtract` | op |
| `redmc.vault.balance` | true |
| `redmc.vault.baltop` | true |
| `redmc.vault.pay` | true |

## Currency System

Currencies are defined in `config.yml` under `currencies.definitions`. Each currency has:

- `display-name` — shown in messages
- `symbol` — shown after amounts
- `starting-balance` — for new players
- `ranks.enabled` + `ranks.tiers` — balance thresholds mapping to rank names (MiniMessage)

The default currency is specified in `currencies.default`.

## Configuration

| Key | Default | Description |
|---|---|---|
| `currencies.default` | `credits` | ID of the default currency used when no currency is specified |
| `currencies.definitions.<id>.display-name` | — | Full name shown in messages |
| `currencies.definitions.<id>.symbol` | — | Short symbol shown after amounts (e.g. `$`) |
| `currencies.definitions.<id>.starting-balance` | `0.0` | Balance assigned to new players |
| `currencies.definitions.<id>.ranks.enabled` | `false` | Enable balance rank tiers for this currency |
| `currencies.definitions.<id>.ranks.tiers.<balance>` | — | MiniMessage rank label applied when balance ≥ `<balance>` |

## Locale Keys

| Key | Value |
|---|---|
| `prefix` | `<#1E90FF>[<#FF1493>Vault<#1E90FF>]<#F0F8FF> ` |
| `error.no-permission` | `%prefix%<#FF6B6B>You don't have permission to use this.` |
| `error.generic` | `%prefix%<#FF6B6B>An error occurred. Please try again.` |
| `error.only-players` | `%prefix%<#FF6B6B>This command can only be used by players.` |
| `error.player-not-found` | `%prefix%<#FF6B6B>Player <#9b94a6>%name% <#FF6B6B>not found.` |
| `success` | `%prefix%<#3DDC97>Done.` |
| `not-found` | `%prefix%<#FF6B6B>Element <#9b94a6>%name% <#FF6B6B>not found.` |
| `insufficient-funds` | `%prefix%<#FF6B6B>Insufficient funds. Your balance: <#9b94a6>%balance%<#FF6B6B>.` |
| `group-prefix-value` | `%prefix%<#9b94a6>Group prefix: <#F0F8FF>%value%` |
| `group-suffix-value` | `%prefix%<#9b94a6>Group suffix: <#F0F8FF>%value%` |
| `player-prefix-value` | `%prefix%<#9b94a6>Player prefix: <#F0F8FF>%value%` |
| `player-suffix-value` | `%prefix%<#9b94a6>Player suffix: <#F0F8FF>%value%` |
| `player-altname-value` | `%prefix%<#9b94a6>Alt name: <#F0F8FF>%value%` |
| `economy-balance` | `%prefix%<#9b94a6>Balance of <#F0F8FF>%name%<#9b94a6>: <#FFB800>%balance% %symbol%` |
| `economy-set` | `%prefix%<#3DDC97>Balance of <#F0F8FF>%name% <#3DDC97>set to <#FFB800>%amount% %symbol%<#3DDC97>.` |
| `economy-add` | `%prefix%<#3DDC97>Added <#FFB800>%amount% %symbol% <#3DDC97>to <#F0F8FF>%name%<#3DDC97>.` |
| `economy-subtract` | `%prefix%<#3DDC97>Subtracted <#FFB800>%amount% %symbol% <#3DDC97>from <#F0F8FF>%name%<#3DDC97>.` |
| `pay-success` | `%prefix%<#3DDC97>Sent <#FFB800>%amount% %symbol% <#3DDC97>to <#F0F8FF>%target%<#3DDC97>.` |
| `pay-received` | `%prefix%<#3DDC97>Received <#FFB800>%amount% %symbol% <#3DDC97>from <#F0F8FF>%sender%<#3DDC97>.` |
| `my-balance` | `%prefix%<#9b94a6>Your balance: <#FFB800>%balance% %symbol%%rank%` |
| `baltop-header` | `%prefix%<#9b94a6>===== <#F0F8FF>Top 10 Richest<#9b94a6> =====` |
| `baltop-entry` | `%prefix%<#9b94a6>#<#FFB800>%rank% <#F0F8FF>%name%<#9b94a6>: <#FFB800>%balance% %symbol%` |
| `reload.config-success` | `%prefix%<#3DDC97>Configuration reloaded.` |
| `reload.data-success` | `%prefix%<#3DDC97>Data reloaded.` |
| `reload.all-success` | `%prefix%<#3DDC97>Configuration and data reloaded.` |

## Placeholders

Registers with `PlaceholdersPlugin` at priority 10:

| Placeholder | Value |
|---|---|
| `##PlayerPrefix##` | Highest-weight prefix |
| `##PlayerSuffix##` | Highest-weight suffix |
| `##PlayerAltName##` | Alt name |
| `##PlayerBalance##` | Balance in default currency |
| `##PlayerBalance_credits##` | Balance in `credits` |
