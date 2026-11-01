# Troubleshooting

## The HUD does not appear

- **Are you holding a signature item?** Only the eight items in the
  [roster](../heroes/index.md) make you a hero. Decor blocks and the Mending Station do not.
- **Is the HUD hidden?** ++f1++ hides it along with the rest of the HUD.
- **Are you in spectator mode, or dead?** Neither counts as a hero.
- **Multiplayer:** the server must have LDFL installed too. A client-only install shows the items
  but nothing happens on the server side.

## I pressed the key and nothing happened

Work through these in order:

1. **Is the ability ready?** The HUD shows the seconds left in grey while it is cooling down, and
   **Ready** in green when it is not. For the ultimate, the meter must be full.
2. **Is the key bound to something else?** Open **Options → Controls** and look for red
   key names: a red key is bound twice. ++r++ is a common choice in item-list mods. Rebind the
   *Hero Ability* keys under **Frontline Heroes**.
3. **Did the ability have something to do?** Some abilities fail without spending the cooldown
   when they cannot do anything:
    - **Deploy Sentry** needs the **top face** of a block within 6 blocks, with room above it and no
      water. Aim at the ground in front of you.
    - **Upheaval** needs air or plants three blocks in front of you. It cannot replace real blocks,
      water or lava, and will not form inside a player.
4. **Is there anyone to affect?** Damage abilities only hit [enemies](../guide/allies-and-enemies.md).
   On **Peaceful** there are no hostile mobs, and with PvP off, players are never enemies.

## My abilities do not hurt other players

That is the default: `combat.pvp` is `false`, so every player is an ally. Turn it on in
`config/ldfl.cfg`, and use scoreboard teams to keep teammates safe. See
[Allies, enemies and PvP](../guide/allies-and-enemies.md).

## My heals do not reach my friend

- With **PvP on**, only players on **your scoreboard team** are allies. Join the same team.
- Solace's beam needs **line of sight** and range: it locks on within 12 blocks and lets go past
  15 blocks or behind a wall.
- The beam never targets yourself. Use the Bloom Flask on yourself.

## My ultimate meter went back to zero

Switching hero, even for a moment, empties the meter. So does dying or logging out. See
[Switching heroes](../guide/how-heroes-work.md#switching-heroes). The Wounded effect does not
touch it; it only reduces healing *received*.

## My sentry disappeared

A sentry powers down whenever its owner is offline or is not Forgewright, and packs itself away
after **30 seconds** powered down. Deploying a new sentry also packs away the old one: you can have one at a time. And
enemies can destroy it: keep it repaired with the wrench, or with Plating.

## My barrier will not come up

- It needs a moment of holding right-click. A quick tap does nothing.
- If it was **broken**, it cannot be raised for 5 seconds.
- If the **Barrier** bar is empty, it needs to regenerate first. It recharges only while lowered.
- An axe hit can disable the shield, just like a vanilla shield.

## My max health or armor looks wrong

The Tank bonus (+4 armor, +6 health) applies only while you are a Tank, and is never saved. If you
see bonus hearts without holding a Tank item, swap items once: the passives are recalculated on
every hero change. If it persists, please [report it](#reporting-a-bug).

## A dedicated server crashes on start with a client class error

Make sure the server and clients run the **same LDFL version**, downloaded from the
[Releases page](https://github.com/Mica-Technologies/LDFL/releases). LDFL's CI boots a dedicated
server to catch exactly this kind of problem, so a crash like this is a bug worth reporting.

## Reporting a bug

Open an issue on [GitHub](https://github.com/Mica-Technologies/LDFL/issues) with:

- the LDFL version (from the jar name or the Mods screen) and Forge version,
- what you did, what you expected, and what happened,
- `logs/latest.log`, and the crash report from `crash-reports/` if there was one.
