package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;

/**
 * The Laser Drill's mining beam (left click): the Laser Rifle's beam that does
 * nothing to what it touches, and reaches only as far as the drill digs.
 *
 * It is the drill's shot (LaserDrillShot, a LaserProjectile) built by the same
 * constructor, so the start point, direction, the trace that decides where the
 * beam ends, the purple impact effect and the spawn data sent to clients are
 * the shot's own. It is drawn by RenderLaserDrillBeam, like the shot.
 *
 * The differences:
 *  - onHit: the beam still stops at the first block or entity in its way, but
 *    deals no damage;
 *  - a fixed length (the drill's reach) instead of the shot's 100 blocks;
 *  - a short lifetime: while left click is held a new beam is fired every
 *    couple of ticks, and short overlapping beams read as one continuous beam
 *    that follows the crosshair instead of a trail of shots.
 */
public class LaserDrillBeam extends LaserDrillShot {

    /**
     * Ticks a Laser Rifle beam stays on screen (LaserProjectile.Factory
     * hardcodes 7); the drill's shot uses it. The renderer fades a beam in and
     * out over its lifetime, so a beam of 1 tick is never visible at all.
     */
    public static final int LIFETIME = 7;

    /**
     * How far a projectile's trace reaches per point of "speed".
     * GenericProjectile.initProjectile leaves its motion vector 1.5 times the
     * speed long (shoot() with velocity 1.5, then * speed), and the beam is
     * traced along that vector.
     */
    private static final float TRACE_PER_SPEED = 1.5F;

    /** Client-side spawn (FML needs this constructor), as for every Techguns projectile. */
    public LaserDrillBeam(World world) {
        super(world);
    }

    /**
     * Server side: a harmless beam fired by the shooter, from the same point and
     * in the same direction as a real shot with the same fire position.
     *
     * @param range    how far the beam reaches, in blocks
     * @param lifetime ticks it stays on screen
     */
    public LaserDrillBeam(World world, EntityLivingBase shooter, float range, int lifetime,
                          EnumBulletFirePos firePos) {
        super(world, shooter, 0.0F, range / TRACE_PER_SPEED, lifetime,
                0.0F, 0.0F, 0.0F, 0.0F, 0.0F, false, firePos);

        // Nothing hit: trace() then draws the beam "speed" long, which is only
        // 2/3 of the distance it traced. Draw it the full range instead.
        if (this.distance == this.speed) {
            this.distance = range;
        }
    }

    /**
     * Called by trace() from the superclass constructor for whatever the beam
     * ends on. GenericProjectile.onHit damages an entity here; this beam only
     * keeps the block part - hitBlock, which is what sends the impact effect
     * (LaserDrillShot.doImpactEffects). Uses no fields of this class: it runs
     * before they are set.
     */
    @Override
    protected void onHit(RayTraceResult hit) {
        if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            this.hitBlock(hit);
        }
    }
}
