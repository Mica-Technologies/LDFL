package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.hero.bulwark.BulwarkKit;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySpec;
import com.micatechnologies.minecraft.ldfl.hero.forgewright.ForgewrightKit;
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

    public static HeroDefinition BULWARK;
    public static HeroDefinition FORGEWRIGHT;

    private Heroes() {
        throw new AssertionError("No instances.");
    }

    private static int seconds(int s) {
        return s * 20;
    }

    public static void init() {
        BULWARK = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(BULWARK_ID, Role.TANK,
                new AbilitySpec("shoulder_rush", seconds(8)),
                new AbilitySpec("shockwave", seconds(6)),
                new AbilitySpec("fault_line", 0),
                160.0F),
            BulwarkKit::shoulderRush, BulwarkKit::shockwave, BulwarkKit::faultLine));

        FORGEWRIGHT = HeroRegistry.register(new HeroDefinition(
            new HeroSpec(FORGEWRIGHT_ID, Role.DAMAGE,
                new AbilitySpec("deploy_sentry", seconds(10)),
                new AbilitySpec("plating", seconds(12)),
                new AbilitySpec("slag_surge", 0),
                200.0F),
            ForgewrightKit::deploySentry, ForgewrightKit::plating, ForgewrightKit::slagSurge));
    }
}
