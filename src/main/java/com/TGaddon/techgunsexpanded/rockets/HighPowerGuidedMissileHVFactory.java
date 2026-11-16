package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GuidedMissileProjectileHV;
import techguns.items.guns.GenericGun;
import techguns.items.guns.ammo.DamageModifier;

/**
 * Factory for the High-Power High-Velocity Guided Missile. Mirrors Techguns'
 * GuidedMissileProjectileHV.Factory: the fired stats go through the inherited
 * {@code mod} DamageModifier (HV speed / reduced radius+range), then our subclass
 * doubles the explosion.
 */
public class HighPowerGuidedMissileHVFactory extends GuidedMissileProjectileHV.Factory {

    @Override
    public GuidedMissileProjectileHV createProjectile(GenericGun gun, World world, EntityLivingBase shooter,
                                                      float p4, float p5, int p6, float p7, float p8, float p9,
                                                      float p10, float p11, boolean p12, EnumBulletFirePos p13,
                                                      float p14, double p15) {
        Entity target = HighPowerGuidedMissileFactory.getLockOnTarget(gun, shooter);
        DamageModifier m = this.getDamageModifier();

        if (target != null) {
            return new HighPowerGuidedMissileHV(world, shooter,
                    m.getDamage(p4), m.getVelocity(p5), m.getTTL(p6), p7,
                    m.getRange(p8), m.getRange(p9), m.getDamage(p10), p11,
                    p12, p13, m.getRadius(p14), target);
        }
        return new HighPowerGuidedMissileHV(world, shooter,
                m.getDamage(p4), m.getVelocity(p5), m.getTTL(p6), p7,
                m.getRange(p8), m.getRange(p9), m.getDamage(p10), p11,
                p12, p13, m.getRadius(p14), (double) 0.01F);
    }
}
