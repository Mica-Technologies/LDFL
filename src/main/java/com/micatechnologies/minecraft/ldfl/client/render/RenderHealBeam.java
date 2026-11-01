package com.micatechnologies.minecraft.ldfl.client.render;

import com.micatechnologies.minecraft.ldfl.entity.EntityHealBeam;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * Draws Solace's heal beam as two crossed, gently pulsing ribbons from the healer's staff hand to
 * the middle of the target. Positions are interpolated per frame, relative to the beam carrier
 * (which sits on the healer), so the link stays attached to both ends while they move.
 */
public class RenderHealBeam extends Render<EntityHealBeam> {

    private static final double WIDTH = 0.07D;

    public RenderHealBeam(RenderManager manager) {
        super(manager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(EntityHealBeam e, double x, double y, double z, float yaw, float partialTicks) {
        Entity owner = e.getOwner();
        Entity target = e.getTarget();
        if (owner == null || target == null) {
            return;
        }
        Vec3d carrier = lerp(e, partialTicks);
        Vec3d ownerPos = lerp(owner, partialTicks);
        Vec3d targetPos = lerp(target, partialTicks);
        // Start at the healer's chest, a little forward, roughly where the staff is held.
        Vec3d look = owner.getLook(partialTicks);
        Vec3d a = ownerPos.add(look.x * 0.5D, owner.getEyeHeight() - 0.35D, look.z * 0.5D)
            .subtract(carrier).add(x, y, z);
        Vec3d b = targetPos.add(0.0D, target.height * 0.55D, 0.0D).subtract(carrier).add(x, y, z);

        Vec3d d = b.subtract(a);
        if (d.lengthSquared() < 1.0e-4D) {
            return;
        }
        Vec3d up = Math.abs(d.normalize().y) > 0.95D ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);
        Vec3d p1 = d.crossProduct(up).normalize().scale(WIDTH);
        Vec3d p2 = d.crossProduct(p1).normalize().scale(WIDTH);
        float pulse = 0.55F + 0.25F * (float) Math.sin((e.ticksExisted + partialTicks) * 0.4D);

        GlStateManager.pushMatrix();
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
        ribbon(buf, a, b, p1, 0.6F, 1.0F, 0.42F, pulse);
        ribbon(buf, a, b, p2, 0.6F, 1.0F, 0.42F, pulse);
        // A thin bright core.
        ribbon(buf, a, b, p1.scale(0.3D), 1.0F, 1.0F, 0.85F, pulse);
        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static Vec3d lerp(Entity e, float t) {
        return new Vec3d(e.prevPosX + (e.posX - e.prevPosX) * t, e.prevPosY + (e.posY - e.prevPosY) * t,
            e.prevPosZ + (e.posZ - e.prevPosZ) * t);
    }

    private static void ribbon(BufferBuilder buf, Vec3d a, Vec3d b, Vec3d side, float r, float g,
                               float bl, float alpha) {
        buf.pos(a.x + side.x, a.y + side.y, a.z + side.z).color(r, g, bl, alpha * 0.6F).endVertex();
        buf.pos(a.x - side.x, a.y - side.y, a.z - side.z).color(r, g, bl, alpha * 0.6F).endVertex();
        buf.pos(b.x - side.x, b.y - side.y, b.z - side.z).color(r, g, bl, alpha).endVertex();
        buf.pos(b.x + side.x, b.y + side.y, b.z + side.z).color(r, g, bl, alpha).endVertex();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHealBeam entity) {
        return null;
    }
}
