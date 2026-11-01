package com.micatechnologies.minecraft.ldfl.client.render;

import com.micatechnologies.minecraft.ldfl.entity.EntityBolt;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Draws a bolt as a short glowing streak along its direction of travel. */
public class RenderBolt extends Render<EntityBolt> {

    private static final double LENGTH = 0.6D;
    private static final double THICK = 0.035D;

    public RenderBolt(RenderManager manager) {
        super(manager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityBolt e, double x, double y, double z, float yaw, float partialTicks) {
        if (e.isOrb()) {
            renderOrb(e, x, y, z, partialTicks);
            return;
        }
        double mx = e.motionX;
        double my = e.motionY;
        double mz = e.motionZ;
        double len = Math.sqrt(mx * mx + my * my + mz * mz);
        if (len < 1.0e-4D) {
            return;
        }
        float boltYaw = (float) Math.toDegrees(Math.atan2(mx, mz));
        float boltPitch = (float) Math.toDegrees(Math.atan2(my, Math.sqrt(mx * mx + mz * mz)));
        float r = 1.0F;
        float g = e.isIgniting() ? 0.45F : 0.85F;
        float b = e.isIgniting() ? 0.15F : 0.4F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(boltYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-boltPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        // Two crossed ribbons from the tail (z = -LENGTH) to the head (z = 0).
        ribbon(buf, THICK, 0.0D, r, g, b);
        ribbon(buf, 0.0D, THICK, r, g, b);
        tess.draw();

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, yaw, partialTicks);
    }

    /** Chime's orb: a small spinning cube inside a pulsing translucent shell. */
    private static void renderOrb(EntityBolt e, double x, double y, double z, float partialTicks) {
        float t = e.ticksExisted + partialTicks;
        double shell = 0.16D + Math.sin(t * 0.8D) * 0.03D;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.1D, z);
        GlStateManager.rotate(t * 20.0F, 0.3F, 1.0F, 0.2F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        Boxes.box(buf, -0.07D, -0.07D, -0.07D, 0.07D, 0.07D, 0.07D, 0xFFF1A8);
        tess.draw();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.35F);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        shellBox(buf, shell);
        tess.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void shellBox(BufferBuilder b, double s) {
        double[][] faces = {
            {-s, s, -s, -s, s, s, s, s, s, s, s, -s}, {-s, -s, -s, s, -s, -s, s, -s, s, -s, -s, s},
            {-s, -s, -s, -s, s, -s, s, s, -s, s, -s, -s}, {-s, -s, s, s, -s, s, s, s, s, -s, s, s},
            {-s, -s, -s, -s, -s, s, -s, s, s, -s, s, -s}, {s, -s, -s, s, s, -s, s, s, s, s, -s, s}};
        for (double[] f : faces) {
            for (int i = 0; i < 12; i += 3) {
                b.pos(f[i], f[i + 1], f[i + 2]).endVertex();
            }
        }
    }

    private static void ribbon(BufferBuilder buf, double hx, double hy, float r, float g, float b) {
        buf.pos(-hx, -hy, -LENGTH).color(r, g, b, 0.0F).endVertex();
        buf.pos(hx, hy, -LENGTH).color(r, g, b, 0.0F).endVertex();
        buf.pos(hx, hy, 0.0D).color(r, g, b, 0.95F).endVertex();
        buf.pos(-hx, -hy, 0.0D).color(r, g, b, 0.95F).endVertex();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBolt entity) {
        return null;
    }
}
