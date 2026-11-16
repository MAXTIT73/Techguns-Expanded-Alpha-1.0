package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.RocketProjectile;

/**
 * High-Power Rocket: a normal Techguns rocket with double explosion (damage and
 * radius). All behaviour (flight, trail, explosion) is inherited from
 * RocketProjectile; only the explosion stats are scaled in the constructor.
 *
 * The (World) constructor is required for client-side entity spawning.
 */
public class HighPowerRocketProjectile extends RocketProjectile {

    public HighPowerRocketProjectile(World world) {
        super(world);
    }

    public HighPowerRocketProjectile(World world, EntityLivingBase shooter,
                                     float a, float b, int c, float d, float e, float f,
                                     float g, float h, boolean blockDamage,
                                     EnumBulletFirePos firePos, float j, double k) {
        super(world, shooter, a, b, c, d, e, f, g, h, blockDamage, firePos, j, k);
        RocketExplosionScaler.scale(this, 2.0f);
    }
}
