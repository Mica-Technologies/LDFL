package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Bulwark's projected barrier: a flat, upright panel held in front of its owner while they raise
 * the Bulwark Shield.
 *
 * <p>The panel cannot be an axis-aligned bounding box, because it turns with its owner, so it has
 * no collision of its own. Blocking is geometric instead: {@code BarrierLogic} tests projectile
 * paths and melee lines against the panel's plane every tick. This entity only carries the pose and
 * the health fraction (for the renderer). The real barrier health lives in the owner's
 * {@code HeroData}, so it survives the panel being lowered and raised.</p>
 *
 * <p>Never saved: the panel is recreated from its owner's state, and one loaded from disk has no
 * owner and removes itself on its first tick.</p>
 */
public class EntityBarrier extends Entity {

    /** Panel half-width and height, in blocks. */
    public static final double HALF_WIDTH = 2.5D;
    public static final double HEIGHT = 3.5D;
    /** Distance of the panel in front of the owner's feet, and how far below their feet it starts. */
    public static final double FORWARD = 1.6D;
    public static final double DROP = 0.4D;

    private static final DataParameter<Integer> OWNER_ID =
        EntityDataManager.createKey(EntityBarrier.class, DataSerializers.VARINT);
    private static final DataParameter<Float> HEALTH_FRACTION =
        EntityDataManager.createKey(EntityBarrier.class, DataSerializers.FLOAT);

    public EntityBarrier(World world) {
        super(world);
        setSize((float) (HALF_WIDTH * 2.0D), (float) HEIGHT);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    public EntityBarrier(World world, EntityPlayer owner) {
        this(world);
        dataManager.set(OWNER_ID, owner.getEntityId());
        followOwner(owner);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevRotationYaw = rotationYaw;
    }

    @Override
    protected void entityInit() {
        dataManager.register(OWNER_ID, -1);
        dataManager.register(HEALTH_FRACTION, 1.0F);
    }

    /** The owning player, or {@code null} if they are gone from this world. */
    public EntityPlayer getOwner() {
        Entity e = world.getEntityByID(dataManager.get(OWNER_ID));
        return e instanceof EntityPlayer ? (EntityPlayer) e : null;
    }

    public float getHealthFraction() {
        return dataManager.get(HEALTH_FRACTION);
    }

    public void setHealthFraction(float f) {
        dataManager.set(HEALTH_FRACTION, MathHelper.clamp(f, 0.0F, 1.0F));
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevRotationYaw = rotationYaw;
        EntityPlayer owner = getOwner();
        if (!world.isRemote) {
            // The owner's hero data is the source of truth. A panel it no longer points at (after a
            // respawn, which keeps the player's entity id, or a hero swap) is an orphan.
            HeroData data = owner == null ? null : HeroCapability.get(owner);
            if (owner == null || !owner.isEntityAlive() || data == null || data.barrier != this) {
                setDead();
                return;
            }
        } else if (owner == null) {
            return;
        }
        followOwner(owner);
    }

    /** Stand the panel in front of {@code owner}, facing where they face (yaw only). */
    private void followOwner(EntityPlayer owner) {
        Vec3d n = normalFor(owner.rotationYaw);
        setPosition(owner.posX + n.x * FORWARD, owner.posY - DROP, owner.posZ + n.z * FORWARD);
        rotationYaw = owner.rotationYaw;
    }

    // --- Geometry ----------------------------------------------------------------------------

    /** Horizontal unit normal of a panel facing {@code yaw}: points away from the owner. */
    public static Vec3d normalFor(float yaw) {
        float r = yaw * 0.017453292F;
        return new Vec3d(-MathHelper.sin(r), 0.0D, MathHelper.cos(r));
    }

    public Vec3d normal() {
        return normalFor(rotationYaw);
    }

    /** Signed distance of {@code p} from the panel's plane; positive is the enemy-facing side. */
    public double side(Vec3d p) {
        Vec3d n = normal();
        return (p.x - posX) * n.x + (p.z - posZ) * n.z;
    }

    /** Whether {@code p}, assumed to lie on (or near) the plane, falls inside the panel. */
    public boolean withinPanel(Vec3d p) {
        Vec3d n = normal();
        double lateral = (p.x - posX) * -n.z + (p.z - posZ) * n.x;
        return Math.abs(lateral) <= HALF_WIDTH && p.y >= posY && p.y <= posY + HEIGHT;
    }

    /**
     * Where the segment {@code from -> to} passes through the panel from the front, or
     * {@code null} if it does not.
     */
    public Vec3d intersectFromFront(Vec3d from, Vec3d to) {
        double d0 = side(from);
        double d1 = side(to);
        if (d0 < 0.0D || d1 > 0.0D || d0 == d1) {
            return null;
        }
        double t = d0 / (d0 - d1);
        Vec3d hit = from.add(to.subtract(from).scale(t));
        return withinPanel(hit) ? hit : null;
    }

    // --- Entity plumbing ---------------------------------------------------------------------

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
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
