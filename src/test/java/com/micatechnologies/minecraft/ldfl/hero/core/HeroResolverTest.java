package com.micatechnologies.minecraft.ldfl.hero.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class HeroResolverTest {

    @Test
    void mainhandWins() {
        assertEquals("forgewright", HeroResolver.resolve("forgewright", "bulwark"));
    }

    @Test
    void offhandCountsWhenMainhandIsNotASignature() {
        assertEquals("bulwark", HeroResolver.resolve(null, "bulwark"));
    }

    @Test
    void emptyHandsMeanNoHero() {
        assertNull(HeroResolver.resolve(null, null));
    }

    @Test
    void slotOrdinalDecodingRejectsGarbage() {
        assertEquals(AbilitySlot.ULTIMATE, AbilitySlot.byOrdinal(2));
        assertNull(AbilitySlot.byOrdinal(3));
        assertNull(AbilitySlot.byOrdinal(-1));
    }
}
