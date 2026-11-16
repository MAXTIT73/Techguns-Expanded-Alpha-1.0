package com.TGaddon.techgunsexpanded.tools;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayerMP;
import techguns.util.TextUtil;
import java.util.List;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.entities.projectiles.EnumBulletFirePos;
import techguns.entities.projectiles.LaserProjectile;
import techguns.items.guns.ChargedProjectileSelector;
import techguns.items.guns.MiningDrill;
import techguns.packets.GunFiredMessage;
import techguns.packets.PacketSpawnParticle;
import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import com.TGaddon.techgunsexpanded.init.GuiHandler;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Laser Drill - stage 1: a working drill with the Laser Rifle's looks and effects.
 *
 * Built on Techguns' MiningDrill (a GenericGunMeleeCharge), so dig speed, mining
 * levels, energy per block and the area mining in onBlockStartBreak come for free.
 *
 * Both mouse buttons use the Laser Rifle's red beam, impact sparks, muzzle flash
 * and muzzle light:
 *  - right click (the shot, for damage): separate shots, like the rifle. The
 *    inherited selector's projectile is invisible, so spawnProjectile is
 *    replaced with a real LaserProjectile fired with the rifle's beam values;
 *  - left click (mining): one continuous, harmless beam for as long as the
 *    button is held, as far as the drill reaches (LaserDrillBeam). The digging
 *    itself is MiningDrill's; the drill's reach is raised to match the beam.
 *
 * Only one of the two works at a time: whichever button fired first keeps the
 * laser until it is released, the other one does nothing meanwhile (see
 * "One laser at a time" below).
 *
 * The drill's swing/break sounds are the laser's as well.
 */
public class ItemLaserDrill extends MiningDrill {

    // The laser shot's damage stats. The gun's own damage fields are
    // package-private in GenericGun, so they cannot be read from here; these are
    // plain values and are easy to tune.
    private static final float SHOT_DAMAGE      = 10.0F;
    private static final float SHOT_DROP_START  = 24.0F;
    private static final float SHOT_DROP_END    = 32.0F;
    private static final float SHOT_DAMAGE_MIN  = 6.0F;
    private static final float SHOT_PENETRATION = 1.0F;

    /**
     * "Bullet speed" of the beam - for a beam this is NOT a velocity. The beam is
     * traced once, in the projectile's constructor, along its motion vector, so
     * the speed is how far the beam reaches. The Laser Rifle uses
     * setBulletSpeed(100); with an ordinary bullet speed like 2.5 the beam is cut
     * off a few blocks from the muzzle and hits nothing beyond that.
     */
    private static final float BEAM_SPEED = 100.0F;

    /** The impact effect LaserProjectile itself uses when the beam hits a block. */
    private static final String LASER_IMPACT_FX = "LaserGunImpact";

    /**
     * Recoil and muzzle flash time of a left-click pulse, in ms. A shot gets the
     * gun's recoiltime / muzzleFlashtime (both 5 ticks for this drill, see
     * ModTools); those fields are package-private, so the same 5 ticks are
     * written out here.
     */
    private static final int BEAM_FLASH_MS = 250;

    /**
     * The continuous left-click beam: a new short beam every BEAM_INTERVAL
     * ticks, each on screen for BEAM_LIFETIME ticks, so two or three overlap at
     * any moment and they read as one beam. Kept short so the beam follows the
     * crosshair instead of leaving a fan of old beams behind when turning.
     */
    private static final int BEAM_INTERVAL = 2;
    private static final int BEAM_LIFETIME = 4;

    /** Server side: world time of each player's last left-click beam. */
    private static final Map<EntityPlayer, Long> LAST_BEAM = new WeakHashMap<EntityPlayer, Long>();

    /** Fixed id of the drill's reach bonus, so it is replaced, not stacked. */
    private static final UUID REACH_MODIFIER_ID = UUID.fromString("6c1f4e52-3a7d-4b8e-9f21-5d0a7c3e8b14");

