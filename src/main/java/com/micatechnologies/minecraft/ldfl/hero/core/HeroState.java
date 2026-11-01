package com.micatechnologies.minecraft.ldfl.hero.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Mutable per-player hero state: which hero is active, how long each ability has left on
 * cooldown, and how full the ultimate meter is (0 to 1).
 *
 * <p>The server owns the real copy; the client holds a synced mirror for the HUD (decision D8).
 * Swapping to a different hero resets everything, so a player cannot dodge cooldowns or bank
 * ultimate charge by juggling items.</p>
 */
public final class HeroState {

    /** Result of an activation check. */
    public enum Activation {
        OK,
        NO_HERO,
        ON_COOLDOWN,
        ULT_NOT_READY
    }

    private HeroSpec hero;
    private final int[] cooldowns = new int[AbilitySlot.values().length];
    private float ultCharge;

    /** The active hero, or {@code null} when the player is not a hero. */
    public HeroSpec hero() {
        return hero;
    }

    /**
     * Switch to {@code next} (or to no hero with {@code null}). Switching to the hero already active
     * does nothing; any real change clears cooldowns and the ultimate meter.
     *
     * @return {@code true} when the active hero changed
     */
    public boolean setHero(HeroSpec next) {
        String nextId = next == null ? null : next.id();
        String curId = hero == null ? null : hero.id();
        if (Objects.equals(nextId, curId)) {
            return false;
        }
        hero = next;
        Arrays.fill(cooldowns, 0);
        ultCharge = 0.0F;
        return true;
    }

    /** Count every cooldown down by one tick. */
    public void tick() {
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) {
                cooldowns[i]--;
            }
        }
    }

    public int cooldown(AbilitySlot slot) {
        return cooldowns[slot.ordinal()];
    }

    /** 0 = empty, 1 = ready. */
    public float ultCharge() {
        return ultCharge;
    }

    public boolean ultReady() {
        return ultCharge >= 1.0F;
    }

    /**
     * Check whether {@code slot} can fire. This consumes nothing; call {@link #commit} once the
     * ability has actually run, so an ability that finds no valid target costs nothing.
     */
    public Activation check(AbilitySlot slot) {
        if (hero == null) {
            return Activation.NO_HERO;
        }
        if (slot == AbilitySlot.ULTIMATE) {
            return ultReady() ? Activation.OK : Activation.ULT_NOT_READY;
        }
        return cooldowns[slot.ordinal()] > 0 ? Activation.ON_COOLDOWN : Activation.OK;
    }

    /** Spend {@code slot}: start its cooldown, or empty the meter for the ultimate. */
    public void commit(AbilitySlot slot) {
        if (hero == null) {
            return;
        }
        if (slot == AbilitySlot.ULTIMATE) {
            ultCharge = 0.0F;
        } else {
            cooldowns[slot.ordinal()] = hero.ability(slot).cooldownTicks();
        }
    }

    /**
     * Add charge for {@code points} of damage dealt or healing done, scaled by the hero's ult cost.
     * The meter never exceeds 1. Zero, negative and NaN points are ignored.
     */
    public void addUltPoints(float points) {
        if (hero == null || !(points > 0.0F)) {
            return;
        }
        ultCharge = Math.min(1.0F, ultCharge + points / hero.ultCost());
    }

    /**
     * Overwrite the mirror from a server sync (client side). Set the hero with {@link #setHero}
     * first, because a hero change clears these values.
     */
    public void applySync(int[] syncedCooldowns, float syncedUltCharge) {
        System.arraycopy(syncedCooldowns, 0, cooldowns, 0,
            Math.min(cooldowns.length, syncedCooldowns.length));
        ultCharge = Math.max(0.0F, Math.min(1.0F, syncedUltCharge));
    }

    /** A copy of the cooldowns in slot-ordinal order, for syncing. */
    public int[] cooldownSnapshot() {
        return cooldowns.clone();
    }
}
