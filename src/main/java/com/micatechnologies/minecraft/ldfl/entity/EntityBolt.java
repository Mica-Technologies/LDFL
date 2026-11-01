package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * A fast, gravity-free bolt fired by Forgewright's Bolt Driver and sentry.
 *
 * <p>Damage is credited to the {@linkplain #getCreditedShooter() credited shooter}: the player
 * behind it, even when a sentry fired it. That makes sentry kills charge the owner's ultimate and
 * apply the Damage passive. Bolts only hurt enemies of that shooter. Bolts are rapid-fire, so a
 * hit clears the target's damage-immunity window first, the way vanilla does for each arrow.</p>
 */
public class EntityBolt extends EntityThrowable {

    private static final DataParameter<Boolean> IGNITE =
        EntityDataManager.createKey(EntityBolt.class, DataSerializers.BOOLEAN);
    private static final int LIFETIME_TICKS = 40;

    private float damage = 4.0F;
    private EntityPlayer credited;

    public EntityBolt(World world) {
        super(world);
        setSize(0.2F, 0.2F);
    }

    /**
     * @param firer    the entity the bolt leaves from (a player or a sentry)
     * @param credited the player credited with the damage, or {@code null} to credit the firer
     */
    public EntityBolt(World world, EntityLivingBase firer, EntityPlayer credited, float damage,
                      boolean ignite) {
        super(world, firer);
        setSize(0.2F, 0.2F);
        this.credited = credited;
        this.damage = damage;
        dataManager.set(IGNITE, ignite);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(IGNITE, false);
    }

    public boolean isIgniting() {
        return dataManager.get(IGNITE);
    }

    /** Who this bolt's damage is credited to: the owning player if known, else the firer. */
    public EntityLivingBase getCreditedShooter() {
        return credited != null ? credited : getThrower();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && ticksExisted > LIFETIME_TICKS) {
            setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote) {
            return;
        }
        Entity hit = result.entityHit;
        EntityLivingBase shooter = getCreditedShooter();
        if (hit != null && shooter != null && Targeting.isEnemy(shooter, hit)) {
            hit.hurtResistantTime = 0;
            hit.attackEntityFrom(DamageSource.causeThrownDamage(this, shooter), damage);
            if (isIgniting()) {
                hit.setFire(3);
            }
        }
        ((WorldServer) world).spawnParticle(isIgniting() ? EnumParticleTypes.FLAME
                : EnumParticleTypes.CRIT, posX, posY, posZ, 4, 0.05D, 0.05D, 0.05D, 0.05D);
        setDead();
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }
}
