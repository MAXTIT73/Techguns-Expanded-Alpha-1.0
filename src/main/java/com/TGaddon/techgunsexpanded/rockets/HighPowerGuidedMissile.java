package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GuidedMissileProjectile;

/**
 * High-Power Guided Missile: Techguns' guided missile with double explosion.
 *
 * Must extend GuidedMissileProjectile, because GuidedMissileLauncher.spawnProjectile
 * casts the created entity to that class to assign the lock-on target.
 *
 * Three constructors are needed, mirroring the parent:
 *  - (World)                 : client-side spawning
 *  - (..., float, Entity)    : fired with a lock-on target
 *  - (..., float, double)    : fired without a target (unguided flight)
 */
public class HighPowerGuidedMissile extends GuidedMissileProjectile {

    public HighPowerGuidedMissile(World world) {
        super(world);
    }

    public HighPowerGuidedMissile(World world, EntityLivingBase shooter,
                                  float a, float b, int c, float d, float e, float f,
                                  float g, float h, boolean blockDamage,
                                  EnumBulletFirePos firePos, float j, double k) {
        super(world, shooter, a, b, c, d, e, f, g, h, blockDamage, firePos, j, k);
        RocketExplosionScaler.scale(this, 2.0f);
    }

    public HighPowerGuidedMissile(World world, EntityLivingBase shooter,
                                  float a, float b, int c, float d, float e, float f,
                                  float g, float h, boolean blockDamage,
                                  EnumBulletFirePos firePos, float j, Entity target) {
        super(world, shooter, a, b, c, d, e, f, g, h, blockDamage, firePos, j, target);
        RocketExplosionScaler.scale(this, 2.0f);
    }
}
