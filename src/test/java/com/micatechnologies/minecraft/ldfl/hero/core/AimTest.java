package com.micatechnologies.minecraft.ldfl.hero.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Pins {@link Aim} to Minecraft's yaw and pitch conventions. */
class AimTest {

    private static final float EPS = 1.0e-3F;

    @Test
    void yawMatchesMinecraftCompass() {
        assertEquals(0.0F, Aim.wrap(Aim.yaw(0, 1)), EPS, "south");
        assertEquals(90.0F, Aim.wrap(Aim.yaw(-1, 0)), EPS, "west");
        assertEquals(-180.0F, Aim.wrap(Aim.yaw(0, -1)), EPS, "north");
        assertEquals(-90.0F, Aim.wrap(Aim.yaw(1, 0)), EPS, "east");
    }

    @Test
    void pitchIsPositiveLookingDown() {
        assertEquals(45.0F, Aim.pitch(1, -1, 0), EPS);
        assertEquals(-45.0F, Aim.pitch(0, 1, 1), EPS);
        assertEquals(0.0F, Aim.pitch(3, 0, 4), EPS);
    }

    @Test
    void approachTakesTheShortWayAndClamps() {
        assertEquals(-170.0F, Aim.wrap(Aim.approach(170.0F, -170.0F, 30.0F)), EPS,
            "crossing the seam is a 20 degree turn, not 340");
        assertEquals(30.0F, Aim.approach(0.0F, 90.0F, 30.0F), EPS);
        assertEquals(-30.0F, Aim.approach(0.0F, -90.0F, 30.0F), EPS);
    }
}
