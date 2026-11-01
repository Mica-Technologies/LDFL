package com.micatechnologies.minecraft.ldfl.hero;

import com.micatechnologies.minecraft.ldfl.LdflConfig;
import com.micatechnologies.minecraft.ldfl.LdflConstants;
import com.micatechnologies.minecraft.ldfl.hero.core.Role;
import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Applies and removes role passives that are attribute modifiers (the Tank's). The Damage and
 * Support passives are event-driven and live in {@link HeroEvents}.
 *
 * <p>The modifiers are marked unsaved, because LDFL does not save hero state either: after a relog
 * both are rebuilt from the held items. {@link #reconcile} also always removes before it adds, so
 * it is safe to call any number of times, including on a player whose data still carries modifiers
 * from an older build.</p>
 */
public final class HeroPassives {

    private static final UUID TANK_ARMOR = UUID.fromString("6d1f3c2a-4b8e-4f0a-9c51-1a7e0b3d2f01");
    private static final UUID TANK_HEALTH = UUID.fromString("6d1f3c2a-4b8e-4f0a-9c51-1a7e0b3d2f02");
    private static final UUID TANK_KNOCKBACK = UUID.fromString("6d1f3c2a-4b8e-4f0a-9c51-1a7e0b3d2f03");

    private HeroPassives() {
        throw new AssertionError("No instances.");
    }

    /** Make the player's modifiers match {@code role} (or no role, for {@code null}). */
    public static void reconcile(EntityPlayer player, Role role) {
        boolean tank = role == Role.TANK;
        set(player, SharedMonsterAttributes.ARMOR, TANK_ARMOR, "tank_armor",
            tank ? LdflConfig.passives.tankArmor : 0.0D);
        set(player, SharedMonsterAttributes.MAX_HEALTH, TANK_HEALTH, "tank_health",
            tank ? LdflConfig.passives.tankHealth : 0.0D);
        set(player, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, TANK_KNOCKBACK, "tank_knockback",
            tank ? LdflConfig.passives.tankKnockbackResistance : 0.0D);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void set(EntityPlayer player, IAttribute attribute, UUID id, String name,
                            double amount) {
        IAttributeInstance inst = player.getEntityAttribute(attribute);
        if (inst == null) {
            return;
        }
        inst.removeModifier(id);
        if (amount != 0.0D) {
            inst.applyModifier(new AttributeModifier(id,
                LdflConstants.MOD_NAMESPACE + ":" + name, amount, 0).setSaved(false));
        }
    }
}
