package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * Solace's healing beam: a carrier entity whose only job is to tell clients who is beaming whom,
 * so they can draw the link. It sits on its owner. The healing itself happens in
 * {@code SolaceKit}, and the owner's {@code HeroData.beam} is the source of truth; a beam that
 * is not referenced there removes itself, the same way {@link EntityBarrier} does.
 */
public class EntityHealBeam extends Entity {

    private static final DataParameter<Integer> OWNER_ID =
        EntityDataManager.createKey(EntityHealBeam.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ID =
        EntityDataManager.createKey(EntityHealBeam.class, DataSerializers.VARINT);

    public EntityHealBeam(World world) {
        super(world);
        setSize(0.1F, 0.1F);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    public EntityHealBeam(World world, EntityPlayer owner, Entity target) {
        this(world);
        dataManager.set(OWNER_ID, owner.getEntityId());
        dataManager.set(TARGET_ID, target.getEntityId());
        setPosition(owner.posX, owner.posY, owner.posZ);
    }

    @Override
    protected void entityInit() {
        dataManager.register(OWNER_ID, -1);
        dataManager.register(TARGET_ID, -1);
    }

    public Entity getOwner() {
        return world.getEntityByID(dataManager.get(OWNER_ID));
    }

    public Entity getTarget() {
        return world.getEntityByID(dataManager.get(TARGET_ID));
    }

    public void setTarget(Entity target) {
        dataManager.set(TARGET_ID, target.getEntityId());
    }

    @Override
    public void onUpdate() {
        Entity owner = getOwner();
        if (!world.isRemote) {
            HeroData data = owner instanceof EntityPlayer ? HeroCapability.get((EntityPlayer) owner) : null;
            if (data == null || !owner.isEntityAlive() || data.beam != this) {
                setDead();
                return;
            }
        }
        if (owner != null) {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            setPosition(owner.posX, owner.posY, owner.posZ);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 96.0D * 96.0D;
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
