package com.micatechnologies.minecraft.ldfl.item;

import com.google.common.collect.Multimap;
import com.micatechnologies.minecraft.ldfl.hero.Heroes;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Forgewright's signature wrench: a quick melee tool. Hitting your own (or an ally's) sentry with
 * it repairs the sentry instead of damaging it; see {@code EntitySentry#attackEntityFrom}.
 */
public class ItemTinkersWrench extends Item implements ISignatureItem {

    private static final double ATTACK_DAMAGE = 4.0D;
    private static final double ATTACK_SPEED = -2.2D;

    public ItemTinkersWrench() {
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public String heroId(ItemStack stack) {
        return Heroes.FORGEWRIGHT_ID;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ATTACK_DAMAGE, 0));
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ATTACK_SPEED, 0));
        }
        return map;
    }
}
