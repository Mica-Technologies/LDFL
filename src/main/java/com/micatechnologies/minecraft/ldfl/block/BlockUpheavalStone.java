package com.micatechnologies.minecraft.ldfl.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * The temporary stone Monolith's Upheaval raises. It crumbles back to air on a scheduled tick
 * after {@code monolith.upheavalSeconds}, drops nothing and cannot be pushed, so it can never be
 * farmed or left behind. It has no item and is not in the creative tab.
 *
 * <p>Upheaval only ever places it into air or replaceable plants, so crumbling to air restores
 * the world (minus any grass it grew through).</p>
 */
public class BlockUpheavalStone extends Block {

    public BlockUpheavalStone() {
        super(Material.ROCK, MapColor.STONE);
        setHardness(1.5F);
        setResistance(6.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        world.setBlockToAir(pos);
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.BLOCK_CRACK, pos.getX() + 0.5D,
                pos.getY() + 0.5D, pos.getZ() + 0.5D, 12, 0.3D, 0.3D, 0.3D, 0.05D,
                Block.getStateId(Blocks.STONE.getDefaultState()));
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }
}
