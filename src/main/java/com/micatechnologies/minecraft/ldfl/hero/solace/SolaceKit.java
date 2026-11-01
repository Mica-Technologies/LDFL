package com.micatechnologies.minecraft.ldfl.hero.solace;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.entity.EntityBloomFlask;
import com.micatechnologies.minecraft.ldfl.entity.EntityHealBeam;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import com.micatechnologies.minecraft.ldfl.item.ItemSolaceStaff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

/**
 * Solace, the Support: the heal beam and the server-side behaviour of its three abilities.
 *
 * <ul>
 *   <li><b>Heal beam</b> (hold right-click with the Solace Staff): heals the ally in your sights.
 *       Once locked, the beam holds its target while they stay in range, in line of sight and
 *       roughly in your aim. It cannot target yourself; Solace's own sustain is the Support
 *       passive and the Bloom Flask.</li>
 *   <li><b>Bloom Flask</b>: throw a flask that heals allies (you included) and wounds enemies.</li>
 *   <li><b>Tailwind</b>: Speed II for four seconds to you and allies within eight blocks.</li>
 *   <li><b>Sanctuary</b> (ultimate): allies within ten blocks are healed for 10, and get
 *       Regeneration II and Resistance for six seconds.</li>
 * </ul>
 *
 * <p>Only healing that actually lands (not overheal) charges the ultimate.</p>
 */
public final class SolaceKit {

    private static final double BEAM_ACQUIRE_RANGE = 12.0D;
    private static final double BEAM_HOLD_RANGE = 15.0D;
    private static final double BEAM_HOLD_COS = Math.cos(Math.toRadians(35.0D));

    private static final double TAILWIND_RADIUS = 8.0D;
    private static final double SANCTUARY_RADIUS = 10.0D;

    private SolaceKit() {
        throw new AssertionError("No instances.");
    }

    // --- Heal beam ---------------------------------------------------------------------------

