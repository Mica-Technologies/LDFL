package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Friend-or-foe for every hero effect (decision D6). Every ability, turret and passive asks this
 * class, so the PvP config and team rules apply the same way everywhere.
 */
public final class Targeting {

    private Targeting() {
        throw new AssertionError("No instances.");
    }

    /**
     * Whether {@code actor} may harm {@code target}.
     *
     * <ul>
     *   <li>Hostile mobs ({@link IMob}) are always enemies unless they are on the actor's team or
     *       owned by the actor.</li>
     *   <li>Players are enemies only when {@code combat.pvp} is on and they are not on the
     *       actor's scoreboard team, creative or spectating.</li>
     *   <li>Passive mobs, villagers, golems and pets are never enemies.</li>
     * </ul>
     */
    public static boolean isEnemy(EntityLivingBase actor, Entity target) {
        if (!(target instanceof EntityLivingBase) || target == actor || !target.isEntityAlive()) {
            return false;
        }
        if (actor.isOnSameTeam(target)) {
            return false;
        }
        if (target instanceof IEntityOwnable && ((IEntityOwnable) target).getOwner() == actor) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) target;
            return LdflConfig.combat.pvp && !p.isCreative() && !p.isSpectator();
        }
        return target instanceof IMob;
    }

    /**
     * Whether {@code actor} may help {@code target} (heals, buffs). Allies are the actor itself,
     * players on the actor's team, and, with PvP off, every player.
     */
    public static boolean isAlly(EntityLivingBase actor, Entity target) {
        if (!(target instanceof EntityLivingBase) || !target.isEntityAlive()) {
            return false;
        }
        if (target == actor || actor.isOnSameTeam(target)) {
            return true;
        }
        if (target instanceof EntityPlayer) {
            return !LdflConfig.combat.pvp;
        }
        return target instanceof IEntityOwnable && ((IEntityOwnable) target).getOwner() == actor;
    }
}
