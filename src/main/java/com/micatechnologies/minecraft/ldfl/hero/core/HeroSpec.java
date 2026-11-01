package com.micatechnologies.minecraft.ldfl.hero.core;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.DoubleSupplier;

/**
 * The rules data for one hero: id, role, one {@link AbilitySpec} per {@link AbilitySlot}, and
 * how much damage or healing fills the ultimate meter.
 */
public final class HeroSpec {

    /** Floor for the ult cost, so a zero or negative config value cannot divide by zero. */
    private static final float MIN_ULT_COST = 1.0F;

    private final String id;
    private final Role role;
    private final Map<AbilitySlot, AbilitySpec> abilities;
    private final DoubleSupplier ultCost;

    /**
     * @param ultCost points of damage dealt or healing done that fill the ultimate meter from empty
     *                to full. Must be positive.
     */
    public HeroSpec(String id, Role role, AbilitySpec ability1, AbilitySpec ability2,
                    AbilitySpec ultimate, float ultCost) {
        this(id, role, ability1, ability2, ultimate, () -> ultCost);
        if (!(ultCost > 0.0F)) {
            throw new IllegalArgumentException("ultCost must be > 0, was " + ultCost);
        }
    }

    /** As above, with the ult cost read each time it is needed (clamped to at least 1). */
    public HeroSpec(String id, Role role, AbilitySpec ability1, AbilitySpec ability2,
                    AbilitySpec ultimate, DoubleSupplier ultCost) {
        this.id = Objects.requireNonNull(id, "id");
        this.role = Objects.requireNonNull(role, "role");
        this.ultCost = Objects.requireNonNull(ultCost, "ultCost");
        EnumMap<AbilitySlot, AbilitySpec> map = new EnumMap<>(AbilitySlot.class);
        map.put(AbilitySlot.ABILITY_1, Objects.requireNonNull(ability1, "ability1"));
        map.put(AbilitySlot.ABILITY_2, Objects.requireNonNull(ability2, "ability2"));
        map.put(AbilitySlot.ULTIMATE, Objects.requireNonNull(ultimate, "ultimate"));
        this.abilities = map;
    }

    /** Lower-case id, unique across heroes. Lang key: {@code ldfl.hero.<id>}. */
    public String id() {
        return id;
    }

    public Role role() {
        return role;
    }

    public AbilitySpec ability(AbilitySlot slot) {
        return abilities.get(slot);
    }

    public float ultCost() {
        double v = ultCost.getAsDouble();
        return v >= MIN_ULT_COST ? (float) v : MIN_ULT_COST;
    }
}
