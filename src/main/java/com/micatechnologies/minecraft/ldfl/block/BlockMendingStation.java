package com.micatechnologies.minecraft.ldfl.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A low floor pad with a glowing cross. A hurt player who steps on a charged station is healed,
 * and the station goes dark while it recharges. The charge state is part of the block state, so
 * every player can see from a distance whether a station is ready.
 *
 * <p>Recharging is a scheduled block tick, so a station costs nothing while idle and works in
 * any world, with no tile entity.</p>
 */
public class BlockMendingStation extends Block {

    public static final PropertyBool CHARGED = PropertyBool.create("charged");

    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    private static final float HEAL = 8.0F;
    private static final int RECHARGE_TICKS = 10 * 20;

    public BlockMendingStation() {
        super(Material.IRON, MapColor.SILVER);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setDefaultState(blockState.getBaseState().withProperty(CHARGED, true));
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote || !state.getValue(CHARGED) || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHealth() >= player.getMaxHealth() || player.isSpectator()) {
            return;
        }
        player.heal(HEAL);
        world.setBlockState(pos, state.withProperty(CHARGED, false));
        world.scheduleUpdate(pos, this, RECHARGE_TICKS);
        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.5F, 1.6F);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote && !state.getValue(CHARGED)) {
            world.setBlockState(pos, state.withProperty(CHARGED, true));
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.BLOCKS, 0.4F, 1.4F);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(CHARGED) && rand.nextInt(3) == 0) {
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + 0.2D + rand.nextDouble() * 0.6D,
                pos.getY() + 0.35D, pos.getZ() + 0.2D + rand.nextDouble() * 0.6D, 0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightValue(IBlockState state) {
        return state.getValue(CHARGED) ? 7 : 0;
    }

    // --- Shape -------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos,
                                            EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    // --- State -------------------------------------------------------------------------------

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CHARGED, (meta & 1) == 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CHARGED) ? 0 : 1;
    }
}
