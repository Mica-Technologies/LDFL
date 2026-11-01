# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

LDFL ("Limitless Development: Frontline Heroes") is a **Minecraft 1.12.2 Forge mod** (mod id: `ldfl`)
that adds a hero system inspired by team hero shooters. **Holding a hero's signature item in either
hand makes you that hero** (mainhand wins). You get your role's passive (Tank, Damage or Support)
and three keybound abilities: two on cooldowns and an ultimate that charges from damage dealt and
healing done. The mod also adds the heroes' gear and a set of futuristic decor blocks.

## Intellectual-property policy — load-bearing, read first

LDFL is **open source under MIT on a public repo.** It is *inspired by* a genre, not by any one
game's content. Therefore:

- **Never** write the name of the game that inspired it, or **any** of its hero, ability, weapon,
  map, item or faction names, anywhere: code, identifiers, lang files, docs, commit messages or
  comments. The same applies to other hero shooters.
- **Never** copy, trace or derive art, textures, models, sounds or UI layouts from any game.
- Before adding a name, check it is not a known hero-shooter name. Prefer generic or invented
  words. When in doubt, pick another name.
- Describe the mod as "hero-shooter inspired" and nothing more specific.

## Build Commands

Set `JAVA_HOME` to a **JDK 17–22** install before each `./gradlew` invocation (21 recommended; CI
uses 21). The mod itself targets **Java 8** via Jabel.

```bash
JAVA_HOME="..." ./gradlew build      # compile + unit tests + jar
JAVA_HOME="..." ./gradlew test       # unit tests only (pure JVM, seconds)
JAVA_HOME="..." ./gradlew runClient  # dev client
JAVA_HOME="..." ./gradlew runServer  # dev dedicated server
```

Never build while a `runClient`/`runServer` is live; it corrupts the running classpath.

Build system is GregTechCEu Buildscripts (RetroFuturaGradle), scaffolded from sibling mod LDIB.
Do **not** run `gradle updateBuildScript`; see the note in `gradle.properties`.

## Architecture

```
hero/core/   Hero rules. Pure Java, ZERO Minecraft types.
  Role, AbilitySlot, AbilitySpec, HeroSpec   rules data
  HeroState                                   cooldowns + ultimate meter; check() then commit()
  HeroResolver                                (mainhand, offhand) -> active hero
hero/        Minecraft side: HeroRegistry/Heroes, HeroData capability, HeroEvents (tick, passives,
             ult charge), Targeting (friend-or-foe), per-hero ability classes.
item/        Signature items (implement ISignatureItem) and other gear.
network/     PacketUseAbility (C->S), PacketHeroSync (S->C).
client/      Keybinds, HUD, client hero mirror. Client-only; reached only via LdflClientProxy.
```

### Rules that are load-bearing

1. **`hero.core` must never import a Minecraft type.** That keeps the rules unit-testable on a
   bare JVM.
2. **Common code must never reach client-only classes.** A `net.minecraft.client` import in
   common code compiles and only fails when a dedicated server boots. The CI server smoke test
   exists to catch that.
3. **The server is authoritative.** The client sends only "slot N pressed". The server checks the
   hero, cooldown and ult charge, runs the ability, and syncs state back. The client mirror ticks
   cooldowns down locally only for a smooth HUD.
4. **Hero data is not persisted.** Identity comes from held items. Role passive attribute
   modifiers are marked unsaved for the same reason, and `HeroPassives.reconcile` (run on the
   first tick after login and on every hero change) removes before it adds. Keep it idempotent.
5. **Friend-or-foe goes through `Targeting.isEnemy`.** Hostile mobs are always enemies; players
   only when `combat.pvp` is on and they are not on the same scoreboard team.

## Conventions

- Package root: `com.micatechnologies.minecraft.ldfl`. Registry names use the `ldfl:` prefix.
- Never hard-code the mod id, name or version; use `LdflConstants` (reads the generated `Tags`).
- Lang keys: `ldfl.hero.<id>`, `ldfl.role.<id>`, `ldfl.ability.<hero>.<ability>`.
- Commit and push straight to `main` for now; no PRs until the first release.

## Planning docs

`docs/AGENT-PLANS/` is **gitignored** and holds the phased master plan and its decisions table.
Read its STATUS section first when resuming.
