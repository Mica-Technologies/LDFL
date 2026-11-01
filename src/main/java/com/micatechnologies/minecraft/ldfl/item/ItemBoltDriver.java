package com.micatechnologies.minecraft.ldfl.item;

import com.micatechnologies.minecraft.ldfl.entity.EntityBolt;
import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * Forgewright's signature sidearm. Right-click fires a fast, flat bolt. During Slag Surge the
 * bolts hit harder and set targets alight. Uses no ammunition and never wears out.
 */
public class ItemBoltDriver extends Item implements ISignatureItem {

    private static final int COOLDOWN_TICKS = 8;
    private static final float DAMAGE = 4.0F;
    private static final float SURGE_DAMAGE = 6.0F;

    public ItemBoltDriver() {
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public String heroId(ItemStack stack) {
        return Heroes.FORGEWRIGHT_ID;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            HeroData data = HeroCapability.get(player);
            boolean surging = data != null && data.surgeTicks > 0;
            EntityBolt bolt = new EntityBolt(world, player, player,
                surging ? SURGE_DAMAGE : DAMAGE, surging);
            bolt.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.0F, 0.2F);
            world.spawnEntity(bolt);
        }
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SHULKER_SHOOT,
            SoundCategory.PLAYERS, 0.6F, 1.9F);
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
