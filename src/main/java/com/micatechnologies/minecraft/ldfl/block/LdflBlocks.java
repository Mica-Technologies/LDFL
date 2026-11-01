package com.micatechnologies.minecraft.ldfl.block;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import com.micatechnologies.minecraft.ldfl.LdflTab;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * LDFL's futuristic building set. Registry names are save-compatible: never rename one.
 * Every block gets a plain {@link ItemBlock}; their item models are registered with the rest in
 * {@code client.LdflModels}.
 */
@Mod.EventBusSubscriber(modid = LdflConstants.MOD_NAMESPACE)
public final class LdflBlocks {

    private static final List<Block> ALL = new ArrayList<>();

    public static final Block HULL_PANEL_LIGHT = add(new BlockDecor(MapColor.SILVER, 0.0F), "hull_panel_light");
    public static final Block HULL_PANEL_DARK = add(new BlockDecor(MapColor.GRAY, 0.0F), "hull_panel_dark");
    public static final Block HULL_PANEL_ACCENT = add(new BlockDecor(MapColor.ADOBE, 0.0F), "hull_panel_accent");
    public static final Block LUMEN_PANEL = add(new BlockDecor(MapColor.DIAMOND, 1.0F), "lumen_panel");
    public static final Block GRID_FLOOR = add(new BlockDecor(MapColor.GRAY, 0.0F), "grid_floor");
    public static final Block VENT_PANEL = add(new BlockDecor(MapColor.GRAY, 0.0F), "vent_panel");
    public static final Block OBJECTIVE_PAD = add(new BlockDecor(MapColor.CYAN, 0.6F), "objective_pad");
    public static final Block HULL_PANEL_LIGHT_STAIRS =
        add(new BlockDecorStairs(HULL_PANEL_LIGHT.getDefaultState()), "hull_panel_light_stairs");
    public static final Block HULL_PANEL_DARK_STAIRS =
        add(new BlockDecorStairs(HULL_PANEL_DARK.getDefaultState()), "hull_panel_dark_stairs");
    public static final Block MENDING_STATION = add(new BlockMendingStation(), "mending_station");

    /** Monolith's temporary wall. Not in {@link #all()}: it has no item and no creative tab entry. */
    public static final Block UPHEAVAL_STONE = new BlockUpheavalStone()
        .setRegistryName(LdflConstants.MOD_NAMESPACE, "upheaval_stone")
        .setTranslationKey(LdflConstants.MOD_NAMESPACE + ".upheaval_stone");

    private LdflBlocks() {
    }

    private static Block add(Block block, String name) {
        block.setRegistryName(LdflConstants.MOD_NAMESPACE, name);
        block.setTranslationKey(LdflConstants.MOD_NAMESPACE + "." + name);
        block.setCreativeTab(LdflTab.INSTANCE);
        ALL.add(block);
        return block;
    }

    public static List<Block> all() {
        return Collections.unmodifiableList(ALL);
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : ALL) {
            event.getRegistry().register(block);
        }
        event.getRegistry().register(UPHEAVAL_STONE);
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        for (Block block : ALL) {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }
}
