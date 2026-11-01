package com.micatechnologies.minecraft.ldfl;

import com.micatechnologies.minecraft.ldfl.item.LdflItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/** LDFL's creative tab. */
public final class LdflTab extends CreativeTabs {

    public static final LdflTab INSTANCE = new LdflTab();

    private LdflTab() {
        super(LdflConstants.MOD_NAMESPACE);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(LdflItems.RAMPART_SHIELD);
    }
}
