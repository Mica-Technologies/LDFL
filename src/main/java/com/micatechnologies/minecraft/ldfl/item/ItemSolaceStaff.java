package com.micatechnologies.minecraft.ldfl.item;

import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Solace's signature staff. Hold right-click to channel the heal beam into the ally in your
 * sights (see {@code SolaceKit}). Never wears out.
 */
public class ItemSolaceStaff extends Item implements ISignatureItem {

    public ItemSolaceStaff() {
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public String heroId(ItemStack stack) {
        return Heroes.SOLACE_ID;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
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
}
