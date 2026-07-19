package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.item.Item;
import techguns.Techguns;

public class ModItems {

    public static Item TUNGSTEN_CARBIDE_INGOT;
    public static Item DIAMOND_WIRE;
    public static Item TITANIUM_SUPERWIRE;
    public static Item COOLING_LIQUID_BOTTLE;
    public static Item ANODA_SUPERWIRE;
    public static Item ULTRATANIUM_INGOT;

    public static void register() {
        TUNGSTEN_CARBIDE_INGOT = new Item()
                .setUnlocalizedName("tungsten_carbide_ingot")
                .setRegistryName(TechgunsExpanded.MODID, "tungsten_carbide_ingot")
                .setCreativeTab(Techguns.tabTechgun);

        DIAMOND_WIRE = new Item()
                .setUnlocalizedName("diamond_wire")
                .setRegistryName(TechgunsExpanded.MODID, "diamond_wire")
                .setCreativeTab(Techguns.tabTechgun);

        TITANIUM_SUPERWIRE = new Item()
                .setUnlocalizedName("titanium_superwire")
                .setRegistryName(TechgunsExpanded.MODID, "titanium_superwire")
                .setCreativeTab(Techguns.tabTechgun);

        COOLING_LIQUID_BOTTLE = new Item()
                .setUnlocalizedName("cooling_liquid_bottle")
                .setRegistryName(TechgunsExpanded.MODID, "cooling_liquid_bottle")
                .setCreativeTab(Techguns.tabTechgun);

        ANODA_SUPERWIRE = new Item()
                .setUnlocalizedName("anoda_superwire")
                .setRegistryName(TechgunsExpanded.MODID, "anoda_superwire")
                .setCreativeTab(Techguns.tabTechgun);

        ULTRATANIUM_INGOT = new Item()
                .setUnlocalizedName("ultratanium_ingot")
                .setRegistryName(TechgunsExpanded.MODID, "ultratanium_ingot")
                .setCreativeTab(Techguns.tabTechgun);
    }
}