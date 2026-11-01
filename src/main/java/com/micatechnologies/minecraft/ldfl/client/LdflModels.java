package com.micatechnologies.minecraft.ldfl.client;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import com.micatechnologies.minecraft.ldfl.item.LdflItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Item model registration. Client-only by {@code value = Side.CLIENT}. */
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE, value = Side.CLIENT)
public final class LdflModels {

    private LdflModels() {
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (Item item : LdflItems.all()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
