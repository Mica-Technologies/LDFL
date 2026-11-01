package com.micatechnologies.minecraft.ldfl.hero.core;

/**
 * Decides which hero a player is from the signature items in their hands (decision D3).
 *
 * <p>The mainhand wins; the offhand counts only when the mainhand holds no signature item. That
 * lets a tank hold the barrier shield in the offhand and swing anything in the mainhand, while a
 * player who deliberately takes another hero's weapon in hand becomes that hero.</p>
 */
public final class HeroResolver {

    private HeroResolver() {
        throw new AssertionError("No instances.");
    }

    /**
     * @param mainhandHero hero id of the mainhand signature item, or {@code null} if none
     * @param offhandHero  hero id of the offhand signature item, or {@code null} if none
     * @return the active hero id, or {@code null} when the player is not a hero
     */
    public static String resolve(String mainhandHero, String offhandHero) {
        return mainhandHero != null ? mainhandHero : offhandHero;
    }
}
