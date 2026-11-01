package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import net.minecraft.potion.Potion;

/**
 * "Marked": applied by Kestrel's Marker Dart. A Marked target takes
 * {@code kestrel.markedDamageBonus} extra damage from every source (applied in
 * {@link HeroEvents}), and the dart also makes it glow.
 */
public class PotionMarked extends Potion {

    public PotionMarked() {
        super(true, 0xE8C23A);
        setRegistryName(LdflConstants.MOD_NAMESPACE, "marked");
        setPotionName("effect." + LdflConstants.MOD_NAMESPACE + ".marked");
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }
}
