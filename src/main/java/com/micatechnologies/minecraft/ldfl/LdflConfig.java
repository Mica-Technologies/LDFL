package com.micatechnologies.minecraft.ldfl;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * LDFL config ({@code config/ldfl.cfg}).
 *
 * <p>Everything here is read on the server, which is authoritative for combat, so nothing needs
 * syncing to clients: the HUD shows cooldowns and charge as the server reports them. Values are
 * read when they are used, so a change made through the in-game config screen (or a reload) applies
 * without a restart. The one exception is the sentry's max health, which applies to sentries
 * deployed after the change.</p>
 */
@Config(modid = LdflConstants.MOD_NAMESPACE)
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE)
public final class LdflConfig {

    @Config.Comment("Combat rules shared by every hero.")
    public static final Combat combat = new Combat();

    @Config.Comment("Role passive numbers.")
    public static final Passives passives = new Passives();

    @Config.Comment("Bulwark (Tank) tuning.")
    public static final Bulwark bulwark = new Bulwark();

    @Config.Comment("Forgewright (Damage) tuning.")
    public static final Forgewright forgewright = new Forgewright();

    @Config.Comment("Solace (Support) tuning.")
    public static final Solace solace = new Solace();

    @Config.Comment("Monolith (Tank) tuning.")
    public static final Monolith monolith = new Monolith();

    @Config.Comment("Kestrel (Damage) tuning.")
    public static final Kestrel kestrel = new Kestrel();

    @Config.Comment("Chime (Support) tuning.")
    public static final Chime chime = new Chime();

