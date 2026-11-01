package com.micatechnologies.minecraft.ldfl.hero.core;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * The rules data for one hero: id, role, one {@link AbilitySpec} per {@link AbilitySlot}, and
 * how much damage or healing fills the ultimate meter.
 */
public final class HeroSpec {

    private final String id;
    private final Role role;
    private final Map<AbilitySlot, AbilitySpec> abilities;
    private final float ultCost;

    /**
     * @param ultCost points of damage dealt or healing done that fill the ultimate meter from empty
     *                to full. Must be positive.
     */
    public HeroSpec(String id, Role role, AbilitySpec ability1, AbilitySpec ability2,
                    AbilitySpec ultimate, float ultCost) {
        this.id = Objects.requireNonNull(id, "id");
        this.role = Objects.requireNonNull(role, "role");
        if (!(ultCost > 0.0F)) {
            throw new IllegalArgumentException("ultCost must be > 0, was " + ultCost);
        }
        this.ultCost = ultCost;
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
        return ultCost;
    }
}
