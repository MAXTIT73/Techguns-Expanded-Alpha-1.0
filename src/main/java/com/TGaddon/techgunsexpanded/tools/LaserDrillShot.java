package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import techguns.TGPackets;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.LaserProjectile;
import techguns.packets.PacketSpawnParticle;

/**
 * The Laser Drill's shot (right click): exactly Techguns' LaserProjectile - same
 * trace, damage, energy damage type - only purple.
 *
 * It is a class of its own so it can look different from the Laser Rifle's
 * beam: it is drawn by RenderLaserDrillBeam (purple beam textures, registered
 * in ClientProxy) and its impact sends the purple "LaserDrillImpact" effect
 * (assets/techguns_expanded/particles/laser_drill_fx.txt) instead of the
 * rifle's red "LaserGunImpact". LaserDrillBeam (left click) extends this.
 */
public class LaserDrillShot extends LaserProjectile {

    /** Purple copy of Techguns' "LaserGunImpact" (sparks + flare). */
    public static final String IMPACT_FX = "LaserDrillImpact";

    /** Client-side spawn (FML needs this constructor). */
    public LaserDrillShot(World world) {
        super(world);
    }

    /** Same constructor as LaserProjectile's: fires and traces immediately. */
    public LaserDrillShot(World world, EntityLivingBase shooter, float damage, float speed, int ttl,
                          float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
                          float penetration, boolean blockDamage, EnumBulletFirePos firePos) {
        super(world, shooter, damage, speed, ttl, spread, dmgDropStart, dmgDropEnd, dmgMin,
                penetration, blockDamage, firePos);
    }

    /**
     * LaserProjectile.doImpactEffects with the purple effect: the same particle
     * packet to everyone within 35 blocks. Runs from trace() in the superclass
     * constructor, so it must not use fields of this class.
     */
    @Override
    protected void doImpactEffects(Material material, RayTraceResult hit, SoundType sound) {
        if (!this.world.isRemote) {
            Vec3d at = hit.hitVec;
            TGPackets.network.sendToAllAround(new PacketSpawnParticle(IMPACT_FX, at.x, at.y, at.z),
                    TGPackets.targetPointAroundEnt(this, 35.0D));
        }
    }
}
