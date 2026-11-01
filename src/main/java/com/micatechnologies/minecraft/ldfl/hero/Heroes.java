package com.micatechnologies.minecraft.ldfl.hero;

import static com.micatechnologies.minecraft.ldfl.LdflConfig.ticks;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.bulwark.BarrierLogic;
import com.micatechnologies.minecraft.ldfl.hero.bulwark.BulwarkKit;
import com.micatechnologies.minecraft.ldfl.hero.chime.ChimeKit;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySpec;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import com.micatechnologies.minecraft.ldfl.hero.core.Role;
import com.micatechnologies.minecraft.ldfl.hero.forgewright.ForgewrightKit;
import com.micatechnologies.minecraft.ldfl.hero.kestrel.KestrelKit;
import com.micatechnologies.minecraft.ldfl.hero.monolith.MonolithKit;
import com.micatechnologies.minecraft.ldfl.hero.solace.SolaceKit;

/**
 * Every hero LDFL ships, defined in one place. Hero ids are save-compatible identifiers (they
 * appear in packets and lang keys): never rename one, add a new one instead.
 *
 * <p>Naming rule (see CLAUDE.md): every hero and ability name is original. Check new names
 * against existing hero shooters before adding them.</p>
 */
public final class Heroes {

    public static final String BULWARK_ID = "bulwark";
    public static final String FORGEWRIGHT_ID = "forgewright";
    public static final String SOLACE_ID = "solace";
    public static final String MONOLITH_ID = "monolith";
    public static final String KESTREL_ID = "kestrel";
    public static final String CHIME_ID = "chime";

    public static HeroDefinition BULWARK;
    public static HeroDefinition FORGEWRIGHT;
    public static HeroDefinition SOLACE;
    public static HeroDefinition MONOLITH;
    public static HeroDefinition KESTREL;
    public static HeroDefinition CHIME;

    private Heroes() {
        throw new AssertionError("No instances.");
    }

    /**
     * Register every hero. Cooldowns and ult costs are read from {@link LdflConfig} each time they
     * are used, so the lambdas below must stay lambdas: capturing the value here would freeze it.
     */
    public static void init() {
        // --- Tanks ---------------------------------------------------------------------------
        BULWARK = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(BULWARK_ID, Role.TANK,
                new AbilitySpec("shoulder_rush", () -> ticks(LdflConfig.bulwark.shoulderRushCooldown)),
                new AbilitySpec("shockwave", () -> ticks(LdflConfig.bulwark.shockwaveCooldown)),
                new AbilitySpec("tectonic_slam", 0),
                () -> LdflConfig.bulwark.ultCost),
            BulwarkKit::shoulderRush, BulwarkKit::shockwave, BulwarkKit::tectonicSlam)
            .withTick((p, d) -> {
                if (d.chargeTicks > 0) {
                    BulwarkKit.tickRush(p, d);
                }
                BarrierLogic.tickOwner(p, d);
            })
            .withCleanup(BarrierLogic::lower));

        MONOLITH = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(MONOLITH_ID, Role.TANK,
                new AbilitySpec("stone_skin", () -> ticks(LdflConfig.monolith.stoneSkinCooldown)),
                new AbilitySpec("upheaval", () -> ticks(LdflConfig.monolith.upheavalCooldown)),
                new AbilitySpec("bedrock_anchor", 0),
                () -> LdflConfig.monolith.ultCost),
            MonolithKit::stoneSkin, MonolithKit::upheaval, MonolithKit::bedrockAnchor)
            .withTick(MonolithKit::tick)
            .withCleanup(MonolithKit::cleanup));

        // --- Damage --------------------------------------------------------------------------
        FORGEWRIGHT = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(FORGEWRIGHT_ID, Role.DAMAGE,
                new AbilitySpec("deploy_sentry", () -> ticks(LdflConfig.forgewright.deploySentryCooldown)),
                new AbilitySpec("plating", () -> ticks(LdflConfig.forgewright.platingCooldown)),
                new AbilitySpec("slag_surge", 0),
                () -> LdflConfig.forgewright.ultCost),
            ForgewrightKit::deploySentry, ForgewrightKit::plating, ForgewrightKit::slagSurge)
            .withTick(ForgewrightKit::tick)
            .withCleanup((p, d) -> d.surgeTicks = 0));

        KESTREL = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(KESTREL_ID, Role.DAMAGE,
                new AbilitySpec("updraft", () -> ticks(LdflConfig.kestrel.updraftCooldown)),
                new AbilitySpec("marker_dart", () -> ticks(LdflConfig.kestrel.markerDartCooldown)),
                new AbilitySpec("long_vigil", 0),
                () -> LdflConfig.kestrel.ultCost),
            KestrelKit::updraft, KestrelKit::markerDart, KestrelKit::longVigil)
            .withTick(KestrelKit::tick)
            .withCleanup(KestrelKit::cleanup));

        // --- Supports ------------------------------------------------------------------------
        SOLACE = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(SOLACE_ID, Role.SUPPORT,
                new AbilitySpec("bloom_flask", () -> ticks(LdflConfig.solace.bloomFlaskCooldown)),
                new AbilitySpec("tailwind", () -> ticks(LdflConfig.solace.tailwindCooldown)),
                new AbilitySpec("sanctuary", 0),
                () -> LdflConfig.solace.ultCost),
            SolaceKit::bloomFlask, SolaceKit::tailwind, SolaceKit::sanctuary)
            .withTick(SolaceKit::tick)
            .withCleanup((p, d) -> SolaceKit.stopBeam(d)));

        CHIME = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(CHIME_ID, Role.SUPPORT,
                new AbilitySpec("resonance", () -> ticks(LdflConfig.chime.resonanceCooldown)),
                new AbilitySpec("ward_bell", () -> ticks(LdflConfig.chime.wardBellCooldown)),
                new AbilitySpec("crescendo", 0),
                () -> LdflConfig.chime.ultCost),
            ChimeKit::resonance, ChimeKit::wardBell, ChimeKit::crescendo)
            .withTick(ChimeKit::tick));
    }
}
