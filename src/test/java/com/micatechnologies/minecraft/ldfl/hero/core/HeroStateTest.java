package com.micatechnologies.minecraft.ldfl.hero.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.micatechnologies.minecraft.ldfl.hero.core.HeroState.Activation;
import org.junit.jupiter.api.Test;

/**
 * Rules tests for {@link HeroState}. Pure Java, no game instance: each test pins a property the
 * hero system relies on, not a tuned number.
 */
class HeroStateTest {

    private static final HeroSpec TANK = new HeroSpec("tank_hero", Role.TANK,
        new AbilitySpec("a1", 40), new AbilitySpec("a2", 100), new AbilitySpec("ult", 0), 200.0F);
    private static final HeroSpec SUPPORT = new HeroSpec("support_hero", Role.SUPPORT,
        new AbilitySpec("a1", 60), new AbilitySpec("a2", 60), new AbilitySpec("ult", 0), 100.0F);

    @Test
    void nothingFiresWithoutAHero() {
        HeroState s = new HeroState();
        assertEquals(Activation.NO_HERO, s.check(AbilitySlot.ABILITY_1));
        assertEquals(Activation.NO_HERO, s.check(AbilitySlot.ULTIMATE));
        s.addUltPoints(1000.0F);
        assertEquals(0.0F, s.ultCharge());
    }

    @Test
    void commitStartsCooldownAndTickingClearsIt() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        assertEquals(Activation.OK, s.check(AbilitySlot.ABILITY_1));
        s.commit(AbilitySlot.ABILITY_1);
        assertEquals(Activation.ON_COOLDOWN, s.check(AbilitySlot.ABILITY_1));
        assertEquals(Activation.OK, s.check(AbilitySlot.ABILITY_2), "slots cool down independently");
        for (int i = 0; i < 39; i++) {
            s.tick();
        }
        assertEquals(Activation.ON_COOLDOWN, s.check(AbilitySlot.ABILITY_1));
        s.tick();
        assertEquals(Activation.OK, s.check(AbilitySlot.ABILITY_1));
        s.tick();
        assertEquals(0, s.cooldown(AbilitySlot.ABILITY_1), "cooldowns never go negative");
    }

    @Test
    void checkAloneConsumesNothing() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        s.check(AbilitySlot.ABILITY_1);
        s.check(AbilitySlot.ABILITY_1);
        assertEquals(0, s.cooldown(AbilitySlot.ABILITY_1));
    }

    @Test
    void ultimateIsGatedByChargeNotCooldown() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        assertEquals(Activation.ULT_NOT_READY, s.check(AbilitySlot.ULTIMATE));
        s.addUltPoints(100.0F);
        assertEquals(0.5F, s.ultCharge(), 1.0e-6F);
        s.addUltPoints(150.0F);
        assertEquals(1.0F, s.ultCharge(), "meter clamps at full");
        assertEquals(Activation.OK, s.check(AbilitySlot.ULTIMATE));
        s.commit(AbilitySlot.ULTIMATE);
        assertEquals(0.0F, s.ultCharge());
        assertEquals(Activation.ULT_NOT_READY, s.check(AbilitySlot.ULTIMATE));
    }

    @Test
    void negativeOrNanPointsAreIgnored() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        s.addUltPoints(-50.0F);
        s.addUltPoints(Float.NaN);
        assertEquals(0.0F, s.ultCharge());
    }

    @Test
    void swappingHeroResetsCooldownsAndUlt() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        s.commit(AbilitySlot.ABILITY_1);
        s.addUltPoints(150.0F);
        assertTrue(s.setHero(SUPPORT));
        assertSame(SUPPORT, s.hero());
        assertEquals(0, s.cooldown(AbilitySlot.ABILITY_1));
        assertEquals(0.0F, s.ultCharge());
    }

    @Test
    void reselectingSameHeroKeepsState() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        s.commit(AbilitySlot.ABILITY_2);
        s.addUltPoints(50.0F);
        assertFalse(s.setHero(TANK));
        assertEquals(100, s.cooldown(AbilitySlot.ABILITY_2));
        assertEquals(0.25F, s.ultCharge(), 1.0e-6F);
    }

    @Test
    void droppingHeroClearsIt() {
        HeroState s = new HeroState();
        s.setHero(TANK);
        assertTrue(s.setHero(null));
        assertNull(s.hero());
        assertFalse(s.setHero(null));
    }
}
