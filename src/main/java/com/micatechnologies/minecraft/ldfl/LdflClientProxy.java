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
        RenderingRegistry.registerEntityRenderingHandler(
            com.micatechnologies.minecraft.ldfl.entity.EntitySentry.class,
            com.micatechnologies.minecraft.ldfl.client.render.RenderSentry::new);
        RenderingRegistry.registerEntityRenderingHandler(
            com.micatechnologies.minecraft.ldfl.entity.EntityBolt.class,
            com.micatechnologies.minecraft.ldfl.client.render.RenderBolt::new);
        RenderingRegistry.registerEntityRenderingHandler(
            com.micatechnologies.minecraft.ldfl.entity.EntityHealBeam.class,
            com.micatechnologies.minecraft.ldfl.client.render.RenderHealBeam::new);
        RenderingRegistry.registerEntityRenderingHandler(
            com.micatechnologies.minecraft.ldfl.entity.EntityBloomFlask.class,
            com.micatechnologies.minecraft.ldfl.client.render.RenderBloomFlask::new);
        RenderingRegistry.registerEntityRenderingHandler(
            com.micatechnologies.minecraft.ldfl.entity.EntityWardBell.class,
            com.micatechnologies.minecraft.ldfl.client.render.RenderWardBell::new);
        MinecraftForge.EVENT_BUS.register(new com.micatechnologies.minecraft.ldfl.client.KestrelClient());
        // The raised-shield model (item/bulwark_shield_blocking) is chosen by a "blocking" item
        // property, as for the vanilla shield. Registered here because IItemPropertyGetter is
        // client-only: creating one in common code would crash a dedicated server.
        com.micatechnologies.minecraft.ldfl.item.LdflItems.BULWARK_SHIELD.addPropertyOverride(
            new net.minecraft.util.ResourceLocation("blocking"),
            (stack, world, entity) -> entity != null && entity.isHandActive()
                && entity.getActiveItemStack() == stack ? 1.0F : 0.0F);
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
