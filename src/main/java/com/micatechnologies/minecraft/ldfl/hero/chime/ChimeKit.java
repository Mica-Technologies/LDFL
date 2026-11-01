package com.micatechnologies.minecraft.ldfl.hero.chime;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.entity.EntityWardBell;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;

/**
 * Chime, the buff Support: the server-side behaviour of its three abilities. The Tuning Rod
 * (its weapon) lives in {@code ItemTuningRod}.
 *
 * <ul>
 *   <li><b>Resonance</b>: Strength for six seconds to you and allies within eight blocks.</li>
 *   <li><b>Ward Bell</b>: place a bell at your feet that keeps giving nearby allies absorption
 *       hearts for a while. You can have one bell; ringing a new one silences the old.</li>
 *   <li><b>Crescendo</b> (ultimate): allies within ten blocks take almost no damage for a few
 *       seconds, and regenerate.</li>
 * </ul>
 *
 * <p>Support given counts toward the ultimate the same way healing does: a point per ally buffed.</p>
 */
public final class ChimeKit {

    private static final double RESONANCE_RADIUS = 8.0D;
    private static final double CRESCENDO_RADIUS = 10.0D;

    private ChimeKit() {
        throw new AssertionError("No instances.");
    }

    public static boolean resonance(EntityPlayerMP p, HeroData data) {
        int buffed = 0;
        for (EntityLivingBase e : p.world.getEntitiesWithinAABB(EntityLivingBase.class,
            p.getEntityBoundingBox().grow(RESONANCE_RADIUS))) {
            if (Targeting.isAlly(p, e) && e.getDistanceSq(p) <= RESONANCE_RADIUS * RESONANCE_RADIUS) {
                e.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 6 * 20, 0));
                buffed++;
            }
        }
        HeroEvents.creditHealing(p, buffed * 2.0F);
        p.getServerWorld().spawnParticle(EnumParticleTypes.NOTE, p.posX, p.posY + 1.5D, p.posZ, 12,
            2.0D, 0.5D, 2.0D, 1.0D);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.BLOCK_NOTE_CHIME,
            SoundCategory.PLAYERS, 1.0F, 1.2F);
        return true;
    }

    public static boolean wardBell(EntityPlayerMP p, HeroData data) {
        if (data.wardBell != null && !data.wardBell.isDead) {
            data.wardBell.setDead();
        }
        data.wardBell = new EntityWardBell(p.world, p);
        p.world.spawnEntity(data.wardBell);
        return true;
    }

    public static boolean crescendo(EntityPlayerMP p, HeroData data) {
        int ticks = LdflConfig.ticks(LdflConfig.chime.crescendoSeconds);
        WorldServer world = p.getServerWorld();
        for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class,
            p.getEntityBoundingBox().grow(CRESCENDO_RADIUS))) {
            if (Targeting.isAlly(p, e) && e.getDistanceSq(p) <= CRESCENDO_RADIUS * CRESCENDO_RADIUS) {
                e.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, ticks, 3));
                e.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, ticks, 1));
                world.spawnParticle(EnumParticleTypes.NOTE, e.posX, e.posY + e.height + 0.4D, e.posZ, 5,
                    0.4D, 0.2D, 0.4D, 1.0D);
            }
        }
        world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.BLOCK_NOTE_BELL,
            SoundCategory.PLAYERS, 1.5F, 0.5F);
        world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.BLOCK_NOTE_BELL,
            SoundCategory.PLAYERS, 1.5F, 1.0F);
        return true;
    }

    public static void tick(EntityPlayerMP p, HeroData data) {
        if (data.wardBell != null && data.wardBell.isDead) {
            data.wardBell = null;
        }
        if (data.wardBell != null) {
            int total = LdflConfig.ticks(LdflConfig.chime.wardBellSeconds);
            data.resource = total > 0 ? 1.0F - data.wardBell.ticksExisted / (float) total : -1.0F;
        }
    }
}
