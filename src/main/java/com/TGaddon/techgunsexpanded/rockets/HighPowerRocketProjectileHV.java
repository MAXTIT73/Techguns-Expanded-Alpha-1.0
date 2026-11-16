package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.RocketProjectileHV;

/**
 * High-Power High-Velocity Rocket: the fast rocket with double explosion.
 * Inherits the HV flight behaviour; only the explosion stats are scaled.
 */
public class HighPowerRocketProjectileHV extends RocketProjectileHV {

    public HighPowerRocketProjectileHV(World world) {
        super(world);
    }

    public HighPowerRocketProjectileHV(World world, EntityLivingBase shooter,
                                       float a, float b, int c, float d, float e, float f,
                                       float g, float h, boolean blockDamage,
                                       EnumBulletFirePos firePos, float j, double k) {
        super(world, shooter, a, b, c, d, e, f, g, h, blockDamage, firePos, j, k);
        RocketExplosionScaler.scale(this, 2.0f);
    }
}
