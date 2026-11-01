package com.micatechnologies.minecraft.ldfl;

import com.micatechnologies.minecraft.ldfl.client.ClientHero;
import com.micatechnologies.minecraft.ldfl.client.ClientSync;
import com.micatechnologies.minecraft.ldfl.client.HeroHud;
import com.micatechnologies.minecraft.ldfl.client.LdflKeys;
import com.micatechnologies.minecraft.ldfl.client.render.RenderBarrier;
import com.micatechnologies.minecraft.ldfl.entity.EntityBarrier;
import com.micatechnologies.minecraft.ldfl.network.PacketHeroSync;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Client proxy. Everything under {@code client/} is reached only from here.
 */
public class LdflClientProxy extends LdflCommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new ClientHero());
        MinecraftForge.EVENT_BUS.register(new HeroHud());
        RenderingRegistry.registerEntityRenderingHandler(EntityBarrier.class, RenderBarrier::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        LdflKeys.register();
    }

    @Override
    public void onHeroSync(PacketHeroSync message) {
        ClientSync.schedule(message);
    }
}
