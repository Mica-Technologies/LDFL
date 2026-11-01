package com.micatechnologies.minecraft.ldfl.item;

import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import com.micatechnologies.minecraft.ldfl.hero.kestrel.KestrelKit;
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
 * Kestrel's signature rifle. Hold right-click to charge (the view zooms in as it charges), and
 * release to fire an instant Longshot whose damage grows with charge. No ammunition, never wears
 * out. The shot itself is in {@link KestrelKit#fireLongshot}.
 */
public class ItemLongshotRifle extends Item implements ISignatureItem {

    private static final int MAX_USE = 72000;
    private static final int COOLDOWN_TICKS = 10;

    public ItemLongshotRifle() {
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public String heroId(ItemStack stack) {
        return Heroes.KESTREL_ID;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return MAX_USE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if (!world.isRemote) {
            KestrelKit.fireLongshot(player, MAX_USE - timeLeft);
        }
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
    }
}
