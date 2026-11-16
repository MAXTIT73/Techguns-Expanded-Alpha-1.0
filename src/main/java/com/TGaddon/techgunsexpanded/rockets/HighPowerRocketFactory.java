package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.RocketProjectile;
import techguns.items.guns.GenericGun;

/**
 * Factory for the High-Power Rocket. Mirrors Techguns' RocketProjectile.Factory
 * (pure pass-through of the fired stats) but produces our subclass, which
 * doubles the explosion in its constructor.
 */
public class HighPowerRocketFactory extends RocketProjectile.Factory {

    @Override
    public RocketProjectile createProjectile(GenericGun gun, World world, EntityLivingBase shooter,
                                             float p4, float p5, int p6, float p7, float p8, float p9,
                                             float p10, float p11, boolean p12, EnumBulletFirePos p13,
                                             float p14, double p15) {
        System.out.println("[TechgunsExpanded][rockets] HighPowerRocketFactory.createProjectile CALLED");
        return new HighPowerRocketProjectile(world, shooter,
                p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15);
    }
}
