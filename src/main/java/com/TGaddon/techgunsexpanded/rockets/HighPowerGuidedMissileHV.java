package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GuidedMissileProjectileHV;

/**
 * High-Power High-Velocity Guided Missile: the fast guided missile with double
 * explosion. Same three constructors as HighPowerGuidedMissile.
 */
public class HighPowerGuidedMissileHV extends GuidedMissileProjectileHV {

    public HighPowerGuidedMissileHV(World world) {
        super(world);
    }

    public HighPowerGuidedMissileHV(World world, EntityLivingBase shooter,
                                    float a, float b, int c, float d, float e, float f,
                                    float g, float h, boolean blockDamage,
                                    EnumBulletFirePos firePos, float j, double k) {
        super(world, shooter, a, b, c, d, e, f, g, h, blockDamage, firePos, j, k);
        RocketExplosionScaler.scale(this, 2.0f);
    }

    public HighPowerGuidedMissileHV(World world, EntityLivingBase shooter,
                                    float a, float b, int c, float d, float e, float f,
                                    float g, float h, boolean blockDamage,
                                    EnumBulletFirePos firePos, float j, Entity target) {
        super(world, shooter, a, b, c, d, e, f, g, h, blockDamage, firePos, j, target);
        RocketExplosionScaler.scale(this, 2.0f);
    }
}
