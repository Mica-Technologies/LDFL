package com.micatechnologies.minecraft.ldfl.hero.forgewright;

import com.micatechnologies.minecraft.ldfl.entity.EntitySentry;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

/**
 * Forgewright, the Damage engineer: the server-side behaviour of its three abilities.
 *
 * <ul>
 *   <li><b>Deploy Sentry</b>: place a sentry on the block face you are looking at, within reach.
 *       You can have one sentry; deploying again packs the old one away. Fails (and costs
 *       nothing) if there is no room.</li>
 *   <li><b>Plating</b>: five seconds of damage resistance, and your sentry is fully repaired.</li>
 *   <li><b>Slag Surge</b> (ultimate): for eight seconds your sentry fires twice as fast, and every
 *       bolt from you or your sentry sets its target alight.</li>
 * </ul>
 */
public final class ForgewrightKit {

    private static final double DEPLOY_REACH = 6.0D;
    private static final int PLATING_TICKS = 5 * 20;
    public static final int SURGE_TICKS = 8 * 20;

    private ForgewrightKit() {
        throw new AssertionError("No instances.");
    }

    public static boolean deploySentry(EntityPlayerMP p, HeroData data) {
        WorldServer world = p.getServerWorld();
        Vec3d eyes = p.getPositionEyes(1.0F);
        Vec3d end = eyes.add(p.getLookVec().scale(DEPLOY_REACH));
        RayTraceResult ray = world.rayTraceBlocks(eyes, end, false, true, false);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK || ray.sideHit != EnumFacing.UP) {
            return false;
        }
        BlockPos on = ray.getBlockPos().up();
        EntitySentry sentry = new EntitySentry(world, p);
        sentry.setLocationAndAngles(on.getX() + 0.5D, on.getY(), on.getZ() + 0.5D, p.rotationYaw, 0.0F);
        sentry.rotationYawHead = p.rotationYaw;
        sentry.renderYawOffset = p.rotationYaw;
        AxisAlignedBB box = sentry.getEntityBoundingBox();
        if (!world.getCollisionBoxes(sentry, box).isEmpty() || world.containsAnyLiquid(box)) {
            return false;
        }

        removeExisting(p, data);
        world.spawnEntity(sentry);
        data.sentry = sentry;
        world.playSound(null, sentry.posX, sentry.posY, sentry.posZ, SoundEvents.BLOCK_ANVIL_PLACE,
            SoundCategory.PLAYERS, 0.5F, 1.4F);
        world.spawnParticle(EnumParticleTypes.CLOUD, sentry.posX, sentry.posY + 0.3D, sentry.posZ, 6,
            0.3D, 0.1D, 0.3D, 0.02D);
        return true;
    }

    /** Pack away this player's current sentry, including one left over from an earlier session. */
    private static void removeExisting(EntityPlayerMP p, HeroData data) {
        if (data.sentry != null && !data.sentry.isDead) {
            data.sentry.packUp();
        }
        data.sentry = null;
        for (Entity e : p.getServerWorld().loadedEntityList) {
            if (e instanceof EntitySentry && !e.isDead
                && p.getUniqueID().equals(((EntitySentry) e).getOwnerId())) {
                ((EntitySentry) e).packUp();
            }
        }
    }

    public static boolean plating(EntityPlayerMP p, HeroData data) {
        p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, PLATING_TICKS, 1));
        if (data.sentry != null && !data.sentry.isDead) {
            data.sentry.repairFully();
        }
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            SoundCategory.PLAYERS, 1.0F, 0.8F);
        return true;
    }

    public static boolean slagSurge(EntityPlayerMP p, HeroData data) {
        data.surgeTicks = SURGE_TICKS;
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_BLAZE_AMBIENT,
            SoundCategory.PLAYERS, 1.0F, 0.7F);
        p.getServerWorld().spawnParticle(EnumParticleTypes.LAVA, p.posX, p.posY + 1.0D, p.posZ, 12,
            0.4D, 0.4D, 0.4D, 0.0D);
        return true;
    }

    /** Called each tick while the player is Forgewright. */
    public static void tick(EntityPlayerMP p, HeroData data) {
        if (data.surgeTicks > 0) {
            data.surgeTicks--;
            if (p.ticksExisted % 4 == 0) {
                p.getServerWorld().spawnParticle(EnumParticleTypes.FLAME, p.posX, p.posY + 1.0D,
                    p.posZ, 2, 0.3D, 0.5D, 0.3D, 0.01D);
            }
        }
        if (data.sentry != null && data.sentry.isDead) {
            data.sentry = null;
        }
        data.resource = data.sentry == null ? -1.0F : data.sentry.getHealth() / data.sentry.getMaxHealth();
    }
}
