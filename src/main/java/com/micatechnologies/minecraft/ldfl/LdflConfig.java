package com.micatechnologies.minecraft.ldfl;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * LDFL config ({@code config/ldfl.cfg}). Everything here is read on the server, which is
 * authoritative for combat, so nothing needs syncing to clients yet.
 */
@Config(modid = LdflConstants.MOD_NAMESPACE)
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE)
public final class LdflConfig {

    @Config.Comment("Combat rules shared by every hero.")
    public static final Combat combat = new Combat();

    @Config.Comment("Role passive numbers.")
    public static final Passives passives = new Passives();

    public static final class Combat {
        @Config.Comment({"When true, hero abilities and turrets also affect players.",
            "Players on the same scoreboard team are never targeted.",
            "When false (default), kits only affect hostile mobs."})
        public boolean pvp = false;
    }

    public static final class Passives {
        @Config.Comment("Tank: bonus armor points.")
        @Config.RangeDouble(min = 0, max = 30)
        public double tankArmor = 4.0D;

        @Config.Comment("Tank: bonus max health (2 = one heart).")
        @Config.RangeDouble(min = 0, max = 40)
        public double tankHealth = 6.0D;

        @Config.Comment("Tank: knockback resistance (0 to 1).")
        @Config.RangeDouble(min = 0, max = 1)
        public double tankKnockbackResistance = 0.5D;

        @Config.Comment("Damage: seconds a hit leaves the target Wounded.")
        @Config.RangeInt(min = 0, max = 30)
        public int damageWoundSeconds = 3;

        @Config.Comment("Damage: fraction of healing a Wounded target still receives.")
        @Config.RangeDouble(min = 0, max = 1)
        public double woundedHealingFactor = 0.5D;

        @Config.Comment("Support: seconds without taking damage before regen starts.")
        @Config.RangeInt(min = 0, max = 60)
        public int supportRegenDelaySeconds = 3;

        @Config.Comment("Support: health regenerated per second once regen has started.")
        @Config.RangeDouble(min = 0, max = 10)
        public double supportRegenPerSecond = 0.5D;
    }

    private LdflConfig() {
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (LdflConstants.MOD_NAMESPACE.equals(event.getModID())) {
            ConfigManager.sync(LdflConstants.MOD_NAMESPACE, Config.Type.INSTANCE);
        }
    }
}
