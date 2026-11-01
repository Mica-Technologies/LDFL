package com.micatechnologies.minecraft.ldfl.item;

import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Bulwark's signature shield. Hold right-click to raise it; it blocks like a vanilla shield
 * (frontal attacks and projectiles) and never wears out. Best carried in the offhand, so the
 * mainhand stays free for the maul.
 *
 * <p>Vanilla blocking works for any item whose use action is {@link EnumAction#BLOCK}, and Forge's
 * {@link #isShield} lets axes disable it the same way they disable a vanilla shield.</p>
 */
public class ItemRampartShield extends Item implements ISignatureItem {

    public ItemRampartShield() {
        setMaxStackSize(1);
    }

    @Override
    public String heroId(ItemStack stack) {
        return Heroes.BULWARK_ID;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public boolean isShield(ItemStack stack, EntityLivingBase entity) {
        return true;
    }
}
