package com.micatechnologies.minecraft.ldfl.hero.bulwark;

import com.micatechnologies.minecraft.ldfl.Ldfl;
import com.micatechnologies.minecraft.ldfl.entity.EntityBarrier;
import com.micatechnologies.minecraft.ldfl.entity.EntityBolt;
import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import com.micatechnologies.minecraft.ldfl.item.ItemRampartShield;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Bulwark's barrier: raising and lowering the panel, its health and regeneration, and what it
 * blocks.
 *
 * <p>Blocking is geometric (see {@link EntityBarrier}). Projectiles are tested at the
 * <em>start</em> of each world tick, against the path they are about to travel that tick, so a
 * projectile is stopped before its own update can reach the people behind the panel. Melee is
 * stopped in {@link LivingAttackEvent} when the attacker and target are on opposite sides of a
 * panel.</p>
 *
 * <p>Only enemies of the barrier's owner are blocked: allies can shoot and swing through it.</p>
 */
public final class BarrierLogic {

    public static final float MAX_HEALTH = 60.0F;
    private static final float REGEN_PER_TICK = 6.0F / 20.0F;
    private static final int REGEN_DELAY_TICKS = 40;
    private static final int BROKEN_LOCKOUT_TICKS = 100;
    /** Raise the panel only after the shield has been held up this long, so a tap does nothing. */
    private static final int RAISE_DELAY_TICKS = 4;

    // --- Owner lifecycle (called from HeroEvents each tick) -----------------------------------

    /** Raise, hold, lower and regenerate the barrier for a player who is Bulwark. */
    public static void tickOwner(EntityPlayerMP player, HeroData data) {
        if (Float.isNaN(data.barrierHealth)) {
            data.barrierHealth = MAX_HEALTH;
        }
        if (data.barrierBrokenTicks > 0) {
            data.barrierBrokenTicks--;
        }

        boolean wantsRaised = data.barrierBrokenTicks == 0 && data.barrierHealth > 0.0F
            && player.isHandActive()
            && player.getActiveItemStack().getItem() instanceof ItemRampartShield
            && player.getItemInUseMaxCount() >= RAISE_DELAY_TICKS;

        if (wantsRaised && (data.barrier == null || data.barrier.isDead)) {
            data.barrier = new EntityBarrier(player.world, player);
            boolean spawned = player.world.spawnEntity(data.barrier);
            Ldfl.LOGGER.debug("Barrier raised for {} (spawned={}, health={})", player.getName(),
                spawned, data.barrierHealth);
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.PLAYERS, 0.6F, 1.6F);
        } else if (!wantsRaised) {
            lower(player, data);
        }

