package com.micatechnologies.minecraft.ldfl.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/** A plain full-cube decor block from LDFL's futuristic building set. */
public class BlockDecor extends Block {

    public BlockDecor(MapColor color, float light) {
        super(Material.IRON, color);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setLightLevel(light);
    }
}