    /**
     * Block reach without any modifier, in survival: the REACH_DISTANCE
     * attribute's base 5.0 minus the 0.5 the game takes off outside creative.
     */
    private static final double VANILLA_SURVIVAL_REACH = 4.5D;

    public ItemLaserDrill(String name, ChargedProjectileSelector projectiles, boolean silenced,
                          int ammoCount, int clipsize, int reloadtime, float damage,
                          SoundEvent loopSound, SoundEvent reloadSound,
                          int recoil, float zoom, float soundVolume, int muzzleFX) {
        super(name, projectiles, silenced, ammoCount, clipsize, reloadtime, damage,
                loopSound, reloadSound, recoil, zoom, soundVolume, muzzleFX);
    }

    // ------------------------------------------------------------------
    // One laser at a time
    // ------------------------------------------------------------------

    /**
     * How long a button counts as "still held" after its last use, in ticks.
     * Left click swings every tick while held, so 3 ticks bridge any gap; a
     * held right click is repeated by the game every 4 ticks, so 6.
     */
    private static final long DRILLING_HOLD_TICKS = 3;
    private static final long SHOOTING_HOLD_TICKS = 6;

    /**
     * World time of each player's last use of each button. Kept on both sides
     * (each side decides the same way, so the client shows exactly what the
     * server does); in single player both threads use the maps, hence
     * synchronized. Weak keys: nothing is kept for players who are gone.
     */
    private static final Map<EntityPlayer, Long> LAST_DRILLING =
            Collections.synchronizedMap(new WeakHashMap<EntityPlayer, Long>());
    private static final Map<EntityPlayer, Long> LAST_SHOOTING =
            Collections.synchronizedMap(new WeakHashMap<EntityPlayer, Long>());

    /** True while the player is drilling (left click held, and it got the laser). */
    public static boolean isDrilling(EntityPlayer player) {
        return isRecent(LAST_DRILLING, player, DRILLING_HOLD_TICKS);
    }

    /** True while the player is shooting (right click held, and it got the laser). */
    public static boolean isShooting(EntityPlayer player) {
        return isRecent(LAST_SHOOTING, player, SHOOTING_HOLD_TICKS);
    }

    private static boolean isRecent(Map<EntityPlayer, Long> map, EntityPlayer player, long ticks) {
        Long last = map.get(player);
        long now = player.world.getTotalWorldTime();
        return last != null && now >= last && now - last <= ticks;
    }

    private static void mark(Map<EntityPlayer, Long> map, EntityPlayer player) {
        map.put(player, player.world.getTotalWorldTime());
    }

    // ------------------------------------------------------------------
    // Right click: the shot
    // ------------------------------------------------------------------

    /**
     * Right click (MiningDrill shoots from here). Refused while the player is
     * drilling - also on the client, so no shot sound or flash is shown either.
     * Only a right click that is let through counts as shooting, so the
     * button that started first keeps the laser.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        // Shift + right click: the settings window instead of a shot.
        if (player.isSneaking() && hand == EnumHand.MAIN_HAND) {
            if (!world.isRemote) {
                player.openGui(TechgunsExpanded.instance, GuiHandler.LASER_DRILL, world, 0, 0, 0);
            }
            // FAIL, not SUCCESS: SUCCESS makes the game swing the arm, and a swing
            // fires the drill's mining beam. FAIL also keeps the off hand idle.
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }
        if (isDrilling(player)) {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
        }
        mark(LAST_SHOOTING, player);
        return super.onItemRightClick(world, player, hand);
    }

    /**
     * The shot. MiningDrill's right click calls shootGunPrimary directly, and for a
     * charge gun that is NOT the charged path: GenericGunCharge.shootGunPrimary
     * delegates straight to GenericGun's, which ends in spawnProjectile and takes the
     * factory from projectile_selector - the Chainsaw's invisible projectile here.
     * So this is the method to replace, not spawnChargedProjectile.
     *
     * The projectile is created the way LaserProjectile.Factory creates the Laser
     * Rifle's: the rifle's beam speed, and a fixed lifetime of 7 ticks instead of
     * the gun's getScaledTTL() - that is a bullet's flight time (1 tick for this
     * drill), and a beam that lives 1 tick is removed before it is ever drawn.
     */
    @Override
    protected void spawnProjectile(World world, EntityLivingBase shooter, ItemStack stack,
                                   float spread, float forwardOffset, float damageMultiplier,
                                   EnumBulletFirePos firePos, Entity target) {
        if (world.isRemote) {
            return;
        }

        LaserProjectile laser = new LaserDrillShot(world, shooter,
                SHOT_DAMAGE * damageMultiplier, BEAM_SPEED, LaserDrillBeam.LIFETIME, spread,
                SHOT_DROP_START, SHOT_DROP_END, SHOT_DAMAGE_MIN * damageMultiplier,
                SHOT_PENETRATION, getDoBlockDamage(shooter), firePos);

        if (forwardOffset > 0.0F) {
            laser.shiftForward(forwardOffset / BEAM_SPEED);
        }
        this.spawnBeam(world, shooter, laser);
    }

