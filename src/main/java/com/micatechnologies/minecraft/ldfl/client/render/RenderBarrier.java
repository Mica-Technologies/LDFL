package com.micatechnologies.minecraft.ldfl.client.render;

import com.micatechnologies.minecraft.ldfl.entity.EntityBarrier;
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
 * Draws the barrier as a glowing translucent panel: a faint fill, a bright frame, horizontal ribs
 * and a scan band sweeping upward. Untextured and procedural. Its colour runs from cyan at full
 * health through amber to red as it weakens.
 */
public class RenderBarrier extends Render<EntityBarrier> {

    private static final double W = EntityBarrier.HALF_WIDTH;
    private static final double H = EntityBarrier.HEIGHT;
    private static final double FRAME = 0.07D;

    public RenderBarrier(RenderManager manager) {
        super(manager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityBarrier e, double x, double y, double z, float yaw, float partialTicks) {
        float health = e.getHealthFraction();
        float r;
        float g;
        float b;
        if (health > 0.5F) {
            float t = (health - 0.5F) * 2.0F;
            r = lerp(1.0F, 0.35F, t);
            g = lerp(0.75F, 0.85F, t);
            b = lerp(0.25F, 1.0F, t);
        } else {
            float t = health * 2.0F;
            r = 1.0F;
            g = lerp(0.25F, 0.75F, t);
            b = lerp(0.2F, 0.25F, t);
        }
        float interpYaw = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * partialTicks;
        float time = e.ticksExisted + partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-interpYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Fill, slightly stronger at the bottom.
        quadGradient(buf, -W, 0.0D, W, H, r, g, b, 0.26F, 0.12F);
        // Ribs.
        for (double ry = 0.7D; ry < H; ry += 0.7D) {
            quad(buf, -W, ry - 0.015D, W, ry + 0.015D, r, g, b, 0.18F);
        }
        // Vertical seams.
        for (double sx = -W + 1.0D; sx < W; sx += 1.0D) {
            quad(buf, sx - 0.012D, 0.0D, sx + 0.012D, H, r, g, b, 0.10F);
        }
        // Scan band sweeping upward.
        double scan = (time * 0.06D) % H;
        quad(buf, -W, scan, W, Math.min(H, scan + 0.18D), r, g, b, 0.22F);
        // Frame.
        quad(buf, -W, 0.0D, W, FRAME, r, g, b, 0.85F);
        quad(buf, -W, H - FRAME, W, H, r, g, b, 0.85F);
        quad(buf, -W, 0.0D, -W + FRAME, H, r, g, b, 0.85F);
        quad(buf, W - FRAME, 0.0D, W, H, r, g, b, 0.85F);

        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        super.doRender(e, x, y, z, yaw, partialTicks);
    }

    private static void quad(BufferBuilder buf, double x0, double y0, double x1, double y1,
                             float r, float g, float b, float a) {
        quadGradient(buf, x0, y0, x1, y1, r, g, b, a, a);
    }

    private static void quadGradient(BufferBuilder buf, double x0, double y0, double x1, double y1,
                                     float r, float g, float b, float aBottom, float aTop) {
        buf.pos(x0, y0, 0.0D).color(r, g, b, aBottom).endVertex();
        buf.pos(x1, y0, 0.0D).color(r, g, b, aBottom).endVertex();
        buf.pos(x1, y1, 0.0D).color(r, g, b, aTop).endVertex();
        buf.pos(x0, y1, 0.0D).color(r, g, b, aTop).endVertex();
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBarrier entity) {
        return null;
    }
}
