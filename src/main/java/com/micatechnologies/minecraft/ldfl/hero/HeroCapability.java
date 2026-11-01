package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Attaches {@link HeroData} to every player. Not serialised: see {@link HeroData}.
 */
public final class HeroCapability {

    @CapabilityInject(HeroData.class)
    public static Capability<HeroData> HERO_DATA = null;

    private static final ResourceLocation KEY =
        new ResourceLocation(LdflConstants.MOD_NAMESPACE, "hero_data");

    private HeroCapability() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(HeroData.class, new Capability.IStorage<HeroData>() {
            @Override
            public NBTBase writeNBT(Capability<HeroData> capability, HeroData instance,
                                    EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<HeroData> capability, HeroData instance,
                                EnumFacing side, NBTBase nbt) {
            }
        }, HeroData::new);
    }

    /** The player's hero data. Every player has one once the capability is attached. */
    public static HeroData get(EntityPlayer player) {
        return player.getCapability(HERO_DATA, null);
    }

    /** Registered on the Forge event bus by {@code Ldfl.preInit}. */
    public static final class Attacher {
        @SubscribeEvent
        public void onAttach(AttachCapabilitiesEvent<Entity> event) {
            if (!(event.getObject() instanceof EntityPlayer)) {
                return;
            }
            final HeroData data = new HeroData();
            event.addCapability(KEY, new ICapabilityProvider() {
                @Override
                public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                    return capability == HERO_DATA;
                }

                @Override
                public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                    return capability == HERO_DATA ? HERO_DATA.cast(data) : null;
                }
            });
        }
    }
}
