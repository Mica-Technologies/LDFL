package com.micatechnologies.minecraft.ldfl.hero.core;

/**
 * The three keybound ability slots every hero has. The ordinal is the wire format of the
 * use-ability packet, so do not reorder these.
 */
public enum AbilitySlot {
    ABILITY_1,
    ABILITY_2,
    /** Gated by the ultimate meter instead of a cooldown. */
    ULTIMATE;

    private static final AbilitySlot[] VALUES = values();

    /** Decode a slot from its ordinal; returns {@code null} for anything out of range. */
    public static AbilitySlot byOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
    }
}
