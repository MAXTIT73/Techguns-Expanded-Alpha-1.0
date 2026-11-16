package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.util.ResourceLocation;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.TGuns;
import techguns.Techguns;
import techguns.items.guns.EnumCrosshairStyle;

/**
 * Tools added by this mod.
 *
 * The Laser Drill's stats are copied from Techguns' own Mining Drill (same
 * selector, sounds and timings) so stage 1 behaves like a proven drill; only the
 * dig speed is set to the obsidian/carbon head level and the base mining radius is
 * 0, because the area is going to come from the drill's own mode setting.
 *
 * Visuals are a placeholder: the model, texture and render transforms are the
 * Laser Rifle's, registered in ClientProxy.
 */
public final class ModTools {

    public static final String LASER_DRILL_NAME = "laser_drill";

    /**
     * Placeholder model skin - the Laser Rifle's.
     *
     * This is the path WITHOUT the .png: Techguns' ClientProxy.setGunTextures builds
     * the real location as "techguns:<path>[_camo].png". Passing a ResourceLocation
     * that already ends in .png produces "lasergun.png.png" and a missing texture.
     */
    public static final String LASER_DRILL_TEX = "textures/guns/lasergun";

    public static ItemLaserDrill LASER_DRILL;

    private ModTools() {}

    /** preInit: create the items (registered by RegistryHandler). */
    public static void createItems() {
        ItemLaserDrill drill = new ItemLaserDrill(
                LASER_DRILL_NAME,
                TGuns.CHAINSAW_PROJECTILES,   // same charged selector the Mining Drill uses
                false,
                3,      // ammo used per shot
                300,    // clip size (energy)
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
        drill.setTool("pickaxe", 3);
        drill.setTool("shovel", 3);
        drill.setDigSpeed(14.0F);
        drill.setMiningRadius(0);       // 1x1 for now; modes arrive in stage 2
        drill.setSwingSoundDelay(6);   // laser pulse while drilling

        drill.setTexture(LASER_DRILL_TEX);
        drill.setRecoiltime(5);
        drill.setShootWithLeftClick(false);
        drill.setFiresoundStart(TGSounds.LASERGUN_FIRE);
        drill.setMaxLoopDelay(10);
        drill.setPenetration(1.0F);
        drill.setNoMuzzleLight();
        drill.setCrossHair(EnumCrosshairStyle.VANILLA);

        LASER_DRILL = drill;
    }
}
