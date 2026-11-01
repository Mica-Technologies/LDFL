package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.LdflSounds;
import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Solace's Bloom Flask: an arcing thrown flask that bursts on impact. Allies in the burst
 * (including the thrower) are healed; enemies take damage and are left Wounded.
 */
public class EntityBloomFlask extends EntityThrowable {

    private static final double RADIUS = 4.0D;
    private static final int WOUND_TICKS = 4 * 20;

    public EntityBloomFlask(World world) {
        super(world);
    }

    public EntityBloomFlask(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote) {
            return;
        }
        EntityLivingBase thrower = getThrower();
        if (thrower != null) {
            for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class,
                getEntityBoundingBox().grow(RADIUS))) {
                if (e.getDistanceSq(this) > RADIUS * RADIUS) {
                    continue;
                }
                if (Targeting.isAlly(thrower, e)) {
                    float healed = Math.min((float) LdflConfig.solace.bloomFlaskHeal, e.getMaxHealth() - e.getHealth());
                    e.heal((float) LdflConfig.solace.bloomFlaskHeal);
                    if (thrower instanceof EntityPlayer && healed > 0.0F) {
                        HeroEvents.creditHealing((EntityPlayer) thrower, healed);
                    }
                } else if (Targeting.isEnemy(thrower, e)) {
                    e.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), (float) LdflConfig.solace.bloomFlaskDamage);
                    e.addPotionEffect(new PotionEffect(HeroEvents.WOUNDED, WOUND_TICKS, 0, false, true));
                }
            }
        }
        WorldServer ws = (WorldServer) world;
        ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, posX, posY + 0.5D, posZ, 30,
            RADIUS * 0.4D, 0.5D, RADIUS * 0.4D, 0.0D);
        ws.spawnParticle(EnumParticleTypes.SPELL_INSTANT, posX, posY + 0.3D, posZ, 20,
            RADIUS * 0.3D, 0.3D, RADIUS * 0.3D, 0.0D);
        world.playSound(null, posX, posY, posZ, LdflSounds.FLASK_BURST,
            SoundCategory.PLAYERS, 1.0F, 1.2F);
        setDead();
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }
}
