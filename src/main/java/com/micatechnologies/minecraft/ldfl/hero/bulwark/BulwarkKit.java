package com.micatechnologies.minecraft.ldfl.hero.bulwark;

import com.micatechnologies.minecraft.ldfl.LdflSounds;
import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

/**
 * Bulwark, the Tank: the server-side behaviour of its three abilities.
 *
 * <ul>
 *   <li><b>Shoulder Rush</b>: a short forward dash. The first enemy it runs into takes damage and
 *       is thrown back, which ends the dash.</li>
 *   <li><b>Shockwave</b>: a line of force along the look direction, up to 16 blocks, that stops at
 *       the first solid block and hits each enemy along it once.</li>
 *   <li><b>Tectonic Slam</b> (ultimate): a forward ground slam. Enemies in a cone take damage, are
 *       knocked up and are left slowed and weakened.</li>
 * </ul>
 */
public final class BulwarkKit {

    private static final int RUSH_TICKS = 10;
    private static final double RUSH_SPEED = 1.6D;

    private static final int SHOCKWAVE_RANGE = 16;

    private static final double SLAM_RANGE = 8.0D;
    private static final double SLAM_HALF_ANGLE_COS = Math.cos(Math.toRadians(50.0D));
    private static final int SLAM_STAGGER_TICKS = 50;

    private BulwarkKit() {
        throw new AssertionError("No instances.");
    }

    /** Horizontal unit vector of the player's facing. */
    private static Vec3d flatLook(EntityPlayerMP p) {
        float yaw = p.rotationYaw * 0.017453292F;
        return new Vec3d(-MathHelper.sin(yaw), 0.0D, MathHelper.cos(yaw));
    }

    // --- Shoulder Rush -----------------------------------------------------------------------

    public static boolean shoulderRush(EntityPlayerMP p, HeroData data) {
        Vec3d dir = flatLook(p);
        p.motionX = dir.x * RUSH_SPEED;
        p.motionZ = dir.z * RUSH_SPEED;
        if (p.onGround) {
            p.motionY = 0.15D;
        }
        p.velocityChanged = true;
        data.chargeTicks = RUSH_TICKS;
        p.world.playSound(null, p.posX, p.posY, p.posZ, LdflSounds.RUSH,
            SoundCategory.PLAYERS, 1.0F, 0.7F);
        return true;
    }

    /** Called every server tick while {@code data.chargeTicks > 0}. */
    public static void tickRush(EntityPlayerMP p, HeroData data) {
        data.chargeTicks--;
        AxisAlignedBB box = p.getEntityBoundingBox().grow(0.6D);
        for (Entity e : p.world.getEntitiesWithinAABBExcludingEntity(p, box)) {
            if (Targeting.isEnemy(p, e)) {
                EntityLivingBase target = (EntityLivingBase) e;
                target.attackEntityFrom(DamageSource.causePlayerDamage(p), (float) LdflConfig.bulwark.shoulderRushDamage);
                float yaw = p.rotationYaw * 0.017453292F;
                target.knockBack(p, 1.5F, MathHelper.sin(yaw), -MathHelper.cos(yaw));
                p.motionX *= 0.2D;
                p.motionZ *= 0.2D;
                p.velocityChanged = true;
                data.chargeTicks = 0;
                p.world.playSound(null, target.posX, target.posY, target.posZ,
                    SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 1.0F, 0.8F);
                return;
            }
        }
    }

    // --- Shockwave ---------------------------------------------------------------------------

    public static boolean shockwave(EntityPlayerMP p, HeroData data) {
        WorldServer world = p.getServerWorld();
        Vec3d start = p.getPositionEyes(1.0F);
        Vec3d dir = p.getLookVec();
        Set<Entity> hit = new HashSet<>();
        for (int i = 1; i <= SHOCKWAVE_RANGE; i++) {
            Vec3d at = start.add(dir.scale(i));
            Material m = world.getBlockState(new BlockPos(at)).getMaterial();
            if (m.blocksMovement()) {
                break;
            }
            world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, at.x, at.y, at.z, 6,
                0.25D, 0.25D, 0.25D, 0.05D);
            List<Entity> near = world.getEntitiesWithinAABBExcludingEntity(p,
                new AxisAlignedBB(at.x - 1, at.y - 1, at.z - 1, at.x + 1, at.y + 1, at.z + 1));
            for (Entity e : near) {
                if (!hit.contains(e) && Targeting.isEnemy(p, e)) {
                    hit.add(e);
                    e.attackEntityFrom(DamageSource.causePlayerDamage(p), (float) LdflConfig.bulwark.shockwaveDamage);
                    ((EntityLivingBase) e).knockBack(p, 0.6F, -dir.x, -dir.z);
                }
            }
        }
        world.playSound(null, p.posX, p.posY, p.posZ, LdflSounds.SHOCKWAVE,
            SoundCategory.PLAYERS, 1.0F, 0.6F);
        return true;
    }

    // --- Tectonic Slam (ultimate) ---------------------------------------------------------------

    public static boolean tectonicSlam(EntityPlayerMP p, HeroData data) {
        WorldServer world = p.getServerWorld();
        Vec3d look = flatLook(p);
        AxisAlignedBB area = p.getEntityBoundingBox().grow(SLAM_RANGE, 3.0D, SLAM_RANGE);
        for (Entity e : world.getEntitiesWithinAABBExcludingEntity(p, area)) {
            if (!Targeting.isEnemy(p, e)) {
                continue;
            }
            Vec3d to = new Vec3d(e.posX - p.posX, 0.0D, e.posZ - p.posZ);
            double dist = to.length();
            if (dist > SLAM_RANGE || (dist > 0.5D && to.normalize().dotProduct(look) < SLAM_HALF_ANGLE_COS)) {
                continue;
            }
            EntityLivingBase target = (EntityLivingBase) e;
            target.attackEntityFrom(DamageSource.causePlayerDamage(p), (float) LdflConfig.bulwark.tectonicSlamDamage);
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, SLAM_STAGGER_TICKS, 3));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, SLAM_STAGGER_TICKS, 1));
            target.motionY += 0.45D;
            target.velocityChanged = true;
        }
        // A visible crack running out along the cone's centre line.
        for (int i = 1; i <= (int) SLAM_RANGE; i++) {
            Vec3d at = new Vec3d(p.posX, p.posY + 0.1D, p.posZ).add(look.scale(i));
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, at.x, at.y, at.z, 4,
                0.4D * i / SLAM_RANGE + 0.2D, 0.1D, 0.4D * i / SLAM_RANGE + 0.2D, 0.02D);
        }
        world.playSound(null, p.posX, p.posY, p.posZ, LdflSounds.TECTONIC_SLAM,
            SoundCategory.PLAYERS, 1.0F, 0.6F);
        return true;
    }
}
