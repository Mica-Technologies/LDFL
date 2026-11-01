package com.micatechnologies.minecraft.ldfl.client;

import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import com.micatechnologies.minecraft.ldfl.item.ItemLongshotRifle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Kestrel's client-side feel, which has to live on the client because the client owns its own
 * player's movement and camera:
 *
 * <ul>
 *   <li><b>Scope</b>: the view zooms in as the Longshot charges.</li>
 *   <li><b>Glide</b>: after Updraft, falling speed is capped for a few seconds. The server grants
 *       the matching fall-damage immunity, so the two cannot disagree in a way that hurts.</li>
 * </ul>
 */
public final class KestrelClient {

    private static final float ZOOM = 0.55F;
    /** Visual charge time; the server uses the configured value, which may differ slightly. */
    private static final float SCOPE_TICKS = 30.0F;
    private static final int GLIDE_TICKS = 80;
    private static final double GLIDE_FALL_SPEED = -0.12D;

    private static int glideTicks;

    /** Called by {@link ClientHero} when an ability key is accepted locally. */
    static void onAbilityPressed(HeroSpec hero, AbilitySlot slot) {
        if (Heroes.KESTREL_ID.equals(hero.id()) && slot == AbilitySlot.ABILITY_1) {
            glideTicks = GLIDE_TICKS;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || glideTicks <= 0) {
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            glideTicks = 0;
            return;
        }
        glideTicks--;
        if (player.onGround && glideTicks < GLIDE_TICKS - 10) {
            glideTicks = 0;
        } else if (player.motionY < GLIDE_FALL_SPEED) {
            player.motionY = GLIDE_FALL_SPEED;
        }
    }

    @SubscribeEvent
    public void onFov(FOVUpdateEvent event) {
        if (event.getEntity().isHandActive()
            && event.getEntity().getActiveItemStack().getItem() instanceof ItemLongshotRifle) {
            float progress = Math.min(1.0F, event.getEntity().getItemInUseMaxCount() / SCOPE_TICKS);
            event.setNewfov(event.getFov() * (1.0F - ZOOM * progress));
        }
    }
}
