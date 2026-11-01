package com.micatechnologies.minecraft.ldfl.client.render;

import com.micatechnologies.minecraft.ldfl.entity.EntitySentry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Draws Forgewright's sentry from shaded boxes: a squat base and post, and a brass head with a
 * barrel that turns and tilts toward its target. The lens glows cyan when active, red during
 * Slag Surge, and goes dark when powered down.
 */
public class RenderSentry extends Render<EntitySentry> {

    private static final int BASE = 0x3A3F47;
    private static final int POST = 0x5A616B;
    private static final int HEAD = 0xC8873A;
    private static final int HEAD_TRIM = 0x8C5A22;
    private static final int BARREL = 0x2B2E33;
    private static final int LENS_ON = 0x5FE3F0;
    private static final int LENS_SURGE = 0xFF5A2A;
    private static final int LENS_OFF = 0x1E2A2C;

    public RenderSentry(RenderManager manager) {
        super(manager);
        shadowSize = 0.4F;
    }

    @Override
    public void doRender(EntitySentry e, double x, double y, double z, float yaw, float partialTicks) {
        float headYaw = e.prevRotationYawHead + (e.rotationYawHead - e.prevRotationYawHead) * partialTicks;
        float pitch = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * partialTicks;
        int light = e.getBrightnessForRender();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
            light & 0xFFFF, light >> 16);
        if (e.hurtTime > 0) {
            GlStateManager.color(1.0F, 0.6F, 0.6F, 1.0F);
        }

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        Boxes.box(buf, -0.4D, 0.0D, -0.4D, 0.4D, 0.14D, 0.4D, BASE);
        Boxes.box(buf, -0.25D, 0.14D, -0.25D, 0.25D, 0.22D, 0.25D, POST);
        Boxes.box(buf, -0.09D, 0.22D, -0.09D, 0.09D, 0.62D, 0.09D, POST);
        tess.draw();

        GlStateManager.translate(0.0D, 0.8D, 0.0D);
        GlStateManager.rotate(-headYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        int lens = !e.isActive() ? LENS_OFF : e.isSurging() ? LENS_SURGE : LENS_ON;
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        Boxes.box(buf, -0.27D, -0.18D, -0.3D, 0.27D, 0.17D, 0.24D, HEAD);
        Boxes.box(buf, -0.29D, -0.05D, -0.32D, 0.29D, 0.03D, 0.2D, HEAD_TRIM);
        Boxes.box(buf, -0.06D, -0.06D, 0.24D, 0.06D, 0.06D, 0.72D, BARREL);
        Boxes.box(buf, -0.13D, 0.06D, 0.24D, 0.13D, 0.13D, 0.27D, lens);
        tess.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySentry entity) {
        return null;
    }
}
