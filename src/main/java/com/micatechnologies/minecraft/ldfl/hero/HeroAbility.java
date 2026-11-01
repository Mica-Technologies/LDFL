package com.micatechnologies.minecraft.ldfl.hero;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Server-side behaviour of one ability. Called only after the cooldown or ultimate check passed.
 */
@FunctionalInterface
public interface HeroAbility {

    /**
     * Run the ability.
     *
     * @return {@code true} if it fired and its cost should be spent; {@code false} if it could not
     *         run (no room to deploy, no target, and so on), which costs nothing
     */
    boolean activate(EntityPlayerMP player, HeroData data);
}
