package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import com.TGaddon.techgunsexpanded.init.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/**
 * Server side of the Laser Drill settings window. It has no slots: it is bound
 * to the drill in the player's main hand, and the window's buttons arrive here
 * through vanilla's "button pressed" packet (the one the enchanting table uses),
 * so the mod needs no network channel of its own.
 *
 * The client calls enchantItem too, right when a button is clicked, so the
 * window shows the change at once - while a window is open the server does not
 * resend the held item.
 */
public class ContainerLaserDrill extends Container {

    /** Button ids 1..5 pick the mining size; these toggle the flags. */
    public static final int BUTTON_SILK_TOUCH = 10;
    public static final int BUTTON_AUTO_SMELTING = 11;
    public static final int BUTTON_FLUID_CONVERTER = 12;

    /** Tab in the window: switch to the upgrades window (opened by the server). */
    public static final int BUTTON_OPEN_UPGRADES = 100;

    public ContainerLaserDrill(EntityPlayer player) {
    }

    /** Open only while the drill is still in the main hand. */
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() == ModTools.LASER_DRILL;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        ItemStack drill = player.getHeldItemMainhand();
        if (drill.getItem() != ModTools.LASER_DRILL) {
            return false;
        }
        if (id == BUTTON_OPEN_UPGRADES) {
            if (!player.world.isRemote) {
                player.openGui(TechgunsExpanded.instance, GuiHandler.LASER_DRILL_UPGRADES,
                        player.world, 0, 0, 0);
            }
            return true;
        }
        if (id >= LaserDrillSettings.MIN_SIZE && id <= LaserDrillSettings.MAX_SIZE) {
            if (!LaserDrillSettings.isSizeAvailable(drill, id)) {
                return false; // needs the Area Expansion upgrade
            }
            LaserDrillSettings.setSize(drill, id);
            return true;
        }
        switch (id) {
            case BUTTON_SILK_TOUCH:
                return toggle(drill, LaserDrillSettings.SILK_TOUCH);
            case BUTTON_AUTO_SMELTING:
                return toggle(drill, LaserDrillSettings.AUTO_SMELTING);
            case BUTTON_FLUID_CONVERTER:
                return toggle(drill, LaserDrillSettings.FLUID_CONVERTER);
            default:
                return false;
        }
    }

    /** Flips a setting - refused if its upgrade is not installed. */
    private static boolean toggle(ItemStack drill, String flag) {
        if (!LaserDrillSettings.isAvailable(drill, flag)) {
            return false;
        }
        LaserDrillSettings.toggleFlag(drill, flag);
        return true;
    }
}
