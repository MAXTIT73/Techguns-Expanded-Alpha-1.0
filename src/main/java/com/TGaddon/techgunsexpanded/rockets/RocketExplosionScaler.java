package com.TGaddon.techgunsexpanded.rockets;

import techguns.entities.projectiles.GenericProjectile;

import java.lang.reflect.Field;

/**
 * Doubles a rocket projectile's explosion by scaling the damage/radius fields.
 *
 * Those fields (damage, damageMin, damageDropStart, damageDropEnd, radius) are
 * package-private on Techguns' GenericProjectile, so we reach them by reflection.
 * The inherited RocketProjectile.explodeRocket() then uses the scaled values, so
 * the blast deals 2x damage over 2x the damage-falloff radius automatically.
 */
public final class RocketExplosionScaler {

    private static final String[] SCALED_FIELDS = {
            "damage", "damageMin", "damageDropStart", "damageDropEnd", "radius"
    };

    private RocketExplosionScaler() {}

    public static void scale(GenericProjectile projectile, float factor) {
        for (String name : SCALED_FIELDS) {
            try {
                Field f = GenericProjectile.class.getDeclaredField(name);
                f.setAccessible(true);
                float before = f.getFloat(projectile);
                f.setFloat(projectile, before * factor);
                System.out.println("[TechgunsExpanded][rockets] scaled " + name + ": " + before + " -> " + f.getFloat(projectile));
            } catch (Exception e) {
                System.out.println("[TechgunsExpanded][rockets] Could not scale rocket field '" + name + "': " + e);
            }
        }
    }
}
