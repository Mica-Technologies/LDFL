package com.micatechnologies.minecraft.ldfl.client.render;

import com.micatechnologies.minecraft.ldfl.entity.EntityWardBell;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Draws Chime's Ward Bell: a small brass bell on a yoke, bobbing and swaying as it rings. */
public class RenderWardBell extends Render<EntityWardBell> {

    public RenderWardBell(RenderManager manager) {
        super(manager);
        shadowSize = 0.25F;
    }

    @Override
    public void doRender(EntityWardBell e, double x, double y, double z, float yaw, float partialTicks) {
        float t = e.ticksExisted + partialTicks;
        double bob = 0.35D + Math.sin(t * 0.12D) * 0.06D;
        float sway = (float) Math.sin(t * 0.3D) * 8.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + bob, z);
        GlStateManager.rotate(t * 1.5F, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        // Yoke
        Boxes.box(buf, -0.3D, 0.62D, -0.04D, 0.3D, 0.68D, 0.04D, 0x6B4A2E);
        tess.draw();

        GlStateManager.translate(0.0D, 0.62D, 0.0D);
        GlStateManager.rotate(sway, 1.0F, 0.0F, 0.0F);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        Boxes.box(buf, -0.06D, -0.1D, -0.06D, 0.06D, 0.0D, 0.06D, 0xC8873A);
        Boxes.box(buf, -0.12D, -0.25D, -0.12D, 0.12D, -0.1D, 0.12D, 0xE8B04A);
        Boxes.box(buf, -0.17D, -0.42D, -0.17D, 0.17D, -0.25D, 0.17D, 0xE8B04A);
        Boxes.box(buf, -0.2D, -0.47D, -0.2D, 0.2D, -0.42D, 0.2D, 0xC8873A);
        Boxes.box(buf, -0.04D, -0.55D, -0.04D, 0.04D, -0.47D, 0.04D, 0x8C5A22);
        tess.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWardBell entity) {
        return null;
    }
}
