package com.micatechnologies.minecraft.ldfl.hero.monolith;

import com.micatechnologies.minecraft.ldfl.LdflSounds;
import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.block.LdflBlocks;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.Targeting;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

/**
 * Monolith, the stone Tank: the server-side behaviour of its three abilities.
 *
 * <ul>
 *   <li><b>Stone Skin</b>: absorption hearts for six seconds.</li>
 *   <li><b>Upheaval</b>: raise a five-wide, two-high wall of temporary stone three blocks ahead,
 *       across your line of sight. It only fills air and plants, and crumbles on its own.</li>
 *   <li><b>Bedrock Anchor</b> (ultimate): for a few seconds you cannot be knocked back, take less
 *       damage, and every hostile mob nearby is forced to attack you, re-taunted each second.</li>
 * </ul>
 */
public final class MonolithKit {

    private static final UUID ANCHOR_KNOCKBACK = UUID.fromString("2f4d8c1e-9a7b-4e3f-8d21-5c6b7a8e9f01");
    private static final int STONE_SKIN_TICKS = 6 * 20;
    private static final int WALL_HALF_WIDTH = 2;
    private static final double WALL_DISTANCE = 3.0D;

    private MonolithKit() {
        throw new AssertionError("No instances.");
    }

    public static boolean stoneSkin(EntityPlayerMP p, HeroData data) {
        int amplifier = Math.max(0, LdflConfig.monolith.stoneSkinAbsorption / 4 - 1);
        p.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, STONE_SKIN_TICKS, amplifier));
        p.world.playSound(null, p.posX, p.posY, p.posZ, LdflSounds.STONE_SKIN,
            SoundCategory.PLAYERS, 1.0F, 0.6F);
        p.getServerWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK, p.posX, p.posY + 1.0D, p.posZ,
            20, 0.4D, 0.6D, 0.4D, 0.05D, Block.getStateId(Blocks.STONE.getDefaultState()));
        return true;
    }

    public static boolean upheaval(EntityPlayerMP p, HeroData data) {
        WorldServer world = p.getServerWorld();
        float yaw = p.rotationYaw * 0.017453292F;
        Vec3d fwd = new Vec3d(-MathHelper.sin(yaw), 0.0D, MathHelper.cos(yaw));
        Vec3d side = new Vec3d(-fwd.z, 0.0D, fwd.x);
        Vec3d centre = new Vec3d(p.posX, p.posY, p.posZ).add(fwd.scale(WALL_DISTANCE));
        int lifetime = LdflConfig.ticks(LdflConfig.monolith.upheavalSeconds);
        int placed = 0;
        for (int i = -WALL_HALF_WIDTH; i <= WALL_HALF_WIDTH; i++) {
            Vec3d c = centre.add(side.scale(i));
            BlockPos base = new BlockPos(c.x, p.posY, c.z);
            // Step up over a one-block rise, so the wall follows gentle terrain.
            if (!canFill(world, base) && canFill(world, base.up())) {
                base = base.up();
            }
            for (int h = 0; h < 2; h++) {
                BlockPos at = base.up(h);
                if (canFill(world, at) && world.getEntitiesWithinAABB(EntityPlayerMP.class,
                    new net.minecraft.util.math.AxisAlignedBB(at)).isEmpty()) {
                    world.setBlockState(at, LdflBlocks.UPHEAVAL_STONE.getDefaultState());
                    world.scheduleUpdate(at, LdflBlocks.UPHEAVAL_STONE, lifetime + world.rand.nextInt(10));
                    placed++;
                }
            }
        }
        if (placed > 0) {
            world.playSound(null, centre.x, centre.y, centre.z, LdflSounds.UPHEAVAL,
                SoundCategory.PLAYERS, 0.7F, 0.5F);
            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, centre.x, centre.y + 0.5D, centre.z, 40,
                1.5D, 0.5D, 1.5D, 0.1D, Block.getStateId(Blocks.STONE.getDefaultState()));
        }
        return placed > 0;
    }

    private static boolean canFill(WorldServer world, BlockPos pos) {
        return world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock().isReplaceable(world, pos)
            && world.getBlockState(pos).getMaterial() != net.minecraft.block.material.Material.WATER
            && world.getBlockState(pos).getMaterial() != net.minecraft.block.material.Material.LAVA;
    }

    public static boolean bedrockAnchor(EntityPlayerMP p, HeroData data) {
        int ticks = LdflConfig.ticks(LdflConfig.monolith.anchorSeconds);
        data.anchorTicks = ticks;
        p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, ticks, 0));
        setKnockbackImmune(p, true);
        taunt(p);
        p.world.playSound(null, p.posX, p.posY, p.posZ, LdflSounds.BEDROCK_ANCHOR,
            SoundCategory.PLAYERS, 1.0F, 0.5F);
        return true;
    }

    /** Each tick while Monolith: run the anchor down and keep taunting. */
    public static void tick(EntityPlayerMP p, HeroData data) {
        if (data.anchorTicks <= 0) {
            return;
        }
        data.anchorTicks--;
        if (data.anchorTicks % 20 == 0) {
            taunt(p);
        }
        if (data.anchorTicks % 4 == 0) {
            p.getServerWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK, p.posX, p.posY + 0.1D,
                p.posZ, 4, 0.5D, 0.0D, 0.5D, 0.0D, Block.getStateId(Blocks.BEDROCK.getDefaultState()));
        }
        if (data.anchorTicks == 0) {
            setKnockbackImmune(p, false);
        }
        int total = LdflConfig.ticks(LdflConfig.monolith.anchorSeconds);
        data.resource = total > 0 ? data.anchorTicks / (float) total : -1.0F;
    }

    /** While not Monolith: end any anchor. Cheap enough to run every tick. */
    public static void cleanup(EntityPlayerMP p, HeroData data) {
        if (data.anchorTicks > 0) {
            data.anchorTicks = 0;
            setKnockbackImmune(p, false);
        }
    }

    private static void taunt(EntityPlayerMP p) {
        double r = LdflConfig.monolith.tauntRadius;
        for (EntityLiving mob : p.world.getEntitiesWithinAABB(EntityLiving.class,
            p.getEntityBoundingBox().grow(r))) {
            if (Targeting.isEnemy(p, mob) && mob.getDistanceSq(p) <= r * r) {
                mob.setAttackTarget(p);
            }
        }
    }

    private static void setKnockbackImmune(EntityPlayerMP p, boolean immune) {
        IAttributeInstance kb = p.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        kb.removeModifier(ANCHOR_KNOCKBACK);
        if (immune) {
            kb.applyModifier(new AttributeModifier(ANCHOR_KNOCKBACK, "ldfl:bedrock_anchor", 1.0D, 0)
                .setSaved(false));
        }
    }
}
