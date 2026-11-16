package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.packets.PacketSpawnParticle;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.LaserProjectile;
import techguns.items.guns.ChargedProjectileSelector;
import techguns.items.guns.MiningDrill;

/**
 * Laser Drill - stage 1: a working drill with the Laser Rifle's looks and effects.
 *
 * Built on Techguns' MiningDrill (a GenericGunMeleeCharge), so dig speed, mining
 * levels, energy per block and the area mining in onBlockStartBreak come for free.
 *
 * Overridden here:
 *  - the shot: the inherited selector is the Chainsaw's, whose projectile is
 *    invisible, so spawnProjectile is replaced with a real LaserProjectile
 *    (the red beam and its impact effects are the projectile's own);
 *  - the mining sounds: the drill's swing/break sounds become the laser's, which
 *    also makes it audible that the drill is doing the digging, not the player's
 *    hand.
 *
 * The sweep particles at the mined block stay as MiningDrill spawns them.
 */
public class ItemLaserDrill extends MiningDrill {

    // The laser shot's stats. The gun's own damage/speed/range fields are
    // package-private in GenericGun, so they cannot be read from here; these are
    // plain values for the beam and are easy to tune.
    private static final float SHOT_DAMAGE      = 10.0F;
    private static final float SHOT_SPEED       = 2.5F;
    private static final float SHOT_DROP_START  = 24.0F;
    private static final float SHOT_DROP_END    = 32.0F;
    private static final float SHOT_DAMAGE_MIN  = 6.0F;
    private static final float SHOT_PENETRATION = 1.0F;

    public ItemLaserDrill(String name, ChargedProjectileSelector projectiles, boolean silenced,
                          int ammoCount, int clipsize, int reloadtime, float damage,
                          SoundEvent loopSound, SoundEvent reloadSound,
                          int recoil, float zoom, float soundVolume, int muzzleFX) {
        super(name, projectiles, silenced, ammoCount, clipsize, reloadtime, damage,
                loopSound, reloadSound, recoil, zoom, soundVolume, muzzleFX);
    }

    /**
     * The shot. MiningDrill's right click calls shootGunPrimary directly, and for a
     * charge gun that is NOT the charged path: GenericGunCharge.shootGunPrimary
     * delegates straight to GenericGun's, which ends in spawnProjectile and takes the
     * factory from projectile_selector - the Chainsaw's invisible projectile here.
     * So this is the method to replace, not spawnChargedProjectile.
     */
    @Override
    protected void spawnProjectile(World world, EntityLivingBase shooter, ItemStack stack,
                                   float spread, float forwardOffset, float damageMultiplier,
                                   EnumBulletFirePos firePos, Entity target) {
        if (world.isRemote) {
            return;
        }

        LaserProjectile laser = new LaserProjectile(world, shooter,
                SHOT_DAMAGE * damageMultiplier, SHOT_SPEED, this.getScaledTTL(), spread,
                SHOT_DROP_START, SHOT_DROP_END, SHOT_DAMAGE_MIN * damageMultiplier,
                SHOT_PENETRATION, getDoBlockDamage(shooter), firePos);

        if (forwardOffset > 0.0F) {
            laser.shiftForward(forwardOffset / SHOT_SPEED);
        }
        world.spawnEntity(laser);
    }

    /** Ticks between laser sparks while the drill is chewing through a block. */
    private static final int MINING_FX_INTERVAL = 3;

    /** The impact effect LaserProjectile itself uses when the beam hits something. */
    private static final String LASER_IMPACT_FX = "LaserGunImpact";

    /**
     * Replaces the drill's spark burst with the laser's impact effect. MiningDrill
     * sends "PowerhammerImpact" here - that was the mechanical-drill puff visible
     * only point blank. Same delivery (Techguns' particle packet to everyone within
     * 32 blocks), different effect.
     */
    @Override
    protected void spawnSweepParticle(World world, double x, double y, double z,
                                      double motionX, double motionY, double motionZ) {
        TGPackets.network.sendToAllAround(
                new PacketSpawnParticle(LASER_IMPACT_FX, x, y, z),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32.0D));
    }

    /**
     * Continuous feedback while mining. There is no per-tick item hook for block
     * breaking, but the player's arm swings on every mining tick, and that is what
     * Techguns' drills already use for their looping sound - so the laser impact
     * effect is put on the block being mined from here.
     *
     * This runs client-side (that is where the mining swing happens), so the effect
     * is created directly through Techguns' proxy instead of a packet.
     */
    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        boolean result = super.onEntitySwing(entity, stack);

        World world = entity.world;
        if (world.isRemote && entity instanceof EntityPlayer
                && entity.ticksExisted % MINING_FX_INTERVAL == 0) {

            RayTraceResult hit = this.rayTrace(world, (EntityPlayer) entity, false);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos();
                EnumFacing side = hit.sideHit;

                // Half a block out along the face that was hit, so the sparks sit on
                // the surface instead of inside the block.
                double x = pos.getX() + 0.5D + side.getFrontOffsetX() * 0.5D;
                double y = pos.getY() + 0.5D + side.getFrontOffsetY() * 0.5D;
                double z = pos.getZ() + 0.5D + side.getFrontOffsetZ() * 0.5D;

                Techguns.proxy.createFX(LASER_IMPACT_FX, world, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
        return result;
    }

    /**
     * Visible confirmation that the drill - not the player's fist - broke the block:
     * a laser impact burst at every block it destroys.
     */
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state,
                                    BlockPos pos, EntityLivingBase miner) {
        boolean result = super.onBlockDestroyed(stack, world, state, pos, miner);
        if (!world.isRemote) {
            this.spawnSweepParticle(world,
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    0.0D, 0.0D, 0.0D);
        }
        return result;
    }

    @Override
    protected SoundEvent getSwingSound() {
        return TGSounds.LASERGUN_FIRE;
    }

    @Override
    protected SoundEvent getBlockBreakSound() {
        return TGSounds.LASERGUN_FIRE;
    }
}
