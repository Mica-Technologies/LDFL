package com.micatechnologies.minecraft.ldfl.network;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * LDFL's network channel. Discriminators are wire format: append new packets, never renumber.
 */
public final class LdflNetwork {

    public static final SimpleNetworkWrapper CHANNEL =
        NetworkRegistry.INSTANCE.newSimpleChannel(LdflConstants.MOD_NAMESPACE);

    private LdflNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(PacketUseAbility.Handler.class, PacketUseAbility.class, 0, Side.SERVER);
        CHANNEL.registerMessage(PacketHeroSync.Handler.class, PacketHeroSync.class, 1, Side.CLIENT);
    }
}
