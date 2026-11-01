package com.micatechnologies.minecraft.ldfl.hero;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

/**
 * Instant-hit shots along the player's aim, for weapons too fast to model as projectiles.
 * The ray stops at the first solid block; entities are hit in order of distance.
 */
public final class HitScan {

    /** Extra margin around entity hitboxes, so fast aiming still connects. */
    private static final double SLOP = 0.3D;

    private HitScan() {
        throw new AssertionError("No instances.");
    }

    /** The result of a hitscan: enemies along the ray, nearest first, and where the ray ended. */
    public static final class Result {
        public final List<EntityLivingBase> enemies;
        public final Vec3d end;

        Result(List<EntityLivingBase> enemies, Vec3d end) {
            this.enemies = enemies;
            this.end = end;
        }
    }

    public static Result enemiesAlongAim(EntityPlayer shooter, double range) {
        Vec3d eyes = shooter.getPositionEyes(1.0F);
        Vec3d look = shooter.getLookVec();
        Vec3d end = eyes.add(look.scale(range));
        RayTraceResult wall = shooter.world.rayTraceBlocks(eyes, end, false, true, false);
        if (wall != null && wall.hitVec != null) {
            end = wall.hitVec;
        }
        final Vec3d from = eyes;
        final Vec3d to = end;
        AxisAlignedBB sweep = new AxisAlignedBB(from.x, from.y, from.z, to.x, to.y, to.z).grow(1.0D);
        List<EntityLivingBase> hits = new ArrayList<>();
        for (Entity e : shooter.world.getEntitiesWithinAABBExcludingEntity(shooter, sweep)) {
            if (Targeting.isEnemy(shooter, e)
                && e.getEntityBoundingBox().grow(SLOP).calculateIntercept(from, to) != null) {
                hits.add((EntityLivingBase) e);
            }
        }
        hits.sort(Comparator.comparingDouble(e -> e.getDistanceSq(from.x, from.y, from.z)));
        return new Result(hits, to);
    }

    /** Draw a particle tracer from the shooter's eyes to {@code end}. */
    public static void tracer(EntityPlayer shooter, Vec3d end, EnumParticleTypes particle) {
        if (!(shooter.world instanceof WorldServer)) {
            return;
        }
        WorldServer world = (WorldServer) shooter.world;
        Vec3d start = shooter.getPositionEyes(1.0F).add(shooter.getLookVec().scale(0.6D));
        Vec3d d = end.subtract(start);
        int steps = (int) Math.ceil(d.length() * 2.0D);
        for (int i = 0; i <= steps; i++) {
            Vec3d at = start.add(d.scale(i / (double) Math.max(1, steps)));
            world.spawnParticle(particle, at.x, at.y, at.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
