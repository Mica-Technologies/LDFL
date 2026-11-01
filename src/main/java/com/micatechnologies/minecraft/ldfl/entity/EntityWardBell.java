package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.HeroEvents;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Chime's Ward Bell: a small floating bell placed at Chime's feet. Every two seconds it rings,
 * and each ally within six blocks gets a short burst of absorption hearts. It fades after
 * {@code chime.wardBellSeconds}. Each ally shielded counts toward Chime's ultimate.
 */
public class EntityWardBell extends Entity {

    private static final double RADIUS = 6.0D;
    private static final int PULSE_TICKS = 40;

    private UUID ownerId;

    public EntityWardBell(World world) {
        super(world);
        setSize(0.6F, 0.9F);
        noClip = true;
    }

    public EntityWardBell(World world, EntityPlayer owner) {
        this(world);
        ownerId = owner.getUniqueID();
        setPosition(owner.posX, owner.posY, owner.posZ);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }
        EntityPlayer owner = ownerId == null ? null : world.getPlayerEntityByUUID(ownerId);
        if (owner == null || ticksExisted > LdflConfig.ticks(LdflConfig.chime.wardBellSeconds)) {
            fade();
            return;
        }
        if (ticksExisted % PULSE_TICKS == 1) {
            ring(owner);
        }
    }

    private void ring(EntityPlayer owner) {
        for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class,
            getEntityBoundingBox().grow(RADIUS))) {
            if (Targeting.isAlly(owner, e) && e.getDistanceSq(this) <= RADIUS * RADIUS) {
                e.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, PULSE_TICKS + 20, 0, true, true));
                HeroEvents.creditHealing(owner, 1.0F);
            }
        }
        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_BELL, SoundCategory.PLAYERS,
            0.8F, 1.0F + rand.nextFloat() * 0.2F);
        ((WorldServer) world).spawnParticle(EnumParticleTypes.NOTE, posX, posY + 1.0D, posZ, 6,
            RADIUS * 0.3D, 0.2D, RADIUS * 0.3D, 1.0D);
    }

    private void fade() {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, posX, posY + 0.5D, posZ, 6,
                0.2D, 0.2D, 0.2D, 0.02D);
        }
        setDead();
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}
