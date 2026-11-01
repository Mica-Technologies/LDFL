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
    }
}
