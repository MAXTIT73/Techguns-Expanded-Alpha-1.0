package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.entities.projectiles.ChainsawProjectile;
import techguns.items.guns.ChargedProjectileSelector;
import techguns.items.guns.ammo.AmmoTypes;
import techguns.items.guns.EnumCrosshairStyle;

/**
 * Tools added by this mod.
 *
 * The Laser Drill is set up like Techguns' own Mining Drill (same selector,
 * sounds and timings, same 3x3 mining) so it behaves like a proven drill, with
 * its own mining stats below: it digs 1.5 times faster than the carbon-head
 * drill and nothing is too hard for it.
 *
 * Visuals are a placeholder: the model, texture and render transforms are the
 * Laser Rifle's, registered in ClientProxy.
 */
public final class ModTools {

    public static final String LASER_DRILL_NAME = "laser_drill";

    /**
     * The drill's model skin: the Laser Rifle's texture recoloured (grey parts
     * black, red parts purple) - same model, so the UV layout is the rifle's.
     * File: assets/techguns_expanded/textures/guns/laser_drill.png. It is used
     * both in hand and in the inventory (both draw the 3D model).
     *
     * This is the path WITHOUT the .png: Techguns' ClientProxy.setGunTextures
     * appends "[_camo].png" itself; a path that already ends in .png gives
     * "laser_drill.png.png" and a missing texture.
     */
    public static final ResourceLocation LASER_DRILL_TEX =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/guns/laser_drill");

    /**
     * Harvest level for the drill's tool classes (pickaxe and shovel).
     *
     * Minecraft 1.12 itself stops at 3 (diamond: obsidian), and Techguns' Mining
     * Drill reaches 4 with its carbon head. Mods are free to ask for more for
     * their own blocks, so "the maximum" has no fixed number - this is simply
     * above anything a block asks for, which means no block is ever refused for
     * being too hard.
     */
    public static final int LASER_DRILL_HARVEST_LEVEL = 100;

    /**
     * Dig speed: 1.5 times the carbon-head Mining Drill's, as this mod sets it
     * (CarbonDrillSpeed: 24) - 36. For comparison: Techguns' plain Mining Drill
     * 14, a diamond pickaxe 8.
     */
    public static final float LASER_DRILL_DIG_SPEED = CarbonDrillSpeed.CARBON_DRILL_DIG_SPEED * 1.5F;

    /**
     * Mining radius around the block that is hit: 1 = 3x3, the same as Techguns'
     * Mining Drill. Unlike that drill, sneaking changes nothing (1x1 is a
     * setting of its own). The area actually used comes from the drill's settings (Shift + right
     * click, LaserDrillSettings - 3x3 unless changed); see
     * ItemLaserDrill.getMiningRadius.
     */
    public static final int LASER_DRILL_MINING_RADIUS = 1;

    /**
     * How far the drill digs (block reach while it is held, in survival) and how
     * far its left-click beam reaches, in blocks. Minecraft's own reach is 4.5.
     */
    public static final float LASER_DRILL_REACH = 5.0F;

    /**
     * Ticks between right-click shots. A held right click is repeated by the
     * game every 4 ticks; 8 lets every second one through, i.e. half the
     * fire rate.
     */
    public static final int LASER_DRILL_SHOT_DELAY = 8;

    /**
     * Energy the drill holds ("magazine size"). One Energy Cell refills it
     * completely, whatever the size - a reload always fills the whole clip.
     */
    public static final int LASER_DRILL_CLIP_SIZE = 500;

    /** Energy each Capacity upgrade adds to the magazine (up to 5: 500 -> 1750). */
    public static final int LASER_DRILL_CLIP_PER_UPGRADE = 250;
    public static final int LASER_DRILL_MAX_CAPACITY_UPGRADES = 5;

    public static ItemLaserDrill LASER_DRILL;

    /** Laser Drill upgrade: makes Silk Touch available (LaserDrillUpgrades.SILK_TOUCH). */
    public static final String SILK_TOUCH_UPGRADE_NAME = "laser_drill_upgrade_silk_touch";
    public static Item SILK_TOUCH_UPGRADE;

