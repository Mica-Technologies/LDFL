package com.micatechnologies.minecraft.ldfl.hero.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** Config-driven tuning: values are read live, and bad values cannot break the rules. */
class HeroSpecTest {

    @Test
    void cooldownIsReadEachTimeItIsCommitted() {
        AtomicInteger cooldown = new AtomicInteger(40);
        HeroSpec spec = new HeroSpec("h", Role.TANK, new AbilitySpec("a1", cooldown::get),
            new AbilitySpec("a2", 10), new AbilitySpec("u", 0), 100.0F);
        HeroState s = new HeroState();
        s.setHero(spec);
        s.commit(AbilitySlot.ABILITY_1);
        assertEquals(40, s.cooldown(AbilitySlot.ABILITY_1));

        cooldown.set(5);
        for (int i = 0; i < 40; i++) {
            s.tick();
        }
        s.commit(AbilitySlot.ABILITY_1);
        assertEquals(5, s.cooldown(AbilitySlot.ABILITY_1), "a retuned cooldown applies on next use");
    }

    @Test
    void negativeConfiguredCooldownCountsAsZero() {
        assertEquals(0, new AbilitySpec("a", () -> -20).cooldownTicks());
    }

    @Test
    void badUltCostIsClampedSoChargingNeverDividesByZero() {
        HeroSpec zero = new HeroSpec("h", Role.SUPPORT, new AbilitySpec("a1", 1),
            new AbilitySpec("a2", 1), new AbilitySpec("u", 0), () -> 0.0D);
        HeroSpec nan = new HeroSpec("h2", Role.SUPPORT, new AbilitySpec("a1", 1),
            new AbilitySpec("a2", 1), new AbilitySpec("u", 0), () -> Double.NaN);
        assertEquals(1.0F, zero.ultCost());
        assertEquals(1.0F, nan.ultCost());
        HeroState s = new HeroState();
        s.setHero(zero);
        s.addUltPoints(0.5F);
        assertEquals(0.5F, s.ultCharge(), 1.0e-6F);
    }
}
