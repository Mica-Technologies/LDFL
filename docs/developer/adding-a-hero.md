# Adding a hero

A walkthrough of what a hero is made of, using the existing heroes as the pattern. Read
[Architecture](architecture.md) first.

!!! danger "Names and art must be original"

    LDFL is public and MIT licensed. Do **not** use the name of any hero, ability, weapon or other
    content from any existing game, and do not copy or trace any art, model or sound. Check a new
    name against existing hero shooters before using it, and prefer plain or invented words.

## 1. Pick an id and a role

The hero id is lower case and permanent: it appears in packets and lang keys, so never rename one
after release. Add a constant to `hero/Heroes.java`:

```java
public static final String WARDEN_ID = "warden";
public static HeroDefinition WARDEN;
```

## 2. Write the kit

Create `hero/warden/WardenKit.java`. Each ability is a method with the `HeroAbility` shape:

```java
public static boolean myAbility(EntityPlayerMP p, HeroData data) {
    // ... do the thing, server side ...
    return true;   // false = could not run; the cooldown is not spent
}
```

Rules:

- Ask `Targeting.isEnemy` / `Targeting.isAlly` for every target. Never check entity types
  yourself.
- Credit healing with `HeroEvents.creditHealing(player, amountThatLanded)`. Damage dealt is
  credited automatically.
- Read every tunable number from `LdflConfig` at the moment you use it.
- If the hero keeps something alive between ticks (an entity, a timer), add a `tick` method that
  sets `data.resource` (0 to 1, or leave it at -1 for no bar) and a `cleanup` method that tears it
  down. `cleanup` runs every tick for every hero that is *not* active, so keep it cheap and
  idempotent.

## 3. Register it

In `Heroes.init()`, keeping the cooldowns and ult cost as lambdas so config changes apply live:

```java
WARDEN = HeroRegistry.register(new HeroDefinition(
    new HeroSpec(WARDEN_ID, Role.TANK,
        new AbilitySpec("first_ability", () -> ticks(LdflConfig.warden.firstCooldown)),
        new AbilitySpec("second_ability", () -> ticks(LdflConfig.warden.secondCooldown)),
        new AbilitySpec("ultimate_ability", 0),
        () -> LdflConfig.warden.ultCost),
    WardenKit::firstAbility, WardenKit::secondAbility, WardenKit::ultimateAbility)
    .withTick(WardenKit::tick)
    .withCleanup(WardenKit::cleanup));
```

## 4. Add config

Add a section class and field to `LdflConfig`, with `@Config.Comment` and a `@Config.Range*` on
every value. Cooldowns and durations in seconds, damage in health points.

## 5. Make the signature item

An item becomes a signature item by implementing `ISignatureItem` and returning the hero id.
For a plain melee weapon, `ItemHeroMelee` already does this:

```java
public static final Item WARDEN_BLADE = add(
    new ItemHeroMelee(Heroes.WARDEN_ID, 7.0D, -2.6D), "warden_blade");
```

Then add its model, texture (drawn with `tools/art`), recipe, and a recipe-book unlock (regenerate
with `tools/data/gen-advancements.js`). Add the item to the matching role advancement and to
**Full Roster**.

## 6. Lang

Add to `en_us.lang`, following the key scheme:

```
item.ldfl.warden_blade.name=Warden Blade
ldfl.hero.warden=Warden
ldfl.ability.warden.first_ability=...
ldfl.ability.warden.second_ability=...
ldfl.ability.warden.ultimate_ability=...
ldfl.resource.warden=...        (only if the hero shows a resource bar)
```

## 7. Sounds

Give each ability its own sound: add a line to `tools/audio/sounds.sh`, an entry in `sounds.json`
with a subtitle, a constant in `LdflSounds`, and a `subtitles.ldfl.<name>` lang line. Keep the
three lists in step.

## 8. Test it

- Unit-test anything rule-like in `hero.core`.
- `./gradlew build`, then `./gradlew runClient` and try every ability against mobs, with PvP both
  off and on, and with an ally (a tamed wolf works).
- Check `run/logs/latest.log` for missing model or texture warnings.
- `./gradlew runServer` once, to make sure nothing client-only leaked into common code.

## 9. Document it

Add a page under `docs/heroes/`, a row in the roster and in the [ability table](../reference/abilities.md),
and screenshots.
