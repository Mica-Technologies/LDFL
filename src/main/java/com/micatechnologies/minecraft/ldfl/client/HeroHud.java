package com.micatechnologies.minecraft.ldfl.client;

import com.micatechnologies.minecraft.ldfl.LdflConstants;
import com.micatechnologies.minecraft.ldfl.hero.core.AbilitySlot;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroSpec;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The hero HUD, top right: hero and role, each ability with its key and cooldown, and the
 * ultimate meter. Drawn only while the player is a hero.
 */
public final class HeroHud {

    private static final int WIDTH = 132;
    private static final int ROW = 11;
    /** Clears two rows of vanilla status-effect icons (24 px each, plus margins). */
    private static final int TOP = 56;
    private static final int READY = 0x55FF55;
    private static final int COOLING = 0xAAAAAA;
    private static final int ULT_READY_A = 0xFFD700;
    private static final int ULT_READY_B = 0xFFFFFF;

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        HeroState state = ClientHero.STATE;
        HeroSpec hero = state.hero();
        if (hero == null || mc.gameSettings.hideGUI || mc.player == null) {
            return;
        }
        ScaledResolution res = event.getResolution();
        FontRenderer font = mc.fontRenderer;
        float resource = ClientHero.resource;
        int rows = resource >= 0.0F ? 7 : 6;
        int x = res.getScaledWidth() - WIDTH - 4;
        // Top right, below the vanilla status-effect icons. The bottom right is where the held
        // item is drawn in first person, and the HUD used to cover the hero's own weapon.
        int y = TOP;

        Gui.drawRect(x - 3, y - 3, x + WIDTH, y + rows * ROW, 0x80000000);

        String ns = LdflConstants.MOD_NAMESPACE;
        font.drawStringWithShadow(I18n.format(ns + ".hero." + hero.id()), x, y, 0xFFFFFF);
        String role = I18n.format(ns + ".role." + hero.role().id());
        font.drawStringWithShadow(role, x + WIDTH - 4 - font.getStringWidth(role), y, roleColor(hero));
        y += ROW + 2;

        if (resource >= 0.0F) {
            String label = I18n.format(ns + ".resource." + hero.id());
            font.drawStringWithShadow(label, x, y, 0xCFE8FF);
            int labelW = font.getStringWidth(label) + 4;
            int barX = x + labelW;
            int barW = WIDTH - 4 - labelW;
            int color = resource > 0.5F ? 0xFF5FD8FF : resource > 0.25F ? 0xFFFFC23F : 0xFFFF5A3F;
            Gui.drawRect(barX, y + 2, barX + barW, y + 7, 0xFF333333);
            Gui.drawRect(barX, y + 2, barX + (int) (barW * resource), y + 7, color);
            y += ROW;
        }

        for (AbilitySlot slot : new AbilitySlot[] {AbilitySlot.ABILITY_1, AbilitySlot.ABILITY_2}) {
            int cd = state.cooldown(slot);
            String key = "[" + LdflKeys.forSlot(slot).getDisplayName() + "] ";
            String name = I18n.format(ns + ".ability." + hero.id() + "." + hero.ability(slot).id());
            font.drawStringWithShadow(key + name, x, y, cd > 0 ? COOLING : 0xFFFFFF);
            String status = cd > 0 ? String.format("%.1fs", cd / 20.0F) : I18n.format(ns + ".hud.ready");
            font.drawStringWithShadow(status, x + WIDTH - 4 - font.getStringWidth(status), y,
                cd > 0 ? COOLING : READY);
            y += ROW;
        }

        String ultKey = "[" + LdflKeys.ULTIMATE.getDisplayName() + "] ";
        String ultName = I18n.format(ns + ".ability." + hero.id() + "."
            + hero.ability(AbilitySlot.ULTIMATE).id());
        boolean ready = state.ultReady();
        int ultColor = ready ? ((mc.player.ticksExisted / 8) % 2 == 0 ? ULT_READY_A : ULT_READY_B) : 0xFFFFFF;
        font.drawStringWithShadow(ultKey + ultName, x, y, ultColor);
        String pct = ready ? I18n.format(ns + ".hud.ready") : (int) (state.ultCharge() * 100.0F) + "%";
        font.drawStringWithShadow(pct, x + WIDTH - 4 - font.getStringWidth(pct), y, ultColor);
        y += ROW;

        int barW = WIDTH - 4;
        Gui.drawRect(x, y, x + barW, y + 4, 0xFF333333);
        Gui.drawRect(x, y, x + (int) (barW * state.ultCharge()), y + 4, ready ? 0xFFFFD700 : 0xFF4FA3FF);
    }

    private static int roleColor(HeroSpec hero) {
        switch (hero.role()) {
            case TANK:
                return 0x6FB7FF;
            case DAMAGE:
                return 0xFF6F6F;
            default:
                return 0x7FFF9F;
        }
    }
}
