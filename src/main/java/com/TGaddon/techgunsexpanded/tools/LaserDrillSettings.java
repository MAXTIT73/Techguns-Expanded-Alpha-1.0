package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * The Laser Drill's settings, kept in the drill's own NBT (one sub-tag), so
 * they travel with the item and the server syncs them to the client like any
 * other item data. Changed in the settings window (GuiLaserDrill /
 * ContainerLaserDrill).
 *
 *  - size: the mining area, 1..5 (1x1 .. 5x5) - LaserDrillMining; 2x2, 4x4
 *    and 5x5 need the Area Expansion upgrade;
 *  - silk touch: needs the Silk Touch upgrade (isAvailable). The drill
 *    carries a real Silk Touch enchantment while it is on, so every block
 *    (also the area's) drops the way silk touch makes it;
 *  - auto smelting: needs the Auto Smelting upgrade; drops are smelted
 *    (LaserDrillEvents);
 *  - fluid converter: needs the Fluid Converter upgrade; lava -> obsidian,
 *    water -> ice (LaserDrillMining).
 */
public final class LaserDrillSettings {

    private static final String TAG = "tgeLaserDrill";
    private static final String SIZE = "size";

    public static final String SILK_TOUCH = "silkTouch";
    public static final String AUTO_SMELTING = "autoSmelting";
    public static final String FLUID_CONVERTER = "fluidConverter";

    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 5;

    /** 3x3, what the drill has always mined. */
    public static final int DEFAULT_SIZE = 3;

    private LaserDrillSettings() {
    }

    public static boolean isSizeAvailable(int size) {
        return size >= MIN_SIZE && size <= MAX_SIZE;
    }

    /**
     * Whether this drill can use a size: 1x1 and 3x3 always, 2x2, 4x4 and 5x5
     * only with the Area Expansion upgrade.
     */
    public static boolean isSizeAvailable(ItemStack stack, int size) {
        return isSizeAvailable(size) && (!needsAreaUpgrade(size)
                || LaserDrillUpgrades.has(stack, LaserDrillUpgrades.AREA_EXPANSION));
    }

    /** The sizes the Area Expansion upgrade unlocks. */
    public static boolean needsAreaUpgrade(int size) {
        return size != 1 && size != DEFAULT_SIZE;
    }

    /** The size the drill mines; a size it cannot use (upgrade missing) counts as 3x3. */
    public static int getSize(ItemStack stack) {
        NBTTagCompound tag = read(stack);
        int size = tag != null && tag.hasKey(SIZE) ? tag.getInteger(SIZE) : DEFAULT_SIZE;
        return isSizeAvailable(stack, size) ? size : DEFAULT_SIZE;
    }

    public static void setSize(ItemStack stack, int size) {
        if (isSizeAvailable(stack, size)) {
            write(stack).setInteger(SIZE, size);
        }
    }

    /** Radius as Techguns counts it (1x1 -> 0, 3x3 -> 1, 5x5 -> 2; 2x2 -> 0, 4x4 -> 1). */
    public static int getMiningRadius(ItemStack stack) {
        return (getSize(stack) - 1) / 2;
    }

    public static boolean getFlag(ItemStack stack, String flag) {
        NBTTagCompound tag = read(stack);
        return tag != null && tag.getBoolean(flag);
    }

    /**
     * Whether a flag can be used at all: its upgrade (requiredUpgrade) is
     * installed, or it needs none.
     */
    public static boolean isAvailable(ItemStack stack, String flag) {
        String upgrade = requiredUpgrade(flag);
        return upgrade == null || LaserDrillUpgrades.has(stack, upgrade);
    }

    /**
     * The upgrade a flag needs (LaserDrillUpgrades id), or null if it needs none.
     * A new upgrade-gated setting is one more line here.
     */
    public static String requiredUpgrade(String flag) {
        if (SILK_TOUCH.equals(flag)) {
            return LaserDrillUpgrades.SILK_TOUCH;
        }
        if (AUTO_SMELTING.equals(flag)) {
            return LaserDrillUpgrades.AUTO_SMELTING;
        }
        if (FLUID_CONVERTER.equals(flag)) {
            return LaserDrillUpgrades.FLUID_CONVERTER;
        }
        return null;
    }

    /** A flag that is switched on AND available - what the drill's functions check. */
    public static boolean isOn(ItemStack stack, String flag) {
        return getFlag(stack, flag) && isAvailable(stack, flag);
    }

    /**
     * Turns off flags whose upgrade is missing (e.g. switched on before upgrades
     * existed), Silk Touch enchantment included. Called while the drill is held.
     */
    public static void enforceUpgrades(ItemStack stack) {
        if (!isAvailable(stack, SILK_TOUCH)
                && (getFlag(stack, SILK_TOUCH) || hasSilkTouchEnchantment(stack))) {
            write(stack).setBoolean(SILK_TOUCH, false);
            applySilkTouch(stack, false);
        }
        if (!isAvailable(stack, AUTO_SMELTING) && getFlag(stack, AUTO_SMELTING)) {
            write(stack).setBoolean(AUTO_SMELTING, false);
        }
        if (!isAvailable(stack, FLUID_CONVERTER) && getFlag(stack, FLUID_CONVERTER)) {
            write(stack).setBoolean(FLUID_CONVERTER, false);
        }
        NBTTagCompound tag = read(stack);
        if (tag != null && tag.hasKey(SIZE) && !isSizeAvailable(stack, tag.getInteger(SIZE))) {
            tag.setInteger(SIZE, DEFAULT_SIZE);
        }
    }

    private static boolean hasSilkTouchEnchantment(ItemStack stack) {
        int silkId = Enchantment.getEnchantmentID(Enchantments.SILK_TOUCH);
        NBTTagList list = stack.getEnchantmentTagList();
        for (int i = 0; i < list.tagCount(); i++) {
            if (list.getCompoundTagAt(i).getShort("id") == silkId) {
                return true;
            }
        }
        return false;
    }

    /** Flips a flag - only if it is available (see isAvailable). */
    public static void toggleFlag(ItemStack stack, String flag) {
        if (!isAvailable(stack, flag)) {
            return;
        }
        write(stack).setBoolean(flag, !getFlag(stack, flag));
        if (SILK_TOUCH.equals(flag)) {
            applySilkTouch(stack, getFlag(stack, SILK_TOUCH));
        }
    }

    /**
     * Adds or removes the Silk Touch enchantment to match the setting. A real
     * enchantment, because that is what blocks check when they decide their
     * drops (Block.harvestBlock), for the drill's area blocks as well. The drill
     * shows the enchantment glint while silk touch is on.
     */
    private static void applySilkTouch(ItemStack stack, boolean on) {
        int silkId = Enchantment.getEnchantmentID(Enchantments.SILK_TOUCH);
        NBTTagList list = stack.getEnchantmentTagList();
        boolean present = false;
        for (int i = list.tagCount() - 1; i >= 0; i--) {
            if (list.getCompoundTagAt(i).getShort("id") == silkId) {
                if (on) {
                    present = true;
                } else {
                    list.removeTag(i);
                }
            }
        }
        if (on && !present) {
            stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
        } else if (!on && list.hasNoTags() && stack.getTagCompound() != null) {
            stack.getTagCompound().removeTag("ench"); // no glint without enchantments
        }
    }

    private static NBTTagCompound read(ItemStack stack) {
        NBTTagCompound root = stack.getTagCompound();
        return root != null && root.hasKey(TAG) ? root.getCompoundTag(TAG) : null;
    }

    private static NBTTagCompound write(ItemStack stack) {
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        if (!root.hasKey(TAG)) {
            root.setTag(TAG, new NBTTagCompound());
        }
        return root.getCompoundTag(TAG);
    }
}
