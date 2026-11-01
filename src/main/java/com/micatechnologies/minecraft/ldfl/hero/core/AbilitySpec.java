package com.micatechnologies.minecraft.ldfl.hero.core;

import java.util.Objects;

/**
 * The rules data for one ability: its id and how long it is unavailable after use.
 *
 * <p>For {@link AbilitySlot#ULTIMATE} the cooldown is ignored; an ultimate is gated by a full
 * ultimate meter instead (see {@link HeroState}).</p>
 */
public final class AbilitySpec {

    private final String id;
    private final int cooldownTicks;

    public AbilitySpec(String id, int cooldownTicks) {
        this.id = Objects.requireNonNull(id, "id");
        if (cooldownTicks < 0) {
            throw new IllegalArgumentException("cooldownTicks must be >= 0, was " + cooldownTicks);
        }
        this.cooldownTicks = cooldownTicks;
    }

    /** Lower-case id, unique within its hero. Lang key: {@code ldfl.ability.<hero>.<id>}. */
    public String id() {
        return id;
    }

    public int cooldownTicks() {
        return cooldownTicks;
    }
}