    public static final class Monolith {
        @Config.Comment("Stone Skin cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double stoneSkinCooldown = 12.0D;

        @Config.Comment("Stone Skin absorption, in health points (2 = one heart).")
        @Config.RangeInt(min = 0, max = 40)
        public int stoneSkinAbsorption = 8;

        @Config.Comment("Upheaval cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double upheavalCooldown = 10.0D;

        @Config.Comment("Seconds an Upheaval wall stands before crumbling.")
        @Config.RangeDouble(min = 1, max = 60)
        public double upheavalSeconds = 6.0D;

        @Config.Comment("Bedrock Anchor (ultimate) duration, seconds.")
        @Config.RangeDouble(min = 1, max = 60)
        public double anchorSeconds = 6.0D;

        @Config.Comment("Radius, in blocks, in which Bedrock Anchor taunts hostile mobs.")
        @Config.RangeDouble(min = 1, max = 48)
        public double tauntRadius = 16.0D;

        @Config.Comment("Damage dealt to fill the ultimate meter.")
        @Config.RangeDouble(min = 1, max = 10000)
        public double ultCost = 180.0D;
    }

    public static final class Kestrel {
        @Config.Comment("Longshot damage at no charge.")
        @Config.RangeDouble(min = 0, max = 100)
        public double longshotMinDamage = 4.0D;

        @Config.Comment("Longshot damage at full charge.")
        @Config.RangeDouble(min = 0, max = 200)
        public double longshotMaxDamage = 16.0D;

        @Config.Comment("Seconds to fully charge a Longshot (halved during Long Vigil).")
        @Config.RangeDouble(min = 0.1, max = 10)
        public double longshotChargeSeconds = 1.5D;

        @Config.Comment("Updraft cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double updraftCooldown = 8.0D;

        @Config.Comment("Marker Dart cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double markerDartCooldown = 10.0D;

        @Config.Comment("Seconds a Marker Dart leaves its target marked.")
        @Config.RangeDouble(min = 1, max = 60)
        public double markedSeconds = 6.0D;

        @Config.Comment("Extra damage a Marked target takes (0.3 = +30%).")
        @Config.RangeDouble(min = 0, max = 5)
        public double markedDamageBonus = 0.3D;

        @Config.Comment("Long Vigil (ultimate) duration, seconds.")
        @Config.RangeDouble(min = 1, max = 60)
        public double vigilSeconds = 8.0D;

        @Config.Comment("Damage dealt to fill the ultimate meter.")
        @Config.RangeDouble(min = 1, max = 10000)
        public double ultCost = 220.0D;
    }

    public static final class Chime {
        @Config.Comment("Tuning Rod orb damage.")
        @Config.RangeDouble(min = 0, max = 100)
        public double orbDamage = 3.5D;

        @Config.Comment("Resonance cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double resonanceCooldown = 14.0D;

        @Config.Comment("Ward Bell cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double wardBellCooldown = 16.0D;

        @Config.Comment("Seconds a Ward Bell rings before it fades.")
        @Config.RangeDouble(min = 1, max = 60)
        public double wardBellSeconds = 10.0D;

        @Config.Comment("Crescendo (ultimate) duration, seconds.")
        @Config.RangeDouble(min = 1, max = 30)
        public double crescendoSeconds = 3.0D;

        @Config.Comment("Damage dealt plus support given to fill the ultimate meter.")
        @Config.RangeDouble(min = 1, max = 10000)
        public double ultCost = 200.0D;
    }

    public static final class Combat {
        @Config.Comment({"When true, hero abilities and turrets also affect players.",
            "Players on the same scoreboard team are never targeted.",
            "When false (default), kits only affect hostile mobs, and every player counts as an ally."})
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

        @Config.Comment("Fraction of healing a Wounded target still receives.")
        @Config.RangeDouble(min = 0, max = 1)
        public double woundedHealingFactor = 0.5D;

        @Config.Comment("Support: seconds without taking damage before regen starts.")
        @Config.RangeInt(min = 0, max = 60)
        public int supportRegenDelaySeconds = 3;

        @Config.Comment("Support: health regenerated per second once regen has started.")
        @Config.RangeDouble(min = 0, max = 10)
        public double supportRegenPerSecond = 0.5D;
    }

    public static final class Bulwark {
        @Config.Comment("Shoulder Rush cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double shoulderRushCooldown = 8.0D;

        @Config.Comment("Shoulder Rush damage to the enemy it hits.")
        @Config.RangeDouble(min = 0, max = 100)
        public double shoulderRushDamage = 6.0D;

        @Config.Comment("Shockwave cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double shockwaveCooldown = 6.0D;

        @Config.Comment("Shockwave damage to each enemy along the line.")
        @Config.RangeDouble(min = 0, max = 100)
        public double shockwaveDamage = 5.0D;

        @Config.Comment("Tectonic Slam (ultimate) damage to each enemy in the cone.")
        @Config.RangeDouble(min = 0, max = 100)
        public double tectonicSlamDamage = 6.0D;

        @Config.Comment("Damage dealt to fill the ultimate meter.")
        @Config.RangeDouble(min = 1, max = 10000)
        public double ultCost = 160.0D;

        @Config.Comment("Barrier health.")
        @Config.RangeDouble(min = 1, max = 1000)
        public double barrierHealth = 60.0D;

        @Config.Comment("Barrier health regenerated per second while lowered.")
        @Config.RangeDouble(min = 0, max = 1000)
        public double barrierRegenPerSecond = 6.0D;

        @Config.Comment("Seconds a broken barrier cannot be raised.")
        @Config.RangeDouble(min = 0, max = 120)
        public double barrierBrokenSeconds = 5.0D;
    }

    public static final class Forgewright {
        @Config.Comment("Deploy Sentry cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double deploySentryCooldown = 10.0D;

        @Config.Comment("Plating cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double platingCooldown = 12.0D;

        @Config.Comment("Damage dealt to fill the ultimate meter.")
        @Config.RangeDouble(min = 1, max = 10000)
        public double ultCost = 200.0D;

        @Config.Comment("Slag Surge (ultimate) duration, seconds.")
        @Config.RangeDouble(min = 1, max = 60)
        public double slagSurgeSeconds = 8.0D;

        @Config.Comment("Bolt Driver damage per bolt (1.5x during Slag Surge).")
        @Config.RangeDouble(min = 0, max = 100)
        public double boltDamage = 4.0D;

        @Config.Comment("Sentry max health. Applies to sentries deployed after a change.")
        @Config.RangeDouble(min = 1, max = 1000)
        public double sentryHealth = 30.0D;

        @Config.Comment("Sentry damage per bolt.")
        @Config.RangeDouble(min = 0, max = 100)
        public double sentryBoltDamage = 3.0D;

        @Config.Comment("Ticks between sentry shots (halved during Slag Surge). 20 ticks = 1 second.")
        @Config.RangeInt(min = 1, max = 200)
        public int sentryFireIntervalTicks = 10;

        @Config.Comment("Sentry targeting range, blocks.")
        @Config.RangeDouble(min = 2, max = 64)
        public double sentryRange = 16.0D;
    }

    public static final class Solace {
        @Config.Comment("Bloom Flask cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double bloomFlaskCooldown = 8.0D;

        @Config.Comment("Bloom Flask healing to each ally in the burst.")
        @Config.RangeDouble(min = 0, max = 100)
        public double bloomFlaskHeal = 6.0D;

        @Config.Comment("Bloom Flask damage to each enemy in the burst.")
        @Config.RangeDouble(min = 0, max = 100)
        public double bloomFlaskDamage = 3.0D;

        @Config.Comment("Tailwind cooldown, seconds.")
        @Config.RangeDouble(min = 0, max = 120)
        public double tailwindCooldown = 12.0D;

        @Config.Comment("Heal beam healing per second.")
        @Config.RangeDouble(min = 0, max = 100)
        public double beamHealPerSecond = 3.0D;

        @Config.Comment("Sanctuary (ultimate) instant healing to each ally.")
        @Config.RangeDouble(min = 0, max = 100)
        public double sanctuaryHeal = 10.0D;

        @Config.Comment("Healing done to fill the ultimate meter.")
        @Config.RangeDouble(min = 1, max = 10000)
        public double ultCost = 200.0D;
    }

    private LdflConfig() {
    }

    /** Seconds from config to whole ticks. */
    public static int ticks(double seconds) {
        return (int) Math.round(seconds * LdflConstants.TICKS_PER_SECOND);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (LdflConstants.MOD_NAMESPACE.equals(event.getModID())) {
            ConfigManager.sync(LdflConstants.MOD_NAMESPACE, Config.Type.INSTANCE);
        }
    }
}
