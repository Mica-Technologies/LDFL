package com.micatechnologies.minecraft.ldfl.client;

import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * Ability keybinds (decision D9): R, G and Z by default. These avoid sneak, drop, swap-hands
 * (needed for the offhand shield) and the creative toolbar keys. All rebindable.
 */
public final class LdflKeys {

    private static final String CATEGORY = "key.categories.ldfl";

    public static final KeyBinding ABILITY_1 = new KeyBinding("key.ldfl.ability_1",
        KeyConflictContext.IN_GAME, Keyboard.KEY_R, CATEGORY);
    public static final KeyBinding ABILITY_2 = new KeyBinding("key.ldfl.ability_2",
        KeyConflictContext.IN_GAME, Keyboard.KEY_G, CATEGORY);
    public static final KeyBinding ULTIMATE = new KeyBinding("key.ldfl.ultimate",
        KeyConflictContext.IN_GAME, Keyboard.KEY_Z, CATEGORY);

    private LdflKeys() {
    }

    public static void register() {
        ClientRegistry.registerKeyBinding(ABILITY_1);
        ClientRegistry.registerKeyBinding(ABILITY_2);
        ClientRegistry.registerKeyBinding(ULTIMATE);
    }

    public static KeyBinding forSlot(AbilitySlot slot) {
        switch (slot) {
            case ABILITY_1:
                return ABILITY_1;
            case ABILITY_2:
                return ABILITY_2;
            default:
                return ULTIMATE;
        }
    }
}
