package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import net.minecraft.potion.Potion;

/**
 * "Wounded": the Damage role passive. While it lasts, the target receives only
 * {@code passives.woundedHealingFactor} of any healing (applied in {@link HeroEvents}).
 */
public class PotionWounded extends Potion {

    public PotionWounded() {
        super(true, 0x8B1A1A);
        setRegistryName(LdflConstants.MOD_NAMESPACE, "wounded");
        setPotionName("effect." + LdflConstants.MOD_NAMESPACE + ".wounded");
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }
}
