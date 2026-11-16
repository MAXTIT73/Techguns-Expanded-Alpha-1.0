package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import techguns.capabilities.TGExtendedPlayer;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GuidedMissileProjectile;
import techguns.items.guns.GenericGun;

/**
 * Factory for the High-Power Guided Missile (Guided Missile Launcher / Grim Reaper).
 *
 * Mirrors Techguns' GuidedMissileProjectile.Factory: it resolves the shooter's
 * lock-on target first and picks the matching constructor — with a target the
 * missile homes in, without one it flies ballistically with the hardcoded 0.01
 * gravity Techguns uses there. Only the produced class differs (ours doubles the
 * explosion).
 *
 * Note: this must stay an IChargedProjectileFactory (inherited from the parent),
 * because ChargedProjectileSelector.getFactoryForType casts to that interface.
 */
public class HighPowerGuidedMissileFactory extends GuidedMissileProjectile.Factory {

    @Override
    public GuidedMissileProjectile createProjectile(GenericGun gun, World world, EntityLivingBase shooter,
                                                    float p4, float p5, int p6, float p7, float p8, float p9,
                                                    float p10, float p11, boolean p12, EnumBulletFirePos p13,
                                                    float p14, double p15) {
        Entity target = getLockOnTarget(gun, shooter);

        if (target != null) {
            return new HighPowerGuidedMissile(world, shooter,
                    p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, target);
        }
        return new HighPowerGuidedMissile(world, shooter,
                p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, (double) 0.01F);
    }

    /** Same lock-on check Techguns does: a target counts only once it is fully locked. */
    static Entity getLockOnTarget(GenericGun gun, EntityLivingBase shooter) {
        if (!(shooter instanceof EntityPlayer)) return null;

        TGExtendedPlayer player = TGExtendedPlayer.get((EntityPlayer) shooter);
        if (player == null || player.lockOnEntity == null) return null;
        if (player.lockOnTicks < gun.getLockOnTicks()) return null;

        return player.lockOnEntity;
    }
}
