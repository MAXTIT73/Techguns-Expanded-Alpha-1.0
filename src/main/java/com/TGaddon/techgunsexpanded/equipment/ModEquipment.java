package com.TGaddon.techgunsexpanded.equipment;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import techguns.TGItems;

/**
 * Wearable gear added by this mod (TG special-slot items).
 */
public final class ModEquipment {

    /** Registry path / texture base name. */
    public static final String ANTIGRAV_JETPACK_NAME = "antigrav_jetpack";

    /**
     * Back-slot model texture. Points at Techguns' anti-grav pack skin for now, so
     * the pack renders correctly out of the box. To use a recolor, drop
     * <code>assets/techguns_expanded/textures/armors/antigrav_jetpack.png</code>
     * (64x64, same UV layout as antigravpack.png) into the mod and change the
     * namespace below to TechgunsExpanded.MODID.
     */
    public static final String ARMOR_TEX_DOMAIN = "techguns";
    public static final String ARMOR_TEX_PATH   = "textures/armors/antigravpack";
    /**
     * Number of camo skins in the texture set: the borrowed anti-grav set has 5
     * (antigravpack.png plus _1.._4), so right-clicking the pack cycles through
     * all of them exactly like the original. A replacement set needs the same
     * number of files, or this constant lowered to match.
     */
    public static final int ARMOR_CAMO_COUNT = 5;

    /** Inventory icon; same idea as the model texture above. */
    public static final ResourceLocation ITEM_ICON =
            new ResourceLocation("techguns", "items/antigravpack");

    public static Item ANTIGRAV_JETPACK;

    private ModEquipment() {}

    /** preInit: create the items (registered by RegistryHandler). */
    public static void createItems() {
        // Charge lasts as long as the vanilla anti-grav pack's (12000 ticks).
        ItemAntiGravJetpack pack =
                new ItemAntiGravJetpack(ANTIGRAV_JETPACK_NAME, ARMOR_CAMO_COUNT, 12000);

        // Techguns' GenericItem constructor adds every instance to TGItems.ITEMLIST,
        // and Techguns then iterates that list to register the items and to call
        // initModel() on them. We register this one ourselves, so take it back out -
        // otherwise it would be registered twice and the second call would crash.
        TGItems.ITEMLIST.remove(pack);

        // GenericItem sets the unlocalized name to "techguns.<name>"; put it back in
        // our own namespace so the lang key is item.antigrav_jetpack.name.
        pack.setUnlocalizedName(ANTIGRAV_JETPACK_NAME);

        ANTIGRAV_JETPACK = pack;
    }
}
