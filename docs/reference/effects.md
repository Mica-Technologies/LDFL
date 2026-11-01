# Status effects

LDFL adds two status effects of its own and uses several vanilla ones. All of them show in the
inventory and as icons in the top right, like any potion effect, and `/effect` can apply or clear
them.

## LDFL effects

### Wounded

`ldfl:wounded` · harmful

A Wounded creature receives only **half** the healing it would otherwise get, from every source:
potions, regeneration, golden apples, Solace's beam. Applied by:

- any hit from a [Damage](../guide/roles.md#damage) hero, for 3 seconds (refreshed on every hit),
- Solace's [Bloom Flask](../heroes/solace.md), to enemies in the burst, for 4 seconds.

The healing factor (`woundedHealingFactor`) and the Damage passive's duration
(`damageWoundSeconds`) are in the `passives` config.

### Marked

`ldfl:marked` · harmful

A Marked creature takes **30% more damage** from every source, whoever deals it. Applied, with
Glowing, by Kestrel's [Marker Dart](../heroes/kestrel.md) for 6 seconds. The bonus
(`markedDamageBonus`) and the duration (`markedSeconds`) are in the `kestrel` config.

## Vanilla effects LDFL uses

| Effect | Applied by | To |
| --- | --- | --- |
| Slowness IV | Tectonic Slam (2.5 s) | Enemies in the cone |
| Weakness II | Tectonic Slam (2.5 s) | Enemies in the cone |
| Absorption II | Stone Skin (6 s) | Monolith |
| Absorption I | Ward Bell (each ring) | Allies near the bell |
| Resistance I | Bedrock Anchor (6 s), Sanctuary (6 s) | Monolith; allies in range |
| Resistance II | Plating (5 s) | Forgewright |
| Resistance IV | Crescendo (3 s) | Allies in range |
| Regeneration II | Sanctuary (6 s), Crescendo (3 s) | Allies in range |
| Speed II | Tailwind (4 s) | Allies in range |
| Strength I | Resonance (6 s) | Allies in range |
| Glowing | Marker Dart (6 s), Long Vigil (8 s) | Enemies |
| Fire | Slag Surge bolts (3 s) | Enemies hit |
