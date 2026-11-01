package com.micatechnologies.minecraft.ldfl.hero.core;

/**
 * Aiming maths in Minecraft's angle conventions, kept free of Minecraft types so it can be unit
 * tested. Yaw is degrees clockwise from south (+Z): 0 = south, 90 = west, 180 = north, -90 = east.
 * Pitch is degrees below horizontal.
 */
public final class Aim {

    private Aim() {
        throw new AssertionError("No instances.");
    }

    /** Yaw that faces along {@code (dx, dz)}. */
    public static float yaw(double dx, double dz) {
        return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
    }

    /** Pitch that faces along {@code (dx, dy, dz)}; positive looks down. */
    public static float pitch(double dx, double dy, double dz) {
        return (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
    }

    /** {@code angle} wrapped into [-180, 180). */
    public static float wrap(float angle) {
        float a = angle % 360.0F;
        if (a >= 180.0F) {
            a -= 360.0F;
        }
        if (a < -180.0F) {
            a += 360.0F;
        }
        return a;
    }

    /** Turn {@code from} toward {@code to} by at most {@code maxStep} degrees, the short way round. */
    public static float approach(float from, float to, float maxStep) {
        float delta = wrap(to - from);
        if (delta > maxStep) {
            delta = maxStep;
        } else if (delta < -maxStep) {
            delta = -maxStep;
        }
        return from + delta;
    }
}