    /**
     * Puts a beam into the world, with the red light pulse at the muzzle the way
     * GenericGun.spawnProjectile does it (colour and size are set on the gun in
     * ModTools). Shared by both mouse buttons so the two cannot drift apart.
     */
    private void spawnBeam(World world, EntityLivingBase shooter, LaserProjectile beam) {
        if (this.muzzelight) {
            Vec3d look = shooter.getLookVec();
            Techguns.proxy.createLightPulse(
                    beam.posX + look.x, beam.posY + look.y, beam.posZ + look.z,
                    this.light_lifetime, this.light_radius_start, this.light_radius_end,
                    this.light_r, this.light_g, this.light_b);
        }
        world.spawnEntity(beam);
    }

    // ------------------------------------------------------------------
    // Left click: the continuous mining beam
    // ------------------------------------------------------------------

    /**
     * Left click has no "fire" hook, but the player's arm swings for it - on
     * every tick while a block is being mined, and (ClientProxy) every tick
     * while the button is held on anything else. Swings reach the server
     * through the swing packet, and that is what Techguns' drills already use
     * for their looping sound.
     *
     * Two things are paced off those swings, both from the server so everybody
     * nearby sees them:
     *  - the beam, every BEAM_INTERVAL ticks (isBeamDue);
     *  - the laser sound (played by super) with the muzzle flash, on Techguns'
     *    swing sound timer (isPulseDue).
     */
    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (isShooting(player)) {
                // Right click has the laser: no beam, no sound, no swing
                // animation (true cancels the vanilla one). Digging is stopped
                // in LaserDrillEvents.
                return true;
            }
            mark(LAST_DRILLING, player);
        }

        // Asked before super: super restarts the swing sound timer.
        boolean pulse = this.isPulseDue(entity, stack);
        boolean beam = this.isBeamDue(entity, stack);

        boolean result = super.onEntitySwing(entity, stack);

        if (pulse) {
            this.sendMuzzleFlash((EntityPlayer) entity);
        }
        if (beam) {
            this.fireMiningBeam((EntityPlayer) entity);
        }
        return result;
    }

    /** Server side, a player, energy left, and no container being opened. */
    private boolean canFireMiningBeam(EntityLivingBase entity, ItemStack stack) {
        if (entity.world.isRemote || !(entity instanceof EntityPlayer)
                || this.getCurrentAmmo(stack) <= 0) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) entity;
        return player.openContainer == null
                || player.openContainer.getClass() == ContainerPlayer.class;
    }

    /**
     * True on the swings GenericGunMeleeCharge.onEntitySwing plays the swing
     * sound on: Techguns' own swingSoundDelay timer has run out. That timer is
     * restarted with the drill's setSwingSoundDelay value (ModTools).
     */
    private boolean isPulseDue(EntityLivingBase entity, ItemStack stack) {
        if (!this.canFireMiningBeam(entity, stack)) {
            return false;
        }
        TGExtendedPlayer props = TGExtendedPlayer.get((EntityPlayer) entity);
        return props != null && props.swingSoundDelay <= 0;
    }

    /** True once every BEAM_INTERVAL ticks per player; records the beam when it is. */
    private boolean isBeamDue(EntityLivingBase entity, ItemStack stack) {
        if (!this.canFireMiningBeam(entity, stack)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer) entity;
        long now = player.world.getTotalWorldTime();
        Long last = LAST_BEAM.get(player);
        if (last != null && now >= last && now - last < BEAM_INTERVAL) {
            return false;
        }
        LAST_BEAM.put(player, now);
        return true;
    }

    /**
     * The "gun fired" message GenericGun.shootGun sends for a shot: it starts
     * the muzzle flash (and recoil) of this player's gun on every client nearby.
     * No energy is used.
     */
    private void sendMuzzleFlash(EntityPlayer player) {
        TGPackets.network.sendToAllAround(
                new GunFiredMessage(player, BEAM_FLASH_MS, BEAM_FLASH_MS, 0, false, false),
                TGPackets.targetPointAroundEnt(player, 100.0D));
    }

    /** One short piece of the continuous beam, as far as the drill reaches. */
    private void fireMiningBeam(EntityPlayer player) {
        World world = player.world;

        // Left click always uses the main hand; same side rule as shootGunPrimary.
        EnumBulletFirePos firePos = player.getPrimaryHand() == EnumHandSide.RIGHT
                ? EnumBulletFirePos.RIGHT : EnumBulletFirePos.LEFT;

        this.spawnBeam(world, player, new LaserDrillBeam(world, player,
                ModTools.LASER_DRILL_REACH, BEAM_LIFETIME, firePos));

        // Fluid converter: freeze/solidify whatever the beam points at (the trace
        // stops at fluids here), so lava and water are converted on sight.
        if (LaserDrillSettings.isOn(player.getHeldItemMainhand(), LaserDrillSettings.FLUID_CONVERTER)) {
            RayTraceResult hit = this.rayTrace(world, player, true);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                LaserDrillMining.convertFluidsAround(world, player, hit.getBlockPos(), 1);
            }
        }
    }

    /**
     * Every tick in a player's inventory: settings whose upgrade is missing are
     * switched off (server side; the change reaches the client like any NBT).
     */
    @Override
    public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot,
                         boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote) {
            LaserDrillSettings.enforceUpgrades(stack);
        }
    }

    /** Radius matching the chosen area, for anything in Techguns that asks. */
    @Override
    public int getMiningRadius(ItemStack stack) {
        return LaserDrillSettings.getMiningRadius(stack);
    }

    /**
     * Area mining and fluid converter: the drill's own (LaserDrillMining), with
     * the area from the settings window, 1x1 .. 5x5. Replaces Techguns' version
     * (super is not called), which only does odd sizes. false: the game then
     * breaks the hit block itself, as usual.
     */
    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (!player.world.isRemote && player instanceof EntityPlayerMP) {
            LaserDrillMining.onBlockStartBreak(this, stack, pos, (EntityPlayerMP) player);
        }
        return false;
    }

    /** The block the player is aiming at, within reach (Item.rayTrace is protected). */
    RayTraceResult rayTraceForMining(World world, EntityPlayer player) {
        return this.rayTrace(world, player, false);
    }

    // ------------------------------------------------------------------
    // Magazine size (Capacity upgrade)
    // ------------------------------------------------------------------

    /**
     * This drill's magazine: 500, +250 per installed Capacity upgrade (up to
     * 5, so 1750). Techguns keeps one magazine size per gun type (the clipsize
     * field); every place it uses it for a single drill is overridden below to
     * use this instead. A reload still takes one Energy Cell and fills the
     * whole magazine.
     */
    public int getClipsize(ItemStack stack) {
        return ModTools.LASER_DRILL_CLIP_SIZE + ModTools.LASER_DRILL_CLIP_PER_UPGRADE
                * Math.min(LaserDrillUpgrades.getCount(stack, LaserDrillUpgrades.CAPACITY),
                ModTools.LASER_DRILL_MAX_CAPACITY_UPGRADES);
    }

    /** A reload (empty or forced - Techguns empties the magazine first) fills it. */
    @Override
    public void reloadAmmo(ItemStack stack) {
        this.reloadAmmo(stack, this.getClipsize(stack) - this.getCurrentAmmo(stack));
    }

    /** No reload while full (the R key). */
    @Override
    public boolean isFullyLoaded(ItemStack stack) {
        return this.getCurrentAmmo(stack) >= this.getClipsize(stack);
    }

    /** The bar under the item and Techguns' ammo HUD. */
    @Override
    public double getPercentAmmoLeft(ItemStack stack) {
        return Math.min(1.0D, (double) this.getCurrentAmmo(stack) / (double) this.getClipsize(stack));
    }

    /**
     * Techguns' ammo HUD (TGGuiEvents.drawGunAmmoCount) shows
     * getAmmoLeftCountTooltip(stack) + "/" + getClipsizeTooltip(), and the
     * second one is not told which drill. Both are called one right after the
     * other in that one expression (client thread), so the first remembers the
     * drill for the second.
     */
    private ItemStack hudStack = ItemStack.EMPTY;

    @Override
    public int getAmmoLeftCountTooltip(ItemStack stack) {
        this.hudStack = stack;
        return super.getAmmoLeftCountTooltip(stack);
    }

    @Override
    public int getClipsizeTooltip() {
        return this.hudStack.getItem() == this ? this.getClipsize(this.hudStack) : super.getClipsizeTooltip();
    }

    /** Techguns' tooltip lists the type's magazine size; show this drill's. */
    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        String prefix = TextUtil.trans("techguns.gun.tooltip.clipsize") + ": ";
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(prefix)) {
                list.set(i, prefix + this.getClipsize(stack));
            }
        }
    }

    /**
     * Techguns' tooltip shows the area from the radius (odd only) - show the real
     * one - and promises silk touch while sneaking, which this drill does not do
     * (LaserDrillEvents undoes it; silk touch is a setting here).
     */
    @Override
    protected void addMiningTooltip(ItemStack stack, World world, List<String> list, ITooltipFlag flag,
                                    boolean longTooltip) {
        super.addMiningTooltip(stack, world, list, flag, longTooltip);
        list.remove(TextUtil.trans("techguns.tooltip.sneaksilktouch"));
        String prefix = TextUtil.trans("techguns.tooltip.miningradius");
        int size = LaserDrillSettings.getSize(stack);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(prefix)) {
                list.set(i, prefix + ": " + size + "x" + size);
            }
        }
    }

    /**
     * The drill's reach: while it is in the main hand, the player's block reach
     * (the REACH_DISTANCE attribute, which also limits the server's dig check)
     * is raised to ModTools.LASER_DRILL_REACH. Melee range is not affected.
     */
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot,
                                                                     ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(EntityPlayer.REACH_DISTANCE.getName(),
                    new AttributeModifier(REACH_MODIFIER_ID, "Laser drill reach",
                            ModTools.LASER_DRILL_REACH - VANILLA_SURVIVAL_REACH, 0));
        }
        return modifiers;
    }

    // ------------------------------------------------------------------
    // Melee sweep effect and sounds
    // ------------------------------------------------------------------

    /**
     * Replaces the drill's melee sweep burst with the laser's impact effect.
     * MiningDrill sends "PowerhammerImpact" here - the mechanical-drill puff.
     * Same delivery: Techguns' particle packet to everyone within 32 blocks.
     */
    @Override
    protected void spawnSweepParticle(World world, double x, double y, double z,
                                      double motionX, double motionY, double motionZ) {
        TGPackets.network.sendToAllAround(
                new PacketSpawnParticle(LASER_IMPACT_FX, x, y, z),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32.0D));
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
