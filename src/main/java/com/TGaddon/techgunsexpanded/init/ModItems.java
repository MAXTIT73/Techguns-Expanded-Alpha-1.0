package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.item.Item;
import techguns.Techguns;

public class ModItems {

    public static Item TUNGSTEN_CARBIDE_INGOT;

    public static void register() {
        TUNGSTEN_CARBIDE_INGOT = new Item()
                .setUnlocalizedName("tungsten_carbide_ingot")
                .setRegistryName(TechgunsExpanded.MODID, "tungsten_carbide_ingot")
                .setCreativeTab(Techguns.tabTechgun);
    }
}