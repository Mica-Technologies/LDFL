package com.micatechnologies.minecraft.ldfl.client.render;

import com.micatechnologies.minecraft.ldfl.entity.EntityBloomFlask;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Draws the Bloom Flask as a small tumbling bottle with a glowing green body. */
public class RenderBloomFlask extends Render<EntityBloomFlask> {

    public RenderBloomFlask(RenderManager manager) {
        super(manager);
        shadowSize = 0.1F;
    }

    @Override
    public void doRender(EntityBloomFlask e, double x, double y, double z, float yaw, float partialTicks) {
        float spin = (e.ticksExisted + partialTicks) * 25.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.1D, z);
        GlStateManager.rotate(spin, 0.4F, 1.0F, 0.2F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        Boxes.box(buf, -0.11D, -0.12D, -0.11D, 0.11D, 0.08D, 0.11D, 0x7CFF6A);
        Boxes.box(buf, -0.05D, 0.08D, -0.05D, 0.05D, 0.17D, 0.05D, 0xE8F4E0);
        Boxes.box(buf, -0.06D, 0.17D, -0.06D, 0.06D, 0.21D, 0.06D, 0x8C5A22);
        tess.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBloomFlask entity) {
        return null;
    }
}
