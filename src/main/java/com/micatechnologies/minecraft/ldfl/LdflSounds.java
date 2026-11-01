package com.micatechnologies.minecraft.ldfl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Every LDFL sound event. The audio is synthesised by {@code tools/audio/sounds.sh}; the names
 * here, in {@code sounds.json} and in that script must match.
 */
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE)
public final class LdflSounds {

    private static final List<SoundEvent> ALL = new ArrayList<>();

    public static final SoundEvent BARRIER_RAISE = add("barrier_raise");
    public static final SoundEvent BARRIER_HIT = add("barrier_hit");
    public static final SoundEvent BARRIER_BREAK = add("barrier_break");
    public static final SoundEvent RUSH = add("rush");
    public static final SoundEvent SHOCKWAVE = add("shockwave");
    public static final SoundEvent TECTONIC_SLAM = add("tectonic_slam");
    public static final SoundEvent SENTRY_DEPLOY = add("sentry_deploy");
    public static final SoundEvent SENTRY_FIRE = add("sentry_fire");
    public static final SoundEvent BOLT_FIRE = add("bolt_fire");
    public static final SoundEvent SLAG_SURGE = add("slag_surge");
    public static final SoundEvent FLASK_BURST = add("flask_burst");
    public static final SoundEvent SANCTUARY = add("sanctuary");
    public static final SoundEvent TAILWIND = add("tailwind");
    public static final SoundEvent STONE_SKIN = add("stone_skin");
    public static final SoundEvent UPHEAVAL = add("upheaval");
    public static final SoundEvent BEDROCK_ANCHOR = add("bedrock_anchor");
    public static final SoundEvent LONGSHOT = add("longshot");
    public static final SoundEvent MARKER_DART = add("marker_dart");
    public static final SoundEvent UPDRAFT = add("updraft");
    public static final SoundEvent LONG_VIGIL = add("long_vigil");
    public static final SoundEvent ORB = add("orb");
    public static final SoundEvent RESONANCE = add("resonance");
    public static final SoundEvent WARD_BELL = add("ward_bell");
    public static final SoundEvent CRESCENDO = add("crescendo");
    public static final SoundEvent ULT_READY = add("ult_ready");
    public static final SoundEvent MENDING = add("mending");

    private LdflSounds() {
    }

    private static SoundEvent add(String name) {
        ResourceLocation id = new ResourceLocation(LdflConstants.MOD_NAMESPACE, name);
        SoundEvent e = new SoundEvent(id).setRegistryName(id);
        ALL.add(e);
        return e;
    }

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent e : ALL) {
            event.getRegistry().register(e);
        }
    }
}