    /** Called each tick while the player is Solace. */
    public static void tick(EntityPlayerMP p, HeroData data) {
        boolean beaming = p.isHandActive() && p.getActiveItemStack().getItem() instanceof ItemSolaceStaff;
        Entity target = beaming ? (data.beam != null ? data.beam.getTarget() : null) : null;
        if (beaming && !holds(p, target)) {
            target = acquire(p);
        }
        if (target == null) {
            stopBeam(data);
            return;
        }
        if (data.beam == null || data.beam.isDead) {
            data.beam = new EntityHealBeam(p.world, p, target);
            p.world.spawnEntity(data.beam);
        } else if (data.beam.getTarget() != target) {
            data.beam.setTarget(target);
        }
        EntityLivingBase ally = (EntityLivingBase) target;
        float healed = Math.min((float) (LdflConfig.solace.beamHealPerSecond / 20.0D), ally.getMaxHealth() - ally.getHealth());
        ally.heal((float) (LdflConfig.solace.beamHealPerSecond / 20.0D));
        if (healed > 0.0F) {
            HeroEvents.creditHealing(p, healed);
        }
        if (p.ticksExisted % 8 == 0) {
            p.getServerWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, ally.posX,
                ally.posY + ally.height * 0.6D, ally.posZ, 2, 0.3D, 0.3D, 0.3D, 0.0D);
        }
    }

    /** Remove the beam if it is up. Safe to call for any player. */
    public static void stopBeam(HeroData data) {
        if (data.beam != null) {
            data.beam.setDead();
            data.beam = null;
        }
    }

    private static boolean holds(EntityPlayerMP p, Entity t) {
        if (!(t instanceof EntityLivingBase) || t == p || !Targeting.isAlly(p, t)
            || p.getDistanceSq(t) > BEAM_HOLD_RANGE * BEAM_HOLD_RANGE || !p.canEntityBeSeen(t)) {
            return false;
        }
        Vec3d to = new Vec3d(t.posX - p.posX, t.posY + t.height * 0.5D - (p.posY + p.getEyeHeight()),
            t.posZ - p.posZ).normalize();
        return to.dotProduct(p.getLookVec()) >= BEAM_HOLD_COS;
    }

    /** The ally nearest the crosshair ray within range, or {@code null}. */
    private static Entity acquire(EntityPlayerMP p) {
        Vec3d eyes = p.getPositionEyes(1.0F);
        Vec3d look = p.getLookVec();
        Vec3d end = eyes.add(look.scale(BEAM_ACQUIRE_RANGE));
        RayTraceResult wall = p.world.rayTraceBlocks(eyes, end, false, true, false);
        double maxDist = wall == null ? BEAM_ACQUIRE_RANGE : wall.hitVec.distanceTo(eyes);
        AxisAlignedBB sweep = p.getEntityBoundingBox().expand(look.x * maxDist, look.y * maxDist,
            look.z * maxDist).grow(1.0D);
        Entity best = null;
        double bestDist = maxDist;
        for (Entity e : p.world.getEntitiesWithinAABBExcludingEntity(p, sweep)) {
            if (!(e instanceof EntityLivingBase) || !Targeting.isAlly(p, e)) {
                continue;
            }
            // A generous hitbox, so the beam is easy to lock on to.
            RayTraceResult hit = e.getEntityBoundingBox().grow(0.5D).calculateIntercept(eyes, end);
            if (hit != null) {
                double d = eyes.distanceTo(hit.hitVec);
                if (d < bestDist) {
                    best = e;
                    bestDist = d;
                }
            }
        }
        return best;
    }

    // --- Abilities ---------------------------------------------------------------------------

    public static boolean bloomFlask(EntityPlayerMP p, HeroData data) {
        EntityBloomFlask flask = new EntityBloomFlask(p.world, p);
        flask.shoot(p, p.rotationPitch, p.rotationYaw, -10.0F, 0.9F, 0.5F);
        p.world.spawnEntity(flask);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW,
            SoundCategory.PLAYERS, 0.8F, 1.0F);
        return true;
    }

    public static boolean tailwind(EntityPlayerMP p, HeroData data) {
        for (EntityLivingBase e : p.world.getEntitiesWithinAABB(EntityLivingBase.class,
            p.getEntityBoundingBox().grow(TAILWIND_RADIUS))) {
            if (Targeting.isAlly(p, e) && e.getDistanceSq(p) <= TAILWIND_RADIUS * TAILWIND_RADIUS) {
                e.addPotionEffect(new PotionEffect(MobEffects.SPEED, 4 * 20, 1));
            }
        }
        p.getServerWorld().spawnParticle(EnumParticleTypes.CLOUD, p.posX, p.posY + 0.2D, p.posZ, 20,
            2.0D, 0.1D, 2.0D, 0.05D);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP,
            SoundCategory.PLAYERS, 0.6F, 1.6F);
        return true;
    }

    public static boolean sanctuary(EntityPlayerMP p, HeroData data) {
        WorldServer world = p.getServerWorld();
        for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class,
            p.getEntityBoundingBox().grow(SANCTUARY_RADIUS))) {
            if (!Targeting.isAlly(p, e) || e.getDistanceSq(p) > SANCTUARY_RADIUS * SANCTUARY_RADIUS) {
                continue;
            }
            e.heal((float) LdflConfig.solace.sanctuaryHeal);
            e.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 6 * 20, 1));
            e.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6 * 20, 0));
            world.spawnParticle(EnumParticleTypes.HEART, e.posX, e.posY + e.height + 0.3D, e.posZ, 4,
                0.4D, 0.2D, 0.4D, 0.0D);
        }
        for (int i = 0; i < 32; i++) {
            double a = i * Math.PI * 2.0D / 32.0D;
            world.spawnParticle(EnumParticleTypes.END_ROD, p.posX + Math.cos(a) * 3.0D, p.posY + 0.2D,
                p.posZ + Math.sin(a) * 3.0D, 1, 0.0D, 0.2D, 0.0D, 0.02D);
        }
        world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP,
            SoundCategory.PLAYERS, 1.0F, 0.8F);
        return true;
    }
}
