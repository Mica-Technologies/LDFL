package com.micatechnologies.minecraft.ldfl.client.render;

import net.minecraft.client.renderer.BufferBuilder;

/**
 * Untextured, face-shaded boxes for LDFL's procedural models. Each face is darkened by a fixed
 * factor (top brightest, bottom darkest), which reads as simple directional lighting. Feed a
 * {@code GL_QUADS} buffer in the {@code POSITION_COLOR} format.
 */
final class Boxes {

    private static final float TOP = 1.0F;
    private static final float NS = 0.8F;
    private static final float EW = 0.65F;
    private static final float BOTTOM = 0.5F;

    private Boxes() {
    }

    /** A box from (x0,y0,z0) to (x1,y1,z1) in colour {@code rgb} (0xRRGGBB). */
    static void box(BufferBuilder b, double x0, double y0, double z0, double x1, double y1, double z1,
                    int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float bl = (rgb & 0xFF) / 255.0F;
        // Top
        face(b, r, g, bl, TOP, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
        // Bottom
        face(b, r, g, bl, BOTTOM, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
        // North (-z) and south (+z)
        face(b, r, g, bl, NS, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0);
        face(b, r, g, bl, NS, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
        // West (-x) and east (+x)
        face(b, r, g, bl, EW, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
        face(b, r, g, bl, EW, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1);
    }

    private static void face(BufferBuilder b, float r, float g, float bl, float shade,
                             double ax, double ay, double az, double bx, double by, double bz,
                             double cx, double cy, double cz, double dx, double dy, double dz) {
        float sr = r * shade;
        float sg = g * shade;
        float sb = bl * shade;
        b.pos(ax, ay, az).color(sr, sg, sb, 1.0F).endVertex();
        b.pos(bx, by, bz).color(sr, sg, sb, 1.0F).endVertex();
        b.pos(cx, cy, cz).color(sr, sg, sb, 1.0F).endVertex();
        b.pos(dx, dy, dz).color(sr, sg, sb, 1.0F).endVertex();
    }
}
