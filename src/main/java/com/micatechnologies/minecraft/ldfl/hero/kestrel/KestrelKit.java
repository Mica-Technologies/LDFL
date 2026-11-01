package com.micatechnologies.minecraft.ldfl.hero.kestrel;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.HitScan;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;

/**
 * Kestrel, the sniper Damage hero: the Longshot and the server-side behaviour of its abilities.
 *
 * <ul>
 *   <li><b>Longshot</b> (hold right-click to charge, release to fire): an instant shot whose damage
 *       grows with charge. It hits the first enemy on the line, or every enemy on it during Long
 *       Vigil.</li>
 *   <li><b>Updraft</b>: launches you straight up. Your fall is slowed (client side) and you take
 *       no fall damage until you land.</li>
 *   <li><b>Marker Dart</b>: the first enemy in your sights glows and takes extra damage from
 *       everyone for a few seconds.</li>
 *   <li><b>Long Vigil</b> (ultimate): every enemy in a wide radius glows, your Longshot pierces
 *       and charges twice as fast.</li>
 * </ul>
 */
public final class KestrelKit {

    public static final double LONGSHOT_RANGE = 64.0D;
    private static final double DART_RANGE = 48.0D;
    private static final double VIGIL_RADIUS = 48.0D;
    private static final double UPDRAFT_VELOCITY = 1.15D;

    private KestrelKit() {
        throw new AssertionError("No instances.");
    }

    /** Ticks to full charge right now: halved during Long Vigil. */
    public static int fullChargeTicks(HeroData data) {
        int full = Math.max(1, LdflConfig.ticks(LdflConfig.kestrel.longshotChargeSeconds));
        return data != null && data.vigilTicks > 0 ? Math.max(1, full / 2) : full;
    }

    /** Fire a Longshot charged for {@code chargeTicks}. Server side. */
    public static void fireLongshot(EntityPlayer p, int chargeTicks) {
        HeroData data = HeroCapability.get(p);
        float power = Math.min(1.0F, chargeTicks / (float) fullChargeTicks(data));
        double min = LdflConfig.kestrel.longshotMinDamage;
        double max = LdflConfig.kestrel.longshotMaxDamage;
        float damage = (float) (min + (max - min) * power * power);
        boolean pierce = data != null && data.vigilTicks > 0;

        HitScan.Result shot = HitScan.enemiesAlongAim(p, LONGSHOT_RANGE);
        for (EntityLivingBase target : shot.enemies) {
            target.hurtResistantTime = 0;
            target.attackEntityFrom(DamageSource.causePlayerDamage(p), damage);
            if (!pierce) {
                break;
            }
        }
        HitScan.tracer(p, shot.end, power >= 1.0F ? EnumParticleTypes.END_ROD : EnumParticleTypes.CRIT);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_FIREWORK_BLAST,
            SoundCategory.PLAYERS, 0.6F + power * 0.6F, 1.6F - power * 0.6F);
    }

    public static boolean updraft(EntityPlayerMP p, HeroData data) {
        p.motionY = UPDRAFT_VELOCITY;
        p.velocityChanged = true;
        p.fallDistance = 0.0F;
        data.noFallTicks = 10;
        p.getServerWorld().spawnParticle(EnumParticleTypes.CLOUD, p.posX, p.posY, p.posZ, 16,
            0.4D, 0.1D, 0.4D, 0.05D);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP,
            SoundCategory.PLAYERS, 0.8F, 1.4F);
        return true;
    }

    public static boolean markerDart(EntityPlayerMP p, HeroData data) {
        HitScan.Result shot = HitScan.enemiesAlongAim(p, DART_RANGE);
        HitScan.tracer(p, shot.end, EnumParticleTypes.SPELL_INSTANT);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
            SoundCategory.PLAYERS, 0.7F, 1.8F);
        if (!shot.enemies.isEmpty()) {
            EntityLivingBase target = shot.enemies.get(0);
            int ticks = LdflConfig.ticks(LdflConfig.kestrel.markedSeconds);
            target.addPotionEffect(new PotionEffect(MobEffects.GLOWING, ticks, 0, false, false));
            target.addPotionEffect(new PotionEffect(HeroEvents.MARKED, ticks, 0, false, true));
        }
        return true;
    }

    public static boolean longVigil(EntityPlayerMP p, HeroData data) {
        int ticks = LdflConfig.ticks(LdflConfig.kestrel.vigilSeconds);
        data.vigilTicks = ticks;
        for (EntityLivingBase e : p.world.getEntitiesWithinAABB(EntityLivingBase.class,
            p.getEntityBoundingBox().grow(VIGIL_RADIUS))) {
            if (Targeting.isEnemy(p, e)) {
                e.addPotionEffect(new PotionEffect(MobEffects.GLOWING, ticks, 0, false, false));
            }
        }
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE,
            SoundCategory.PLAYERS, 0.6F, 1.6F);
        return true;
    }

    public static void tick(EntityPlayerMP p, HeroData data) {
        if (data.vigilTicks > 0) {
            data.vigilTicks--;
            int total = LdflConfig.ticks(LdflConfig.kestrel.vigilSeconds);
            data.resource = total > 0 ? data.vigilTicks / (float) total : -1.0F;
        }
    }

    public static void cleanup(EntityPlayerMP p, HeroData data) {
        data.vigilTicks = 0;
    }
}
