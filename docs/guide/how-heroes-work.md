# How heroes work

The whole hero system rests on one rule: **the signature item in your hand decides who you are.**
There is no class picker, no menu and nothing saved to your character. This page covers what
follows from that.

## Becoming a hero

Every hero has one or two **signature items** (see the [roster](../heroes/index.md)). The moment
one is in your hand you become that hero:

- the **hero HUD** appears in the top right,
- your **role passive** applies ([Tank, Damage or Support](roles.md)),
- your three ability keys start working.

Put the item away and all of that goes away. You are an ordinary player again.

## Which hand counts

Both hands count, and **the main hand wins**:

| Main hand | Off hand | You are |
| --- | --- | --- |
| A signature item | Anything | The **main-hand** item's hero |
| Anything else (or empty) | A signature item | The **off-hand** item's hero |
| Anything else | Anything else | Not a hero |

This is what lets [Bulwark](../heroes/bulwark.md) carry its shield in the off hand while swinging
the maul, or a sword, or a pickaxe, in the main hand. It also means that if you deliberately take
another hero's weapon into your main hand, you become that hero.

!!! example "Shield plus a tool"

    Bulwark Shield in the off hand, a pickaxe in the main hand: you are Bulwark and can mine while
    keeping the barrier ready. Swap the pickaxe for a Solace Staff and you are Solace instead.

## Switching heroes

You can switch at any time by changing what you hold. Carrying several signature items in the
hotbar is a legitimate way to play.

Every switch is a **fresh start**: the new hero's abilities start **ready**, and the **ultimate
meter starts empty**. Swapping back does not give you the old charge back. So the rule of thumb is:
**pick your hero before the fight, and switch only when it is worth losing your ultimate charge.**

Whatever the old hero left in the world tidies itself up when you switch: a Bulwark barrier
drops, a Solace beam stops, a Bedrock Anchor ends, and a Forgewright sentry powers down (and packs
itself away after 30 seconds unless you switch back).

## Abilities and cooldowns

Each hero has two abilities on cooldowns, on <span class="slot">R</span> and
<span class="slot">G</span> by default. Press the key, the ability fires, and the HUD counts down
until it is ready again.

An ability that **cannot do anything costs nothing**. A sentry with no room to stand on, or a wall
with no air to fill, fails quietly and leaves the cooldown untouched. Try again somewhere else.

## Ultimates

The third ability, on <span class="slot ult">Z</span>, is the **ultimate**. It has no cooldown.
Instead it needs a full **ultimate meter**, which fills as you deal damage and, for Supports, as
you heal and buff. See [Ultimates](ultimates.md).

## The server decides

In multiplayer, everything that matters happens on the **server**:

1. Your client sends only "ability key pressed".
2. The server checks that you are a hero, that the ability is off cooldown (or your ultimate is
   full), and then runs it.
3. The server sends your hero, cooldowns, ultimate charge and resource bar back to your HUD.

So a modified client cannot fire abilities early or charge its ultimate faster, and lag never
lets anyone use an ability twice. Your HUD counts cooldowns down smoothly between updates, and the
server corrects it if they ever disagree.

## Nothing is saved

LDFL stores **no hero data** in your player file or the world. Log out and back in and you are
whatever you are holding, with fresh cooldowns and an empty meter. Uninstalling the mod leaves
your player data untouched. The role passive's bonus armor and health are likewise never saved,
so they cannot get "stuck" on a character after an update or a crash.

## Dying and spectating

A dead or spectating player is never a hero. When you respawn holding a signature item, you
become that hero again with a fresh start.

## Who can I hit, and who can I heal?

Every ability asks the same question about every target: *is this an enemy, an ally, or
neither?* By default heroes fight **hostile mobs** and help **every player** and your **own pets**.
With PvP turned on, players on other scoreboard teams become enemies. The full rules are on
[Allies, enemies and PvP](allies-and-enemies.md).