    /** Laser Drill upgrade: mined drops go straight into the inventory (LaserDrillUpgrades.MAGNET). */
    public static final String MAGNET_UPGRADE_NAME = "laser_drill_upgrade_magnet";
    public static Item MAGNET_UPGRADE;

    /** Laser Drill upgrade: makes Auto Smelting available (LaserDrillUpgrades.AUTO_SMELTING). */
    public static final String AUTO_SMELTING_UPGRADE_NAME = "laser_drill_upgrade_auto_smelting";
    public static Item AUTO_SMELTING_UPGRADE;

    /** Laser Drill upgrade: makes the Fluid Converter available (LaserDrillUpgrades.FLUID_CONVERTER). */
    public static final String FLUID_CONVERTER_UPGRADE_NAME = "laser_drill_upgrade_fluid_converter";
    public static Item FLUID_CONVERTER_UPGRADE;

    /** Laser Drill upgrade: unlocks the 2x2, 4x4 and 5x5 sizes (LaserDrillUpgrades.AREA_EXPANSION). */
    public static final String AREA_EXPANSION_UPGRADE_NAME = "laser_drill_upgrade_area_expansion";
    public static Item AREA_EXPANSION_UPGRADE;

    /** Laser Drill upgrade: one energy per dig instead of per block (LaserDrillUpgrades.ENERGY_EFFICIENCY). */
    public static final String ENERGY_EFFICIENCY_UPGRADE_NAME = "laser_drill_upgrade_energy_efficiency";
    public static Item ENERGY_EFFICIENCY_UPGRADE;

    /** Laser Drill upgrade: +250 magazine energy, up to 5 (LaserDrillUpgrades.CAPACITY). */
    public static final String CAPACITY_UPGRADE_NAME = "laser_drill_upgrade_capacity";
    public static Item CAPACITY_UPGRADE;

    private ModTools() {}

