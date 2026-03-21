# Common Patterns

## Plugin Lifecycle

Every plugin follows this pattern:

```java
public class FooPlugin extends JavaPlugin {
    private static FooPlugin instance;
    private ConfigManager configManager;
    private LocaleManager localeManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this, "config.yml");
        localeManager = new LocaleManager(configManager, "en_US", "en_US", "ru_RU");
        // init managers, listeners...
    }

    public static FooPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LocaleManager getLocaleManager() { return localeManager; }
}
```

Plugin is always accessed via `JavaPlugin.getPlugin(FooPlugin.class)` inside commands — never via static `getInstance()`.

## ConfigManager

Wraps `YamlConfiguration` files. Initialized with the list of resource files:

```java
new ConfigManager(this, "config.yml", "groups.yml")
```

Backed by version-mismatch backup logic (see [structure.md](structure.md)).

Key methods: `getString`, `getInt`, `getDouble`, `getBoolean`, `getList`, `getConfig`, `reload`.

## LocaleManager

Resolves the player's client language to the correct locale file. Falls back to the default locale.

```java
String raw = localeManager.getMessage(sender, "some-key");
// Then substitute %prefix% and send:
ApiUtils.sendCommandSenderMessageArgs(sender, raw, "%prefix%", localeManager.getMessage(sender, "prefix"));
```

All locale keys that appear in commands must exist in **every** locale file.

### Standard Locale File Structure

Every plugin's `lang/en_US.yml` (and `ru_RU.yml`) follows this standardized nested layout:

```yaml
prefix: "<#1E90FF>[<#FF1493>PluginName<#1E90FF>]<#F0F8FF> "

error:
  no-permission: "%prefix%<#FF6B6B>You don't have permission to use this."
  only-players:  "%prefix%<#FF6B6B>This command can only be used by players."
  player-not-found: "%prefix%<#FF6B6B>Player not found."

# Plugin-specific keys (nested by feature)
featureName:
  some-key: "..."

reload:
  config-success: "%prefix%<#3DDC97>Configuration reloaded."
  data-success:   "%prefix%<#3DDC97>Data reloaded."
  all-success:    "%prefix%<#3DDC97>Configuration and data reloaded."
```

Key naming rules:
- Keys are **nested by section** (YAML map), never flat dot-notation strings.
- The `reload` section uses `config-success` / `data-success` / `all-success`; only the subcommands that exist are included (e.g. MOTD only has `reload.all-success`).
- Plugin-specific sections use the feature name as the top-level key (e.g. `afk`, `playtime`, `chat`, `cosmetics`).
- `getMessage(sender, "section.key")` — the dotted path matches the YAML nesting.

## ApiUtils

- `formatText(raw, args...)` — applies placeholder substitution and parses MiniMessage → `Component`
- `sendCommandSenderMessageArgs(sender, msg, args...)` — formats and sends message to sender
- `logArgs(msg, args...)` — logs to console with placeholder substitution

## Commands

Each command implements `Command<CommandSourceStack>`:

```java
public class FooCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        FooPlugin plugin = JavaPlugin.getPlugin(FooPlugin.class);
        LocaleManager locale = plugin.getLocaleManager();
        String prefix = locale.getMessage(sender, "prefix");

        // validate...
        // execute...

        ApiUtils.sendCommandSenderMessageArgs(sender, locale.getMessage(sender, "success"),
            "%prefix%", prefix);
        return Command.SINGLE_SUCCESS;
    }
}
```

Commands are registered in a `*Bootstrap` class via `LifecycleEvents.COMMANDS`. Each bootstrap splits the tree into `make*Node()` helper methods.

## CRUD Command Hierarchy

All management plugins follow this structure:

```
/plugin create <id>
/plugin read
/plugin delete <id>
/plugin reload
  config
  data / alerts
  all
/plugin update <id>
  <property> [value]
```

## Folia Scheduler

Never use `BukkitScheduler`. Use:

```java
// Global (non-region-bound):
plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> { ... });
plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> { ... }, delay, period);

// Async:
plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> { ... }, delay, period, TimeUnit.MILLISECONDS);

// Region-bound (entity or location):
plugin.getServer().getRegionScheduler().execute(plugin, location, () -> { ... });
```

## Color Palette (MiniMessage hex)

| Purpose | Hex |
|---|---|
| Primary accent (blue) | `<#1E90FF>` |
| Secondary accent (pink) | `<#FF1493>` |
| Success (green) | `<#3DDC97>` |
| Error (red) | `<#FF6B6B>` |
| Alert / economy (gold) | `<#FFB800>` |
| Muted text | `<#9b94a6>` |
| Dark muted | `<#888888>` |
| Light / body text | `<#F0F8FF>` |
| Magic / wither | `<#C780FA>` |
| Fire / lava accent | `<#FF4500>` |

## Placeholder System

Placeholders use `##Key##` syntax. Registered via `PlaceholdersPlugin.getRegistryManager().addRegistry(registry, priority)`.

Each registry maps keys to `PlaceholderContext` (a `Function<Player, String>`). Dynamic keys use a `patternHandler` (`BiFunction<String, Player, String>`).

`PlaceholderParser.parse(text, player)` resolves all `##Key##` tokens.

## Player Display Tokens

Locale strings must not reference `##PlayerName##`, `%player%`, or `%name%` directly for player display. Instead, use the Vault token families resolved by `VaultPlugin`:

| Token | Context |
|---|---|
| `%player_prefix%%player_altname%%player_suffix%` | Single-player (self or one target) |
| `%sender_prefix%%sender_altname%%sender_suffix%` | Acting player in two-player messages |
| `%target_prefix%%target_altname%%target_suffix%` | Receiving player in two-player messages |

Resolution in Java:

```java
// Single online player
msg = VaultPlugin.resolvePlayer(msg, player);

// Two online players
msg = VaultPlugin.resolveTwoPlayers(msg, sender, target);

// Sender online, target offline (by UUID)
msg = VaultPlugin.resolveTwoPlayers(msg, sender, targetUuid);

// Any player by UUID only (offline-safe)
msg = VaultPlugin.resolvePlayerByUuid(msg, uuid);
```

Offline players are resolved via `VaultMetaResolver` which reads stored `vault.prefix.*` / `vault.altname.*` / `vault.suffix.*` permission entries — no online `Player` required. The `%player_altname%` falls back to `VaultPlayerData.getName()` when no altname permission is set.

## MiniMessage

Always use `ApiUtils.getMM()` to obtain the `MiniMessage` instance. Never use `MiniMessage.miniMessage()` directly.
