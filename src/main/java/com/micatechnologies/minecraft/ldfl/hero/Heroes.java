package com.micatechnologies.minecraft.ldfl.hero;

import static com.micatechnologies.minecraft.ldfl.LdflConfig.ticks;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.bulwark.BulwarkKit;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySpec;
import com.micatechnologies.minecraft.ldfl.hero.forgewright.ForgewrightKit;
import com.micatechnologies.minecraft.ldfl.hero.solace.SolaceKit;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import com.micatechnologies.minecraft.ldfl.hero.core.Role;

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

    public static HeroDefinition BULWARK;
    public static HeroDefinition FORGEWRIGHT;
    public static HeroDefinition SOLACE;

    private Heroes() {
        throw new AssertionError("No instances.");
    }

    /**
     * Register every hero. Cooldowns and ult costs are read from {@link LdflConfig} each time they
     * are used, so the lambdas below must stay lambdas: capturing the value here would freeze it.
     */
    public static void init() {
        BULWARK = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(BULWARK_ID, Role.TANK,
                new AbilitySpec("shoulder_rush", () -> ticks(LdflConfig.bulwark.shoulderRushCooldown)),
                new AbilitySpec("shockwave", () -> ticks(LdflConfig.bulwark.shockwaveCooldown)),
                new AbilitySpec("tectonic_slam", 0),
                () -> LdflConfig.bulwark.ultCost),
            BulwarkKit::shoulderRush, BulwarkKit::shockwave, BulwarkKit::tectonicSlam));

        FORGEWRIGHT = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(FORGEWRIGHT_ID, Role.DAMAGE,
                new AbilitySpec("deploy_sentry", () -> ticks(LdflConfig.forgewright.deploySentryCooldown)),
                new AbilitySpec("plating", () -> ticks(LdflConfig.forgewright.platingCooldown)),
                new AbilitySpec("slag_surge", 0),
                () -> LdflConfig.forgewright.ultCost),
            ForgewrightKit::deploySentry, ForgewrightKit::plating, ForgewrightKit::slagSurge));

        SOLACE = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(SOLACE_ID, Role.SUPPORT,
                new AbilitySpec("bloom_flask", () -> ticks(LdflConfig.solace.bloomFlaskCooldown)),
                new AbilitySpec("tailwind", () -> ticks(LdflConfig.solace.tailwindCooldown)),
                new AbilitySpec("sanctuary", 0),
                () -> LdflConfig.solace.ultCost),
            SolaceKit::bloomFlask, SolaceKit::tailwind, SolaceKit::sanctuary));
    }
}
