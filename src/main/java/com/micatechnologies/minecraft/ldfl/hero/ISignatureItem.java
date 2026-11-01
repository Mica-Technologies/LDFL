package com.micatechnologies.minecraft.ldfl.hero;

import net.minecraft.item.ItemStack;

/**
 * Implemented by every hero signature item. Holding one in either hand makes the player that hero
 * (mainhand wins; see {@code HeroResolver}).
 */
public interface ISignatureItem {

    /** The hero this stack belongs to, as registered in {@link HeroRegistry}. */
    String heroId(ItemStack stack);
}
