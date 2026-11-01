package com.micatechnologies.minecraft.ldfl;

/**
 * Mod-wide constants. Never hard-code the mod id, name or version elsewhere; they come from the
 * build-generated {@code Tags} class, and the version is derived from the latest git tag.
 */
public final class LdflConstants {

    public static final String MOD_NAMESPACE = Tags.MODID;
    public static final String MOD_NAME = Tags.MODNAME;
    public static final String MOD_VERSION = Tags.VERSION;

    public static final int TICKS_PER_SECOND = 20;

    private LdflConstants() {
        throw new AssertionError("No instances.");
    }
}
