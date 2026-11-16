package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.RocketProjectileHV;
import techguns.items.guns.GenericGun;

/**
 * Factory for the High-Power High-Velocity Rocket. Mirrors Techguns'
 * RocketProjectileHV.Factory, which runs the fired stats through the inherited
 * {@code mod} DamageModifier (the HV speed/range boost), but produces our
 * subclass, which then doubles the explosion.
 */
public class HighPowerRocketHVFactory extends RocketProjectileHV.Factory {

    @Override
    public RocketProjectileHV createProjectile(GenericGun gun, World world, EntityLivingBase shooter,
                                               float p4, float p5, int p6, float p7, float p8, float p9,
                                               float p10, float p11, boolean p12, EnumBulletFirePos p13,
                                               float p14, double p15) {
        System.out.println("[TechgunsExpanded][rockets] HighPowerRocketHVFactory.createProjectile CALLED");
        return new HighPowerRocketProjectileHV(world, shooter,
                this.mod.getDamage(p4),
                this.mod.getVelocity(p5),
                this.mod.getTTL(p6),
                p7,
                this.mod.getRange(p8),
                this.mod.getRange(p9),
                this.mod.getDamage(p10),
                p11,
                p12, p13,
                this.mod.getRadius(p14),
                p15);
    }
}
