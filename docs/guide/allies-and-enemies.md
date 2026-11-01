# Allies, enemies and PvP

Every hero effect, every ability, the sentry, the barrier and the passives, asks the same two
questions about a target: **can I hurt it?** and **can I help it?** One set of rules answers both,
so they apply the same way everywhere.

## The default: heroes fight monsters

Out of the box, PvP is **off**. Heroes are a co-op toolkit:

| Target | Enemy (can be hurt) | Ally (can be healed and buffed) |
| --- | :---: | :---: |
| Hostile mobs (zombies, skeletons, creepers, witches, …) | :material-check: | |
| **Every player** | | :material-check: |
| Your own tamed pets (wolves, cats, parrots, …) | | :material-check: |
| Yourself | | :material-check: |
| Passive animals, villagers, iron golems | | |
| Other players' pets | | |

Nothing you do can hurt another player, and every heal, shield and buff reaches every player in
range.

## Turning PvP on

Set `combat.pvp` to `true` in `config/ldfl.cfg` (or in the in-game config screen). Now players
are enemies too, with two exceptions:

- players on **your scoreboard team** stay allies,
- players in **creative** or **spectator** mode are never targeted.

| Target, PvP on | Enemy | Ally |
| --- | :---: | :---: |
| Hostile mobs | :material-check: | |
| Players on **your** team | | :material-check: |
| Players on **another** team, or on no team | :material-check: | |
| Your own tamed pets | | :material-check: |
| Anything on your team (mobs too) | | :material-check: |

The setting is read live, so changing it mid-game takes effect immediately.

## Setting up teams

LDFL uses vanilla **scoreboard teams**. For a two-team match:

```
/scoreboard teams add red
/scoreboard teams add blue
/scoreboard teams option red color red
/scoreboard teams option blue color blue
/scoreboard teams join red PlayerOne PlayerTwo
/scoreboard teams join blue PlayerThree PlayerFour
```

With `combat.pvp` on, red and blue can now hurt each other, and each team's Supports heal only
their own side. You may also want `/scoreboard teams option red friendlyfire false` so vanilla
melee does not hit teammates either.

!!! tip "Mobs on teams"

    Anything on your team counts as an ally, mobs included. Put a summoned zombie on your team and
    your heals reach it, and your abilities leave it alone. Teams only ever turn enemies into
    allies, though: a villager or an animal on the other team is still not something heroes
    attack.

## Specific cases

**Bulwark's barrier** stops only *enemy* projectiles and swings, and only enemies are pushed back
by it. Allies walk and shoot straight through.

**Forgewright's sentry** cannot be damaged by its owner or the owner's allies. Hitting it with a
Tinker's Wrench repairs it instead. A sentry's bolts count as its owner's shots, so the same
friend-or-foe rules apply to them.

**Monolith's Bedrock Anchor** taunts only *mobs*. Players, even enemy players, always choose their
own targets.

**Solace's heal beam** locks onto allies only, and never onto yourself.
