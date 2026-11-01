# Limitless Development: Frontline Heroes (LDFL)

A hero-shooter inspired mod for Minecraft 1.12.2 (Forge).

**Hold a hero's signature item and you become that hero.** Each hero has a role:

| Role | Passive |
|------|---------|
| **Tank** | Extra armor, extra health, knockback resistance |
| **Damage** | Your hits leave enemies *Wounded*, so they receive less healing |
| **Support** | Regenerate health after a few seconds out of combat |

Every hero also has three abilities on keybinds: two on cooldowns and an **ultimate** that charges
as you deal damage or heal. The HUD shows your hero, role, cooldowns and ultimate charge.

Default keys (rebindable under *Controls → LDFL*): **R** ability 1, **G** ability 2, **Z** ultimate.

## Heroes (work in progress)

- **Bulwark** (Tank): Rampart Shield and Bulwark Maul. *Shoulder Rush*, *Shockwave*, ultimate *Fault Line*.
- **Forgewright** (Damage): deploys an auto-targeting sentry. *(planned)*
- **Solace** (Support): heal beam and area heals. *(planned)*

## Server config

`config/ldfl.cfg` → `combat.pvp` (default `false`). When it is off, hero kits affect hostile mobs
only. When it is on, they also affect players, except players on your own scoreboard team.

## Building

Requires a JDK 17–22 (21 recommended) to run Gradle; the mod targets Java 8.

```bash
./gradlew build
```

## License

[MIT](LICENSE). The build scripts come from
[GregTechCEu Buildscripts](https://github.com/GregTechCEu/Buildscripts), which are also MIT
(see `LICENSE-GregTechCEu Buildscripts.txt`).

LDFL is an original work and is not affiliated with any game studio. It contains no third-party
characters, names or assets.
