package com.micatechnologies.minecraft.ldfl.item;

import com.micatechnologies.minecraft.ldfl.LdflSounds;
import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.entity.EntityBolt;
import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * Chime's signature rod. Right-click fires a slow resonant orb, so the support can still fight.
 * Orb damage counts toward Chime's ultimate like any other damage.
 */
public class ItemTuningRod extends Item implements ISignatureItem {

    private static final int COOLDOWN_TICKS = 12;

    public ItemTuningRod() {
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public String heroId(ItemStack stack) {
        return Heroes.CHIME_ID;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            EntityBolt orb = new EntityBolt(world, player, player,
                (float) LdflConfig.chime.orbDamage, false).asOrb();
            orb.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.6F, 0.3F);
            world.spawnEntity(orb);
        }
        world.playSound(null, player.posX, player.posY, player.posZ, LdflSounds.ORB,
            SoundCategory.PLAYERS, 0.6F, 1.6F + world.rand.nextFloat() * 0.3F);
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
