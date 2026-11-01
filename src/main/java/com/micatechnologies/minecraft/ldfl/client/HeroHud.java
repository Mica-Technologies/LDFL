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
 * The hero HUD, bottom right: hero and role, each ability with its key and cooldown, and the
 * ultimate meter. Drawn only while the player is a hero.
 */
public final class HeroHud {

    private static final int WIDTH = 132;
    private static final int ROW = 11;
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
        int x = res.getScaledWidth() - WIDTH - 4;
        int y = res.getScaledHeight() - 6 * ROW - 6;

        Gui.drawRect(x - 3, y - 3, x + WIDTH, y + 6 * ROW, 0x80000000);

        String ns = LdflConstants.MOD_NAMESPACE;
        font.drawStringWithShadow(I18n.format(ns + ".hero." + hero.id()), x, y, 0xFFFFFF);
        String role = I18n.format(ns + ".role." + hero.role().id());
        font.drawStringWithShadow(role, x + WIDTH - 4 - font.getStringWidth(role), y, roleColor(hero));
        y += ROW + 2;

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
