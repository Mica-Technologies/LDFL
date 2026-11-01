package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** A hero's rules ({@link HeroSpec}) plus the server-side behaviour of each ability. */
public final class HeroDefinition {

    private final HeroSpec spec;
    private final Map<AbilitySlot, HeroAbility> abilities = new EnumMap<>(AbilitySlot.class);

    public HeroDefinition(HeroSpec spec, HeroAbility ability1, HeroAbility ability2,
                          HeroAbility ultimate) {
        this.spec = Objects.requireNonNull(spec, "spec");
        abilities.put(AbilitySlot.ABILITY_1, Objects.requireNonNull(ability1, "ability1"));
        abilities.put(AbilitySlot.ABILITY_2, Objects.requireNonNull(ability2, "ability2"));
        abilities.put(AbilitySlot.ULTIMATE, Objects.requireNonNull(ultimate, "ultimate"));
    }

    public HeroSpec spec() {
        return spec;
    }

    public HeroAbility ability(AbilitySlot slot) {
        return abilities.get(slot);
    }
}
