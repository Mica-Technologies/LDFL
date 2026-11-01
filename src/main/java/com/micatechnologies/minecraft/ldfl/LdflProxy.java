package com.micatechnologies.minecraft.ldfl;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Sided-proxy contract. The only sanctioned bridge from common code into client-only classes:
 * a {@code net.minecraft.client} import in common code compiles fine and only fails when a
 * dedicated server boots.
 */
public interface LdflProxy {

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);
}
