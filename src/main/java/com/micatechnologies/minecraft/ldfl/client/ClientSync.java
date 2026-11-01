package com.micatechnologies.minecraft.ldfl.client;

import com.micatechnologies.minecraft.ldfl.network.PacketHeroSync;
import net.minecraft.client.Minecraft;

/** Moves an incoming hero sync from the network thread onto the client thread. */
public final class ClientSync {

    private ClientSync() {
    }

    public static void schedule(PacketHeroSync message) {
        Minecraft.getMinecraft().addScheduledTask(() -> ClientHero.apply(message));
    }
}
