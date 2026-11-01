# Ability table

Every ability in one place, with its default numbers. "Configurable" values can be changed in
`config/ldfl.cfg` (see [Configuration](config.md)); the rest are fixed in the mod.

Durations and cooldowns are in seconds. Damage and healing are in health points (2 = one heart).

## Tanks

### Bulwark <span class="role tank">Tank</span>

| Slot | Ability | Cooldown | Effect | Area |
| --- | --- | --- | --- | --- |
| Hold | **Barrier** | — | 60 health; regenerates 6/s while lowered, after 2 s; 5 s lockout when broken | Panel in front of you |
| <span class="slot">R</span> | **Shoulder Rush** | 8 | 0.5 s dash; first enemy hit takes 6 and is knocked back | Your path |
| <span class="slot">G</span> | **Shockwave** | 6 | 5 to each enemy on the line, small knockback | Line, 16 blocks, stops at walls |
| <span class="slot ult">Z</span> | **Tectonic Slam** | 160 charge | 6 damage, knock-up, Slowness IV + Weakness II for 2.5 s | 100° cone, 8 blocks |

Configurable: both cooldowns, Rush/Shockwave/Slam damage, ult cost, barrier health, regen and
broken time.

### Monolith <span class="role tank">Tank</span>

| Slot | Ability | Cooldown | Effect | Area |
| --- | --- | --- | --- | --- |
| <span class="slot">R</span> | **Stone Skin** | 12 | 8 absorption for 6 s | Self |
| <span class="slot">G</span> | **Upheaval** | 10 | Wall of temporary stone, crumbles after 6 s | 5 wide × 2 high, 3 blocks ahead |
| <span class="slot ult">Z</span> | **Bedrock Anchor** | 180 charge | 6 s: knockback immune, Resistance I, taunts mobs every second | Taunt radius 16 |

Configurable: both cooldowns, Stone Skin absorption, wall lifetime, anchor duration, taunt radius,
ult cost.

## Damage

### Forgewright <span class="role damage">Damage</span>

| Slot | Ability | Cooldown | Effect | Area |
| --- | --- | --- | --- | --- |
| Weapon | **Bolt Driver** | 0.4 per shot | 4 per bolt (6 during Slag Surge) | Straight line, no drop |
| <span class="slot">R</span> | **Deploy Sentry** | 10 | Turret: 30 health, 3 per bolt, 2 shots/s | Placed up to 6 blocks away; fires to 16 |
| <span class="slot">G</span> | **Plating** | 12 | Resistance II for 5 s; sentry fully repaired | Self + sentry |
| <span class="slot ult">Z</span> | **Slag Surge** | 200 charge | 8 s: sentry fires 2× faster, your bolts 1.5×, all bolts ignite for 3 s | Self + sentry |

Configurable: both cooldowns, bolt damage, sentry health/damage/fire rate/range, surge duration,
ult cost. Fixed: wrench repairs 6 per hit; idle sentries pack away after 30 s.

### Kestrel <span class="role damage">Damage</span>

| Slot | Ability | Cooldown | Effect | Area |
| --- | --- | --- | --- | --- |
| Weapon | **Longshot** | 0.5 after a shot | 4 (snap) to 16 (full charge, 1.5 s); hitscan | 64 blocks, first enemy |
| <span class="slot">R</span> | **Updraft** | 8 | Launch upward, slow glide, no fall damage | Self |
| <span class="slot">G</span> | **Marker Dart** | 10 | Glowing + Marked (+30% damage taken) for 6 s | First enemy, 48 blocks |
| <span class="slot ult">Z</span> | **Long Vigil** | 220 charge | 8 s: enemies glow, Longshot pierces and charges 2× faster | Reveal radius 48 |

Configurable: Longshot min/max damage and charge time, both cooldowns, Marked bonus and duration,
Vigil duration, ult cost.

## Support

### Solace <span class="role support">Support</span>

| Slot | Ability | Cooldown | Effect | Area |
| --- | --- | --- | --- | --- |
| Hold | **Heal beam** | — | 3 health/s to one ally | Lock-on 12 blocks, holds to 15 |
| <span class="slot">R</span> | **Bloom Flask** | 8 | Allies healed 6; enemies take 3 and are Wounded 4 s | 4-block burst where it lands |
| <span class="slot">G</span> | **Tailwind** | 12 | Speed II for 4 s | Allies within 8 |
| <span class="slot ult">Z</span> | **Sanctuary** | 200 charge | Heal 10, then Regeneration II + Resistance I for 6 s | Allies within 10 |

Configurable: beam healing, flask heal and damage, both cooldowns, Sanctuary heal, ult cost.

### Chime <span class="role support">Support</span>

| Slot | Ability | Cooldown | Effect | Area |
| --- | --- | --- | --- | --- |
| Weapon | **Tuning Rod** | 0.6 per shot | 3.5 per orb | Straight line, no drop |
| <span class="slot">R</span> | **Resonance** | 14 | Strength I for 6 s | Allies within 8 |
| <span class="slot">G</span> | **Ward Bell** | 16 | Bell rings every 2 s for 10 s; each ring gives Absorption I | Allies within 6 of the bell |
| <span class="slot ult">Z</span> | **Crescendo** | 200 charge | Resistance IV + Regeneration II for 3 s | Allies within 10 |

Configurable: orb damage, both cooldowns, bell lifetime, Crescendo duration, ult cost.

## Role passives

| Role | Passive | Default |
| --- | --- | --- |
| Tank | Armor, max health, knockback resistance | +4, +6, 50% |
| Damage | Hits leave enemies **Wounded** | 3 s, healing received ×0.5 |
| Support | Regeneration out of combat | After 3 s without damage, 0.5 health/s |

All configurable under `passives`.
