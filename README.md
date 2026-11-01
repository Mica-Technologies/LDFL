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

- **Bulwark** (Tank): Rampart Shield and Bulwark Maul. Holding up the shield projects a wide
  barrier that stops enemy projectiles and melee, with its own health shown on the HUD.
  *Shoulder Rush*, *Shockwave*, ultimate *Fault Line*.
- **Forgewright** (Damage): Bolt Driver and Tinker's Wrench. *Deploy Sentry* places an
  auto-targeting sentry (one at a time; hit it with the wrench to repair it), *Plating* gives
  brief damage resistance and fully repairs the sentry, ultimate *Slag Surge* doubles the
  sentry's fire rate and makes every bolt ignite.
- **Solace** (Support): Solace Staff. Hold right-click to beam healing into the ally in your
  sights. *Bloom Flask* heals allies (you included) and wounds enemies, *Tailwind* speeds up
  nearby allies, ultimate *Sanctuary* heals and shields every ally around you.

With `combat.pvp` off, every player counts as an ally, and your tamed pets always do.

## Server config

`config/ldfl.cfg` → `combat.pvp` (default `false`). When it is off, hero kits affect hostile mobs
only. When it is on, they also affect players, except players on your own scoreboard team.

## Operator commands

- `/ldfl ult [player]`: fill the active hero's ultimate meter.
- `/ldfl reset [player]`: clear the active hero's ability cooldowns.

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
