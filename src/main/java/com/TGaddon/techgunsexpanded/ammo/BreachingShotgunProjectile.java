package com.TGaddon.techgunsexpanded.ammo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import techguns.api.damagesystem.DamageType;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.GenericProjectile;
import techguns.items.guns.GenericGun;
import techguns.items.guns.IProjectileFactory;
import techguns.items.guns.ammo.DamageModifier;

/**
 * A pellet of a Breaching Shotgun Shell: Techguns' normal shotgun pellet,
 * 1.5 times worse (Factory), that breaches the door it hits (DoorBreaching).
 */
public class BreachingShotgunProjectile extends GenericProjectile {

    /** How much worse than a normal shell: damage, damage range and spread. */
    public static final float PENALTY = 1.5F;

    /** Client-side spawn (FML needs this constructor). */
    public BreachingShotgunProjectile(World world) {
        super(world);
    }

    public BreachingShotgunProjectile(World world, EntityLivingBase shooter, float damage, float speed, int ttl,
                                      float spread, float dmgDropStart, float dmgDropEnd, float dmgMin,
                                      float penetration, boolean blockDamage, EnumBulletFirePos firePos) {
        super(world, shooter, damage, speed, ttl, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration,
                blockDamage, firePos);
    }

    /** The usual impact (sound, particles), then the door. */
    @Override
    protected void hitBlock(RayTraceResult hit) {
        super.hitBlock(hit);
        if (!this.world.isRemote) {
            DoorBreaching.onDoorShot(this.world, hit.getBlockPos(), this.shooter);
        }
    }

    /**
     * The gun's stats for a normal shell, made 1.5 times worse: damage (and
     * minimum damage), range (where damage starts and stops dropping, and how
     * long a pellet flies) divided by 1.5, spread multiplied by 1.5. Damage and
     * range go through Techguns' DamageModifier, so the shotgun's tooltip shows
     * them in red with "-33%" while it is loaded with these shells.
     */
    public static class Factory implements IProjectileFactory<GenericProjectile> {

        private final DamageModifier mod = new DamageModifier()
                .setDmg(1.0F / PENALTY, 0.0F)
                .setRange(1.0F / PENALTY, 0.0F);

        @Override
        public DamageModifier getDamageModifier() {
            return this.mod;
        }

        @Override
        public GenericProjectile createProjectile(GenericGun gun, World world, EntityLivingBase shooter,
                                                  float damage, float speed, int ttl, float spread,
                                                  float dmgDropStart, float dmgDropEnd, float dmgMin,
                                                  float penetration, boolean blockDamage,
                                                  EnumBulletFirePos firePos, float radius, double gravity) {
            return new BreachingShotgunProjectile(world, shooter, this.mod.getDamage(damage), speed,
                    this.mod.getTTL(ttl), spread * PENALTY, this.mod.getRange(dmgDropStart),
                    this.mod.getRange(dmgDropEnd), this.mod.getDamage(dmgMin), penetration, blockDamage, firePos);
        }

        @Override
        public DamageType getDamageType() {
            return DamageType.PROJECTILE;
        }
    }
}
