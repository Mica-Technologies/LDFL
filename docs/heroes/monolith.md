# Monolith

<div class="hero-strip" markdown>
<img src="../../assets/images/icons/cragfist.png" alt="">
<div class="meta">
<strong>Monolith</strong>
<span><span class="role tank">Tank</span> &nbsp;Stone walls and taunts</span>
<span>Signature item: <strong>Cragfist</strong></span>
</div>
</div>

Monolith is a brawler tank made of stone. Where [Bulwark](bulwark.md) protects with a barrier,
Monolith protects by **becoming the target**: it hardens its skin, throws up walls of rock to
block paths, and with its ultimate drags every hostile mob nearby onto itself while it stands
immovable.

![Bedrock Anchor: Monolith rooted in place with bedrock dust at its feet and the Anchor bar on the HUD](../assets/images/monolith-anchor.jpg)

## Signature item

| Item | What it does |
| --- | --- |
| <img class="icon" src="../../assets/images/icons/cragfist.png" alt=""> **Cragfist** | A stone-and-iron gauntlet: **9 attack damage**, 1.1 swings per second. Never wears out. |

## Passive: Tank

**+4 armor**, **+6 max health** (three hearts) and **50% knockback resistance** while Monolith is
active. See [Roles and passives](../guide/roles.md).

## Abilities

!!! ability "Stone Skin · <span class='slot'>R</span> · 12 s cooldown"

    Your skin turns to stone: **8 points of absorption** (four golden hearts) for **6 seconds**.
    Absorption soaks up damage before your real health does, so pop it just before you walk into
    a fight, not after you are already low.

    ![Stone Skin: absorption hearts on the health bar and stone fragments around Monolith](../assets/images/monolith-stone-skin.jpg)

!!! ability "Upheaval · <span class='slot'>G</span> · 10 s cooldown"

    Raise a wall of rough **Upheaval Stone**, **5 blocks wide and 2 high**, three blocks in
    front of you and square to where you face. It crumbles away on its own after about
    **6 seconds** and drops nothing.

    - It only fills **air and plants** (grass, flowers, snow layers and the like). It never
      replaces real blocks, water or lava, so it cannot be used to grief or to dig.
    - It will not form inside a player.
    - It steps up over a one-block rise, so the wall follows gentle terrain.
    - If there is no room for even one block, nothing happens and **no cooldown is spent**.

    Use it to plug a doorway, cut a mob off from an ally, or hide a Support who is being chased.

    ![An Upheaval wall of cracked stone cutting the arena in two](../assets/images/monolith-upheaval.jpg)

!!! ultimate "Bedrock Anchor · <span class='slot ult'>Z</span> · ultimate (180 damage)"

    Root yourself for **6 seconds**:

    - you **cannot be knocked back** at all,
    - you take less damage (**Resistance I**),
    - and every hostile mob within **16 blocks** is forced to target you, **re-taunted every
      second** for the whole duration.

    The HUD shows an **Anchor** bar counting down. Bedrock dust swirls at your feet while it lasts.

    Bedrock Anchor turns the fight around: the mobs that were chasing your Support turn to you,
    and your team gets six seconds of free hits.

## Playing Monolith

- **Stone Skin, then dive.** Monolith has no barrier; absorption is its shield. Use it on the way
  in.
- **Walls are for your team, not just for you.** An Upheaval between a skeleton and your Solace
  is worth more than one in front of your own face.
- **Anchor when the mobs are spread out.** The taunt collects them; then your Damage heroes
  have one tidy target area. Pair it with [Bulwark's Tectonic Slam](bulwark.md) or
  [Chime's Crescendo](chime.md) for a big turn.
- Taunting only affects **mobs**. Players, even enemy players with PvP on, choose their own
  targets.

## Crafting

<!-- recipe:cragfist -->
<div class="recipe">
<div class="grid3"><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Stone</span><span class="cell">Iron Block</span><span class="cell">Stone</span><span class="cell"></span><span class="cell">Iron Block</span><span class="cell"></span></div>
<span class="arrow">→</span>
<span class="out"><img src="../../assets/images/icons/cragfist.png" alt="ldfl:cragfist" title="ldfl:cragfist"></span>
</div>
<!-- /recipe -->

## Tuning

Server owners can change the cooldowns, absorption, wall lifetime, anchor duration, taunt radius
and ult cost in `config/ldfl.cfg` under `monolith`. See
[Configuration](../reference/config.md#monolith).