    /** preInit: create the items (registered by RegistryHandler). */
    public static void createItems() {
        ItemLaserDrill drill = new ItemLaserDrill(
                LASER_DRILL_NAME,
                energyCellSelector(),
                false,
                LASER_DRILL_SHOT_DELAY,
                LASER_DRILL_CLIP_SIZE,
                45,     // reload time
                10.0F,  // damage
                TGSounds.LASERGUN_FIRE,     // loop/fire sound - the Laser Rifle's
                TGSounds.LASERGUN_RELOAD,
                2, 0.0F, 1.0F, 1);

        // GenericGun -> GenericItem, whose constructor adds the item to
        // TGItems.ITEMLIST. Techguns iterates that list to register items and to
        // call initModel(), so take it out: we register it ourselves. (Guns are not
        // in that loop on Techguns' side either - TGuns.registerItems lists every
        // gun by hand - so nothing registers this for us.)
        TGItems.ITEMLIST.remove(drill);

        // GenericItem sets the unlocalized name to "techguns.<name>"; keep ours.
        drill.setUnlocalizedName(LASER_DRILL_NAME);
        drill.setCreativeTab(Techguns.tabTechgun);

        drill.setMeleeDmg(12.0F, 2.0F);
        drill.setTool("pickaxe", LASER_DRILL_HARVEST_LEVEL);
        drill.setTool("shovel", LASER_DRILL_HARVEST_LEVEL);
        drill.setDigSpeed(LASER_DRILL_DIG_SPEED);
        drill.setMiningRadius(LASER_DRILL_MINING_RADIUS);
        // Ticks between laser sounds (and muzzle flashes) while left click is
        // held; the beam itself is continuous (ItemLaserDrill).
        drill.setSwingSoundDelay(4);

        drill.setTexture(LASER_DRILL_TEX);
        drill.setRecoiltime(5);
        drill.setShootWithLeftClick(false);
        drill.setFiresoundStart(TGSounds.LASERGUN_FIRE);
        drill.setMaxLoopDelay(10);
        drill.setPenetration(1.0F);
        // Purple muzzle light (the Laser Rifle's is red: 0.9, 0.3, 0.1). Techguns
        // only shows these light pulses when the Albedo mod is installed.
        drill.setMuzzleLight(0.7F, 0.2F, 1.0F);
        drill.setCrossHair(EnumCrosshairStyle.VANILLA);

        LASER_DRILL = drill;

        // Upgrades (put into the drill in its upgrades window).
        SILK_TOUCH_UPGRADE = new Item()
                .setUnlocalizedName(SILK_TOUCH_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, SILK_TOUCH_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(SILK_TOUCH_UPGRADE, LaserDrillUpgrades.SILK_TOUCH, 1);

        MAGNET_UPGRADE = new Item()
                .setUnlocalizedName(MAGNET_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, MAGNET_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(MAGNET_UPGRADE, LaserDrillUpgrades.MAGNET, 1);

        AUTO_SMELTING_UPGRADE = new Item()
                .setUnlocalizedName(AUTO_SMELTING_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, AUTO_SMELTING_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(AUTO_SMELTING_UPGRADE, LaserDrillUpgrades.AUTO_SMELTING, 1);

        FLUID_CONVERTER_UPGRADE = new Item()
                .setUnlocalizedName(FLUID_CONVERTER_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, FLUID_CONVERTER_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(FLUID_CONVERTER_UPGRADE, LaserDrillUpgrades.FLUID_CONVERTER, 1);

        AREA_EXPANSION_UPGRADE = new Item()
                .setUnlocalizedName(AREA_EXPANSION_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, AREA_EXPANSION_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(AREA_EXPANSION_UPGRADE, LaserDrillUpgrades.AREA_EXPANSION, 1);

        ENERGY_EFFICIENCY_UPGRADE = new Item()
                .setUnlocalizedName(ENERGY_EFFICIENCY_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, ENERGY_EFFICIENCY_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(ENERGY_EFFICIENCY_UPGRADE, LaserDrillUpgrades.ENERGY_EFFICIENCY, 1);

        CAPACITY_UPGRADE = new Item()
                .setUnlocalizedName(CAPACITY_UPGRADE_NAME)
                .setRegistryName(TechgunsExpanded.MODID, CAPACITY_UPGRADE_NAME)
                .setCreativeTab(Techguns.tabTechgun)
                .setMaxStackSize(16);
        LaserDrillUpgrades.register(CAPACITY_UPGRADE, LaserDrillUpgrades.CAPACITY,
                LASER_DRILL_MAX_CAPACITY_UPGRADES);
    }

    /**
     * The drill's projectile selector. A gun's ammo type comes from its selector,
     * so this is what makes the drill run on Energy Cells (the Laser Rifle's
     * ammo; an empty cell is given back on reload). Otherwise it is the selector
     * Techguns' Mining Drill uses (TGuns.CHAINSAW_PROJECTILES), whose ammo is
     * Fuel Tanks: same projectile factory, which the drill never fires anyway -
     * ItemLaserDrill.spawnProjectile shoots the laser instead.
     */
    private static ChargedProjectileSelector<ChainsawProjectile> energyCellSelector() {
        return new ChargedProjectileSelector<ChainsawProjectile>(
                AmmoTypes.ENERGY_CELL, new ChainsawProjectile.Factory());
    }

    /**
     * preInit: the harmless beam shown on left click. It is a LaserProjectile
     * subclass, so no renderer has to be registered for it - the Laser Rifle's
     * is used. IDs 0-3 are the rockets (ModRockets.registerEntities).
     *
     * And the drill's shot (right click), so it can be drawn purple. Both are
     * drawn by RenderLaserDrillBeam (ClientProxy).
     */
    public static void registerEntities() {
        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "laser_drill_shot"),
                LaserDrillShot.class, "laser_drill_shot", 5,
                TechgunsExpanded.instance, 128, 1, true);
        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "laser_drill_beam"),
                LaserDrillBeam.class, "laser_drill_beam", 4,
                TechgunsExpanded.instance, 128, 1, true);
    }
}
