package com.micatechnologies.minecraft.ldfl.hero.core;

/**
 * The three hero roles. Every hero belongs to exactly one, and the role decides the passive a
 * player gets while that hero is active.
 *
 * <p>Pure Java, like the rest of {@code hero.core}: the Minecraft side ({@code hero.HeroPassives})
 * turns a role into attribute modifiers and event effects.</p>
 */
public enum Role {
    /** Frontline: extra armor, extra health and knockback resistance. */
    TANK("tank"),
    /** Pressure: hits leave the target "Wounded", which cuts the healing it receives. */
    DAMAGE("damage"),
    /** Sustain: regenerates health after a few seconds out of combat. */
    SUPPORT("support");

    private final String id;

    Role(String id) {
        this.id = id;
    }

    /** Lower-case id, used for lang keys ({@code ldfl.role.<id>}) and config. */
    public String id() {
        return id;
    }
}
