package com.micatechnologies.minecraft.ldfl.item;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import com.micatechnologies.minecraft.ldfl.LdflTab;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Every LDFL item. Registry names are save-compatible: never rename one. */
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE)
public final class LdflItems {

    private static final List<Item> ALL = new ArrayList<>();

    public static final Item BULWARK_SHIELD = add(new ItemBulwarkShield(), "bulwark_shield");
    public static final Item BULWARK_MAUL = add(new ItemBulwarkMaul(), "bulwark_maul");
    public static final Item BOLT_DRIVER = add(new ItemBoltDriver(), "bolt_driver");
    public static final Item TINKERS_WRENCH = add(new ItemTinkersWrench(), "tinkers_wrench");
    public static final Item SOLACE_STAFF = add(new ItemSolaceStaff(), "solace_staff");
    public static final Item CRAGFIST = add(
        new ItemHeroMelee(com.micatechnologies.minecraft.ldfl.hero.Heroes.MONOLITH_ID, 8.0D, -2.9D), "cragfist");
    public static final Item LONGSHOT_RIFLE = add(new ItemLongshotRifle(), "longshot_rifle");
    public static final Item TUNING_ROD = add(new ItemTuningRod(), "tuning_rod");

    private LdflItems() {
    }

    private static Item add(Item item, String name) {
        item.setRegistryName(LdflConstants.MOD_NAMESPACE, name);
        item.setTranslationKey(LdflConstants.MOD_NAMESPACE + "." + name);
        item.setCreativeTab(LdflTab.INSTANCE);
        ALL.add(item);
        return item;
    }

    public static List<Item> all() {
        return Collections.unmodifiableList(ALL);
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        for (Item item : ALL) {
            event.getRegistry().register(item);
        }
    }
}
