package com.TGaddon.techgunsexpanded.combat;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import techguns.entities.npcs.AttackHelicopter;
import techguns.entities.projectiles.GenericProjectile;

/**
 * Headshots for Techguns projectiles: a bullet or beam that hits a mob in the
 * head deals extra damage - usually x1.3, sometimes x2.
 *
 * Techguns has no headshots of its own (GenericProjectile.onHit still carries a
 * commented-out "Check for Headshot" from an older version). This adds them
 * from the outside, for every Techguns gun at once, without touching Techguns:
 *
 *  - LivingHurtEvent is fired for all damage before it is applied; the event
 *    tells us the projectile (the damage source's immediate source).
 *  - The projectile has not moved yet when it deals damage: it hits whatever
 *    is between its position and position + motion (GenericProjectile.onUpdate
 *    and the beams' trace()). Repeating that segment against the target's
 *    box, grown by 0.3 exactly as GenericProjectile.findEntityOnPath grows it,
 *    gives the same hit point Techguns found.
 *  - The hit is a headshot when that point is no lower than HEAD_DEPTH below
 *    the target's eyes.
 *  - Never for hasNoHead targets: bosses whose eyes are level with the whole
 *    body, where every hit would count as a headshot.
 *
 * Priority HIGHEST: for its own mobs Techguns cancels this event at HIGH
 * priority and applies the damage itself (DamageSystem.livingHurt) with the
 * event's amount, so the amount has to be changed before that.
 */
@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class HeadshotHandler {

    /** Chance of the big multiplier; otherwise the normal one is used. */
    public static final float CRIT_CHANCE = 0.2F;
    public static final float HEADSHOT_MULTIPLIER = 1.3F;
    public static final float CRIT_HEADSHOT_MULTIPLIER = 2.0F;

    /**
     * How far below the eyes the head reaches, in blocks. With 0.3 the head is
     * the top ~0.5 block of a zombie, skeleton or player. Mobs are judged by
     * their eye height, so for spiders, slimes or ghasts (whose head is not on
     * top) this is only an approximation.
     */
    public static final double HEAD_DEPTH = 0.3D;

    /** Growth Techguns applies to a target's box when it looks for hits. */
    private static final double HIT_BOX_GROWTH = 0.3D;

    private HeadshotHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        DamageSource source = event.getSource();
        if (target.world.isRemote || event.getAmount() <= 0.0F || !isProjectileHit(source)
                || hasNoHead(target)) {
            return;
        }

        GenericProjectile projectile = (GenericProjectile) source.getImmediateSource();
        Vec3d hit = findHitPoint(projectile, target);
        if (hit == null || hit.y < target.posY + target.getEyeHeight() - HEAD_DEPTH) {
            return;
        }

        float multiplier = target.getRNG().nextFloat() < CRIT_CHANCE
                ? CRIT_HEADSHOT_MULTIPLIER : HEADSHOT_MULTIPLIER;
        event.setAmount(event.getAmount() * multiplier);
    }

    /**
     * Targets that never take headshots: the Ender Dragon (vanilla) and Techguns'
     * Attack Helicopter. Their eye height sits at the level of the whole body, so
     * the head check would turn almost any hit into a headshot - on (mini-)bosses.
     * Subclasses (e.g. modded dragons built on EntityDragon) are excluded too.
     */
    private static boolean hasNoHead(EntityLivingBase target) {
        return target instanceof EntityDragon || target instanceof AttackHelicopter;
    }

    /**
     * Direct hits of Techguns projectiles only. Explosions are excluded (a
     * rocket's blast also names the rocket as its source, but has no head to
     * hit), and so is the tiny "knockback" hit Techguns deals just before the
     * real one.
     */
    private static boolean isProjectileHit(DamageSource source) {
        if (!(source.getImmediateSource() instanceof GenericProjectile)) {
            return false;
        }
        String type = source.getDamageType();
        return !source.isExplosion()
                && !"tg_explosion".equals(type)
                && !"tg_knockback".equals(type);
    }

    /** Where the projectile's path of this tick enters the target's (grown) box. */
    private static Vec3d findHitPoint(Entity projectile, EntityLivingBase target) {
        Vec3d start = new Vec3d(projectile.posX, projectile.posY, projectile.posZ);
        Vec3d end = start.addVector(projectile.motionX, projectile.motionY, projectile.motionZ);
        AxisAlignedBB box = target.getEntityBoundingBox().grow(HIT_BOX_GROWTH);

        RayTraceResult result = box.calculateIntercept(start, end);
        return result != null ? result.hitVec : null;
    }
}
