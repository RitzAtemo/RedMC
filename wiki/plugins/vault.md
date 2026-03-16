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

## Placeholders

Registers with `PlaceholdersPlugin` at priority 10:

| Placeholder | Value |
|---|---|
| `##PlayerPrefix##` | Highest-weight prefix |
| `##PlayerSuffix##` | Highest-weight suffix |
| `##PlayerAltName##` | Alt name |
| `##PlayerBalance##` | Balance in default currency |
| `##PlayerBalance_credits##` | Balance in `credits` |
