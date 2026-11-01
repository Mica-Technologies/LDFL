package com.micatechnologies.minecraft.ldfl.block;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

/** Stairs made from one of the decor blocks; same hardness, sound and look as the full block. */
public class BlockDecorStairs extends BlockStairs {

    public BlockDecorStairs(IBlockState modelState) {
        super(modelState);
        setHarvestLevel("pickaxe", 0);
        // BlockStairs makes every stair cast light occlusion like a full block; fine for metal.
        useNeighborBrightness = true;
    }
}
