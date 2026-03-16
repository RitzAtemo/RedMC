# Placeholders Plugin

Priority-sorted placeholder registry system. Provides `##Key##` token resolution for all other plugins.

## How It Works

- `PlaceholderRegistryManager` holds a list of `PlaceholderRegistry` instances sorted by priority (higher = evaluated first)
- Each registry maps string keys to a `PlaceholderContext` (`Function<Player, String>`)
- Dynamic keys use a `patternHandler` (`BiFunction<String, Player, String>`) — return `null` to leave the placeholder unparsed
- `PlaceholderParser.parse(text, player)` resolves all `##Key##` tokens in a string

## Registering a Registry

```java
PlaceholdersPlugin.getRegistryManager().addRegistry(myRegistry, priority);
```

## Built-in Placeholders (registered by other plugins)

| Placeholder | Source | Description |
|---|---|---|
| `##PlayerPrefix##` | Vault | Player's highest-weight prefix |
| `##PlayerSuffix##` | Vault | Player's highest-weight suffix |
| `##PlayerAltName##` | Vault | Player's alt name |
| `##PlayerBalance##` | Vault | Balance in the default currency |
| `##PlayerBalance_X##` | Vault | Balance in currency `X` |
| `##PlayerName##` | API | Player's name |
| `##CurrentServerOnline##` | API | Online player count |
| `##ServerOnlineMaximum##` | API | Max player count |

## Config

```yaml
config-version: "0.0.1-alpha"
pattern: "##(.*?)##"
general-registry-weight: 0
```
