package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Laser Drill's installed upgrades ("the set"), kept in the drill's NBT as
 * upgrade id -> count.
 *
 * An upgrade item put into the upgrades window's slot disappears at once and
 * is added to the set (ContainerLaserDrillUpgrades) - up to the upgrade's
 * maximum; what is over the maximum stays in the slot. Anything that is not an
 * upgrade stays in the slot and goes back to the player when the window closes.
 *
 * Upgrades (registered in ModTools):
 *  - SILK_TOUCH (max 1): makes the Silk Touch setting available;
 *  - MAGNET (max 1): mined drops go straight into the inventory (LaserDrillEvents);
 *  - AUTO_SMELTING (max 1): makes the Auto Smelting setting available;
 *  - FLUID_CONVERTER (max 1): makes the Fluid Converter setting available;
 *  - AREA_EXPANSION (max 1): unlocks the 2x2, 4x4 and 5x5 mining sizes;
 *  - ENERGY_EFFICIENCY (max 1): one energy per dig, however many blocks it
 *    breaks, instead of one per block (LaserDrillMining);
 *  - CAPACITY (max 5): +250 energy per upgrade in the drill's magazine
 *    (ItemLaserDrill.getClipsize(stack)).
 */
public final class LaserDrillUpgrades {

    private static final String TAG = "tgeLaserDrillUpgrades";

    public static final String SILK_TOUCH = "silk_touch";
    public static final String MAGNET = "magnet";
    public static final String AUTO_SMELTING = "auto_smelting";
    public static final String FLUID_CONVERTER = "fluid_converter";
    public static final String AREA_EXPANSION = "area_expansion";
    public static final String ENERGY_EFFICIENCY = "energy_efficiency";
    public static final String CAPACITY = "capacity";

    /** Upgrade items -> upgrade id, and each id's maximum count. */
    private static final Map<Item, String> UPGRADE_ITEMS = new LinkedHashMap<Item, String>();
    private static final Map<String, Integer> MAX_COUNT = new LinkedHashMap<String, Integer>();

    private LaserDrillUpgrades() {
    }

    /**
     * Makes an item an upgrade with the given id, of which at most maxCount can
     * be installed (call once per upgrade item, in preInit).
     */
    public static void register(Item item, String id, int maxCount) {
        UPGRADE_ITEMS.put(item, id);
        MAX_COUNT.put(id, maxCount);
    }

    public static boolean isUpgrade(ItemStack stack) {
        return !stack.isEmpty() && UPGRADE_ITEMS.containsKey(stack.getItem());
    }

    /**
     * Adds as much of the stack to the drill's set as the upgrade's maximum
     * allows.
     *
     * @return how many items were installed (the caller takes them from the slot);
     *         0 if it is not an upgrade or the maximum is already reached
     */
    public static int install(ItemStack drill, ItemStack upgrade) {
        if (!isUpgrade(upgrade)) {
            return 0;
        }
        String id = UPGRADE_ITEMS.get(upgrade.getItem());
        int installed = getCount(drill, id);
        int count = Math.min(upgrade.getCount(), MAX_COUNT.get(id) - installed);
        if (count <= 0) {
            return 0;
        }
        write(drill).setInteger(id, installed + count);
        return count;
    }

    public static boolean has(ItemStack drill, String id) {
        return getCount(drill, id) > 0;
    }

    /** Installed upgrades, id -> count, in the order they were first installed. */
    public static Map<String, Integer> getInstalled(ItemStack drill) {
        Map<String, Integer> out = new LinkedHashMap<String, Integer>();
        NBTTagCompound root = drill.getTagCompound();
        if (root != null && root.hasKey(TAG)) {
            NBTTagCompound set = root.getCompoundTag(TAG);
            for (String id : set.getKeySet()) {
                int count = set.getInteger(id);
                if (count > 0) {
                    out.put(id, count);
                }
            }
        }
        return out;
    }

    public static int getCount(ItemStack drill, String id) {
        Integer count = getInstalled(drill).get(id);
        return count == null ? 0 : count;
    }

    private static NBTTagCompound write(ItemStack drill) {
        NBTTagCompound root = drill.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            drill.setTagCompound(root);
        }
        if (!root.hasKey(TAG)) {
            root.setTag(TAG, new NBTTagCompound());
        }
        return root.getCompoundTag(TAG);
    }
}
