package com.micatechnologies.minecraft.ldfl.hero.core;

import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * The rules data for one ability: its id and how long it is unavailable after use.
 *
 * <p>The cooldown is read through a supplier, so a server can retune it from config while running
 * without rebuilding any hero. For {@link AbilitySlot#ULTIMATE} the cooldown is ignored; an
 * ultimate is gated by a full ultimate meter instead (see {@link HeroState}).</p>
 */
public final class AbilitySpec {

    private final String id;
    private final IntSupplier cooldownTicks;

    /** An ability with a fixed cooldown. */
    public AbilitySpec(String id, int cooldownTicks) {
        this(id, () -> cooldownTicks);
        if (cooldownTicks < 0) {
            throw new IllegalArgumentException("cooldownTicks must be >= 0, was " + cooldownTicks);
        }
    }

    /** An ability whose cooldown is read each time it is used (negative values count as 0). */
    public AbilitySpec(String id, IntSupplier cooldownTicks) {
        this.id = Objects.requireNonNull(id, "id");
        this.cooldownTicks = Objects.requireNonNull(cooldownTicks, "cooldownTicks");
    }

    /** Lower-case id, unique within its hero. Lang key: {@code ldfl.ability.<hero>.<id>}. */
    public String id() {
        return id;
    }

    public int cooldownTicks() {
        return Math.max(0, cooldownTicks.getAsInt());
    }
}
