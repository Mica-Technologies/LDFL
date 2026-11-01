package com.micatechnologies.minecraft.ldfl.item;

import com.google.common.collect.Multimap;
import com.micatechnologies.minecraft.ldfl.hero.ISignatureItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A plain signature melee weapon: fixed attack damage and speed, never wears out. For heroes whose
 * weapon needs no behaviour beyond hitting things.
 */
public class ItemHeroMelee extends Item implements ISignatureItem {

    private final String heroId;
    private final double attackDamage;
    private final double attackSpeed;

    /**
     * @param attackDamage total damage minus the player's base 1
     * @param attackSpeed  modifier on the player's base 4.0 attacks per second (negative is slower)
     */
    public ItemHeroMelee(String heroId, double attackDamage, double attackSpeed) {
        this.heroId = heroId;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public String heroId(ItemStack stack) {
        return heroId;
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
                new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
        }
        return map;
    }
}
