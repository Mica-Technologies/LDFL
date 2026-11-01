package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import com.micatechnologies.minecraft.ldfl.hero.core.Aim;
import com.micatechnologies.minecraft.ldfl.item.ItemTinkersWrench;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Forgewright's sentry: a stationary auto-turret that shoots bolts at enemies of its owner.
 *
 * <ul>
 *   <li>It fires only while its owner is online and is Forgewright. Otherwise it powers down, and
 *       after 30 seconds idle it packs itself away, so abandoned sentries do not litter a server.</li>
 *   <li>Its owner and their allies cannot damage it. Hitting it with the Tinker's Wrench repairs
 *       it instead.</li>
 *   <li>During its owner's Slag Surge ultimate it fires twice as fast and its bolts ignite.</li>
 * </ul>
 */
public class EntitySentry extends EntityLiving {

    private static final float REPAIR_PER_HIT = 6.0F;
    private static final int IDLE_PACK_UP_TICKS = 30 * 20;
    private static final float TURN_RATE = 25.0F;

    private static double range() {
        return LdflConfig.forgewright.sentryRange;
    }

    private static final DataParameter<Boolean> ACTIVE =
        EntityDataManager.createKey(EntitySentry.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SURGING =
        EntityDataManager.createKey(EntitySentry.class, DataSerializers.BOOLEAN);

    private UUID ownerId;
    private EntityLivingBase target;
    private int fireCooldown;
    private int idleTicks;

    public EntitySentry(World world) {
        super(world);
        setSize(0.8F, 1.1F);
        experienceValue = 0;
    }

    public EntitySentry(World world, EntityPlayer owner) {
        this(world);
        ownerId = owner.getUniqueID();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ACTIVE, false);
        dataManager.register(SURGING, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LdflConfig.forgewright.sentryHealth);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public boolean isActive() {
        return dataManager.get(ACTIVE);
    }

    public boolean isSurging() {
        return dataManager.get(SURGING);
    }

    /** The owner if they are online in this world. */
    public EntityPlayer getOwner() {
        return ownerId == null ? null : world.getPlayerEntityByUUID(ownerId);
    }

    @Override
    public float getEyeHeight() {
        return 0.8F;
    }

    // --- Behaviour ---------------------------------------------------------------------------

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote) {
            return;
        }
        EntityPlayer owner = getOwner();
        HeroData data = owner == null ? null : HeroCapability.get(owner);
        boolean active = data != null && data.state().hero() != null
            && Heroes.FORGEWRIGHT_ID.equals(data.state().hero().id());
        dataManager.set(ACTIVE, active);
        if (!active) {
            target = null;
            if (++idleTicks > IDLE_PACK_UP_TICKS) {
                packUp();
            }
            return;
        }
        idleTicks = 0;
        data.sentry = this;
        boolean surging = data.surgeTicks > 0;
        dataManager.set(SURGING, surging);

        if (fireCooldown > 0) {
            fireCooldown--;
        }
        if (!isValidTarget(owner, target)) {
            target = findTarget(owner);
        }
        if (target == null) {
            return;
        }
        double dx = target.posX - posX;
        double dy = target.posY + target.getEyeHeight() * 0.7D - (posY + getEyeHeight());
        double dz = target.posZ - posZ;
        rotationYawHead = Aim.approach(rotationYawHead, Aim.yaw(dx, dz), TURN_RATE);
        rotationYaw = rotationYawHead;
        renderYawOffset = rotationYawHead;
        rotationPitch = Aim.approach(rotationPitch, Aim.pitch(dx, dy, dz), TURN_RATE);

        boolean onTarget = Math.abs(Aim.wrap(Aim.yaw(dx, dz) - rotationYawHead)) < 10.0F;
        if (fireCooldown == 0 && onTarget) {
            EntityBolt bolt = new EntityBolt(world, this, owner, (float) LdflConfig.forgewright.sentryBoltDamage, surging);
            bolt.shoot(dx, dy, dz, 2.2F, 1.0F);
            world.spawnEntity(bolt);
            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_SHULKER_SHOOT,
                SoundCategory.HOSTILE, 0.5F, 1.6F);
            fireCooldown = surging ? Math.max(1, LdflConfig.forgewright.sentryFireIntervalTicks / 2) : LdflConfig.forgewright.sentryFireIntervalTicks;
        }
    }

    private boolean isValidTarget(EntityPlayer owner, EntityLivingBase t) {
        return t != null && !t.isDead && Targeting.isEnemy(owner, t)
            && getDistanceSq(t) <= range() * range() && canEntityBeSeen(t);
    }

    private EntityLivingBase findTarget(EntityPlayer owner) {
        EntityLivingBase best = null;
        double bestDist = Double.MAX_VALUE;
        for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class,
            getEntityBoundingBox().grow(range()))) {
            double d = getDistanceSq(e);
            if (d < bestDist && isValidTarget(owner, e)) {
                best = e;
                bestDist = d;
            }
        }
        return best;
    }

    /** Remove the sentry with a puff, without drops. */
    public void packUp() {
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY + 0.5D,
                posZ, 8, 0.3D, 0.3D, 0.3D, 0.02D);
        }
        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_PISTON_CONTRACT,
            SoundCategory.NEUTRAL, 0.6F, 1.2F);
        setDead();
    }

    // --- Damage and repair -------------------------------------------------------------------

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity attacker = source.getTrueSource();
        EntityPlayer owner = getOwner();
        if (attacker instanceof EntityPlayer && !world.isRemote) {
            EntityPlayer p = (EntityPlayer) attacker;
            boolean friendly = p == owner || (owner != null && Targeting.isAlly(owner, p));
            if (friendly) {
                if (p.getHeldItemMainhand().getItem() instanceof ItemTinkersWrench
                    && getHealth() < getMaxHealth()) {
                    heal(REPAIR_PER_HIT);
                    world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_ANVIL_USE,
                        SoundCategory.PLAYERS, 0.4F, 1.8F);
                }
                return false;
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    /** Fully repair (Forgewright's Plating ability). */
    public void repairFully() {
        setHealth(getMaxHealth());
    }

    // --- Entity plumbing ---------------------------------------------------------------------

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
    }

    @Override
    protected boolean canDropLoot() {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (ownerId != null) {
            compound.setUniqueId("Owner", ownerId);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("Owner")) {
            ownerId = compound.getUniqueId("Owner");
        }
    }
}
