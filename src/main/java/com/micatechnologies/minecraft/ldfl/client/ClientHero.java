package com.micatechnologies.minecraft.ldfl.client;

import com.micatechnologies.minecraft.ldfl.LdflSounds;
import com.micatechnologies.minecraft.ldfl.hero.HeroDefinition;
import com.micatechnologies.minecraft.ldfl.hero.HeroRegistry;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroState;
import com.micatechnologies.minecraft.ldfl.network.LdflNetwork;
import com.micatechnologies.minecraft.ldfl.network.PacketHeroSync;
import com.micatechnologies.minecraft.ldfl.network.PacketUseAbility;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * The client's mirror of its own hero state, for the HUD and to avoid sending pointless key
 * presses. The server's copy is the real one (decision D8); this mirror ticks cooldowns down
 * locally between syncs so the HUD counts smoothly.
 */
public final class ClientHero {

    public static final HeroState STATE = new HeroState();

    /** The hero's extra resource as 0-1 (Bulwark: barrier health), or -1 for none. */
    public static float resource = -1.0F;

    /** Apply a server sync. Called on the client thread. */
    static void apply(PacketHeroSync msg) {
        HeroDefinition def = HeroRegistry.get(msg.heroId.isEmpty() ? null : msg.heroId);
        boolean heroChanged = STATE.setHero(def == null ? null : def.spec());
        boolean wasReady = STATE.ultReady();
        STATE.applySync(msg.cooldowns, msg.ultCharge);
        resource = msg.resource;
        // A short chime, heard only by this player, the moment the ultimate becomes ready.
        if (!heroChanged && !wasReady && STATE.ultReady() && Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.playSound(LdflSounds.ULT_READY, 0.8F, 1.0F);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            STATE.setHero(null);
            return;
        }
        if (mc.isGamePaused()) {
            return;
        }
        STATE.tick();
        for (AbilitySlot slot : AbilitySlot.values()) {
            while (LdflKeys.forSlot(slot).isPressed()) {
                if (STATE.hero() != null) {
                    if (STATE.check(slot) == HeroState.Activation.OK) {
                        KestrelClient.onAbilityPressed(STATE.hero(), slot);
                    }
                    LdflNetwork.CHANNEL.sendToServer(new PacketUseAbility(slot));
                }
            }
        }
    }
}
