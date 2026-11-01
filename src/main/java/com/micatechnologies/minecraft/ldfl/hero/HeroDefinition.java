package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * A hero's rules ({@link HeroSpec}), the server-side behaviour of each ability, and two optional
 * lifecycle hooks:
 *
 * <ul>
 *   <li>{@link #withTick tick}: runs every server tick while a player <em>is</em> this hero
 *       (channelled effects, timers, the HUD resource).</li>
 *   <li>{@link #withCleanup cleanup}: runs every server tick while a player is <em>not</em> this
 *       hero, and must be cheap and idempotent. It tears down anything the hero left behind (a
 *       raised barrier, a beam, a running buff) the moment the player switches away.</li>
 * </ul>
 */
public final class HeroDefinition {

    private static final BiConsumer<EntityPlayerMP, HeroData> NOTHING = (p, d) -> { };

    private final HeroSpec spec;
    private final Map<AbilitySlot, HeroAbility> abilities = new EnumMap<>(AbilitySlot.class);
    private BiConsumer<EntityPlayerMP, HeroData> tick = NOTHING;
    private BiConsumer<EntityPlayerMP, HeroData> cleanup = NOTHING;

    public HeroDefinition(HeroSpec spec, HeroAbility ability1, HeroAbility ability2,
                          HeroAbility ultimate) {
        this.spec = Objects.requireNonNull(spec, "spec");
        abilities.put(AbilitySlot.ABILITY_1, Objects.requireNonNull(ability1, "ability1"));
        abilities.put(AbilitySlot.ABILITY_2, Objects.requireNonNull(ability2, "ability2"));
        abilities.put(AbilitySlot.ULTIMATE, Objects.requireNonNull(ultimate, "ultimate"));
    }

    public HeroDefinition withTick(BiConsumer<EntityPlayerMP, HeroData> tick) {
        this.tick = Objects.requireNonNull(tick, "tick");
        return this;
    }

    public HeroDefinition withCleanup(BiConsumer<EntityPlayerMP, HeroData> cleanup) {
        this.cleanup = Objects.requireNonNull(cleanup, "cleanup");
        return this;
    }

    public HeroSpec spec() {
        return spec;
    }

    public HeroAbility ability(AbilitySlot slot) {
        return abilities.get(slot);
    }

    void tick(EntityPlayerMP player, HeroData data) {
        tick.accept(player, data);
    }

    void cleanup(EntityPlayerMP player, HeroData data) {
        cleanup.accept(player, data);
    }
}
