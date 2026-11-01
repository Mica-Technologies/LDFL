package com.micatechnologies.minecraft.ldfl.entity;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

/** Every LDFL entity. Network ids are wire format: append, never renumber. */
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE)
public final class LdflEntities {

    private LdflEntities() {
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntityBarrier.class)
            .id(new ResourceLocation(LdflConstants.MOD_NAMESPACE, "barrier"), 0)
            .name(LdflConstants.MOD_NAMESPACE + ".barrier")
            // Update every tick: the panel follows its owner's facing.
            .tracker(96, 1, false)
            .build());
        event.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntitySentry.class)
            .id(new ResourceLocation(LdflConstants.MOD_NAMESPACE, "sentry"), 1)
            .name(LdflConstants.MOD_NAMESPACE + ".sentry")
            .tracker(80, 2, false)
            .build());
        event.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntityBolt.class)
            .id(new ResourceLocation(LdflConstants.MOD_NAMESPACE, "bolt"), 2)
            .name(LdflConstants.MOD_NAMESPACE + ".bolt")
            .tracker(64, 1, true)
            .build());
        event.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntityHealBeam.class)
            .id(new ResourceLocation(LdflConstants.MOD_NAMESPACE, "heal_beam"), 3)
            .name(LdflConstants.MOD_NAMESPACE + ".heal_beam")
            .tracker(96, 5, false)
            .build());
        event.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntityBloomFlask.class)
            .id(new ResourceLocation(LdflConstants.MOD_NAMESPACE, "bloom_flask"), 4)
            .name(LdflConstants.MOD_NAMESPACE + ".bloom_flask")
            .tracker(64, 2, true)
            .build());
        event.getRegistry().register(EntityEntryBuilder.create()
            .entity(EntityWardBell.class)
            .id(new ResourceLocation(LdflConstants.MOD_NAMESPACE, "ward_bell"), 5)
            .name(LdflConstants.MOD_NAMESPACE + ".ward_bell")
            .tracker(64, 20, false)
            .build());
    }
}
