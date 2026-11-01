package com.micatechnologies.minecraft.ldfl;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/** Dedicated-server proxy: every client hook is a no-op. */
public class LdflCommonProxy implements LdflProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {
    }
}