        if (data.barrier == null
            && player.ticksExisted - data.barrierLastHitTick >= REGEN_DELAY_TICKS
            && data.barrierBrokenTicks == 0) {
            data.barrierHealth = Math.min(MAX_HEALTH, data.barrierHealth + REGEN_PER_TICK);
        }
        if (data.barrier != null) {
            data.barrier.setHealthFraction(data.barrierHealth / MAX_HEALTH);
        }
        data.resource = data.barrierHealth / MAX_HEALTH;
    }

    /** Remove the panel if it is up. Safe to call for any player. */
    public static void lower(EntityPlayer player, HeroData data) {
        if (data.barrier != null) {
            if (!data.barrier.isDead) {
                Ldfl.LOGGER.debug("Barrier lowered for {}", player.getName());
                data.barrier.setDead();
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 0.5F, 1.6F);
            }
            data.barrier = null;
        }
    }

    /** Take {@code amount} damage on the panel; breaks it at zero. */
    private static void damage(EntityBarrier barrier, EntityPlayer owner, HeroData data,
                               float amount, Vec3d at) {
        data.barrierHealth = Math.max(0.0F, data.barrierHealth - amount);
        data.barrierLastHitTick = owner.ticksExisted;
        Ldfl.LOGGER.debug("Barrier of {} absorbed {} at {} (health now {})", owner.getName(),
            amount, at, data.barrierHealth);
        WorldServer world = (WorldServer) barrier.world;
        world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, at.x, at.y, at.z, 6,
            0.1D, 0.1D, 0.1D, 0.05D);
        if (data.barrierHealth <= 0.0F) {
            data.barrierBrokenTicks = BROKEN_LOCKOUT_TICKS;
            lower(owner, data);
            world.playSound(null, barrier.posX, barrier.posY, barrier.posZ,
                SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.2F, 0.7F);
        } else {
            world.playSound(null, at.x, at.y, at.z, SoundEvents.BLOCK_GLASS_HIT,
                SoundCategory.PLAYERS, 0.8F, 1.4F);
            barrier.setHealthFraction(data.barrierHealth / MAX_HEALTH);
        }
    }

    // --- Projectiles -------------------------------------------------------------------------

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.world.isRemote) {
            return;
        }
        List<EntityBarrier> barriers = new ArrayList<>();
        for (Entity e : event.world.loadedEntityList) {
            if (e instanceof EntityBarrier && !e.isDead) {
                barriers.add((EntityBarrier) e);
            }
        }
        for (EntityBarrier barrier : barriers) {
            EntityPlayer owner = barrier.getOwner();
            HeroData data = owner == null ? null : HeroCapability.get(owner);
            if (data == null || data.barrier != barrier) {
                continue;
            }
            AxisAlignedBB reach = barrier.getEntityBoundingBox().grow(6.0D);
            for (Entity p : event.world.getEntitiesWithinAABBExcludingEntity(barrier, reach)) {
                if (barrier.isDead) {
                    break;
                }
                if (p instanceof EntityLivingBase && !p.isDead && Targeting.isEnemy(owner, p)) {
                    holdBack(barrier, (EntityLivingBase) p);
                    continue;
                }
                if (!isProjectile(p) || p.isDead) {
                    continue;
                }
                Entity shooter = shooterOf(p);
                if (shooter == owner || (shooter != null && !Targeting.isEnemy(owner, shooter))) {
                    continue;
                }
                Vec3d from = new Vec3d(p.posX, p.posY, p.posZ);
                Vec3d to = from.add(p.motionX, p.motionY, p.motionZ);
                Vec3d hit = barrier.intersectFromFront(from, to);
                if (hit != null) {
                    p.setDead();
                    damage(barrier, owner, data, projectileDamage(p), hit);
                }
            }
        }
    }

    /** Enemies may come up to the panel but not walk through it: push any that try back out. */
    private static final double STANDOFF = 0.6D;
    /** How far past the plane an enemy can be and still be pushed back (not one already behind). */
    private static final double PUSH_DEPTH = 1.2D;

    private static void holdBack(EntityBarrier barrier, EntityLivingBase e) {
        Vec3d centre = new Vec3d(e.posX, e.posY + e.height * 0.5D, e.posZ);
        double d = barrier.side(centre);
        if (d >= STANDOFF || d < -PUSH_DEPTH || !barrier.withinPanel(centre.subtract(
            barrier.normal().scale(d)))) {
            return;
        }
        Vec3d n = barrier.normal();
        double push = STANDOFF - d;
        double inward = e.motionX * n.x + e.motionZ * n.z;
        if (inward < 0.0D) {
            e.motionX -= n.x * inward;
            e.motionZ -= n.z * inward;
        }
        if (e instanceof EntityPlayerMP) {
            ((EntityPlayerMP) e).connection.setPlayerLocation(e.posX + n.x * push, e.posY,
                e.posZ + n.z * push, e.rotationYaw, e.rotationPitch);
        } else {
            e.setPosition(e.posX + n.x * push, e.posY, e.posZ + n.z * push);
            e.velocityChanged = true;
        }
    }

    private static boolean isProjectile(Entity e) {
        return e instanceof EntityArrow || e instanceof EntityThrowable || e instanceof EntityFireball;
    }

    private static Entity shooterOf(Entity p) {
        if (p instanceof EntityBolt) {
            // A sentry's bolt counts as its owner's shot.
            return ((EntityBolt) p).getCreditedShooter();
        }
        if (p instanceof EntityArrow) {
            return ((EntityArrow) p).shootingEntity;
        }
        if (p instanceof EntityThrowable) {
            return ((EntityThrowable) p).getThrower();
        }
        if (p instanceof EntityFireball) {
            return ((EntityFireball) p).shootingEntity;
        }
        return null;
    }

    /** Roughly what the projectile would have dealt, mirroring vanilla's arrow formula. */
    private static float projectileDamage(Entity p) {
        if (p instanceof EntityArrow) {
            double speed = Math.sqrt(p.motionX * p.motionX + p.motionY * p.motionY
                + p.motionZ * p.motionZ);
            return (float) Math.ceil(speed * ((EntityArrow) p).getDamage());
        }
        if (p instanceof EntityFireball) {
            return 8.0F;
        }
        return 2.0F;
    }

    // --- Melee -------------------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target.world.isRemote) {
            return;
        }
        Entity direct = event.getSource().getImmediateSource();
        if (!(direct instanceof EntityLivingBase) || direct == target) {
            return;
        }
        Vec3d from = new Vec3d(direct.posX, direct.posY + direct.height * 0.5D, direct.posZ);
        Vec3d to = new Vec3d(target.posX, target.posY + target.height * 0.5D, target.posZ);
        AxisAlignedBB around = target.getEntityBoundingBox().grow(EntityBarrier.HALF_WIDTH + 4.0D);
        for (EntityBarrier barrier : target.world.getEntitiesWithinAABB(EntityBarrier.class, around)) {
            EntityPlayer owner = barrier.getOwner();
            HeroData data = owner == null ? null : HeroCapability.get(owner);
            if (data == null || data.barrier != barrier
                || !Targeting.isEnemy(owner, direct) || !Targeting.isAlly(owner, target)) {
                continue;
            }
            Vec3d hit = barrier.intersectFromFront(from, to);
            if (hit != null) {
                event.setCanceled(true);
                damage(barrier, owner, data, event.getAmount(), hit);
                return;
            }
        }
    }
}
