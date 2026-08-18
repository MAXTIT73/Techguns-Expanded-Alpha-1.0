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
    public static Item THUMB_TUNGSTEN_ORE;
    public static Item NETHER_ARMOR_PLATING;
    public static Item ADVANCED_CYBERNETIC_PARTS;
    public static Item POWER_ARMOR_PLATING_MK2;
    public static Item ELITE_CYBERNETIC_PARTS;
    public static Item MECHANICAL_PARTS_TITAN;
    public static Item TUNGSTEN_CARBIDE_PLATE;
    public static Item MECHANICAL_PARTS_TUNGSTEN_CARBIDE;
    public static Item OVERHEATED_BLAZE_ROD;

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

        THUMB_TUNGSTEN_ORE = new Item()
                .setUnlocalizedName("thumb_tungsten_ore")
                .setRegistryName(TechgunsExpanded.MODID, "thumb_tungsten_ore")
                .setCreativeTab(Techguns.tabTechgun);

        NETHER_ARMOR_PLATING = new Item()
                .setUnlocalizedName("nether_armor_plating")
                .setRegistryName(TechgunsExpanded.MODID, "nether_armor_plating")
                .setCreativeTab(Techguns.tabTechgun);

        ADVANCED_CYBERNETIC_PARTS = new Item()
                .setUnlocalizedName("advanced_cybernetic_parts")
                .setRegistryName(TechgunsExpanded.MODID, "advanced_cybernetic_parts")
                .setCreativeTab(Techguns.tabTechgun);

        POWER_ARMOR_PLATING_MK2 = new Item()
                .setUnlocalizedName("power_armor_plating_mk2")
                .setRegistryName(TechgunsExpanded.MODID, "power_armor_plating_mk2")
                .setCreativeTab(Techguns.tabTechgun);

        ELITE_CYBERNETIC_PARTS = new Item()
                .setUnlocalizedName("elite_cybernetic_parts")
                .setRegistryName(TechgunsExpanded.MODID, "elite_cybernetic_parts")
                .setCreativeTab(Techguns.tabTechgun);

        MECHANICAL_PARTS_TITAN = new Item()
                .setUnlocalizedName("mechanical_parts_titan")
                .setRegistryName(TechgunsExpanded.MODID, "mechanical_parts_titan")
                .setCreativeTab(Techguns.tabTechgun);

        TUNGSTEN_CARBIDE_PLATE = new Item()
                .setUnlocalizedName("tungsten_carbide_plate")
                .setRegistryName(TechgunsExpanded.MODID, "tungsten_carbide_plate")
                .setCreativeTab(Techguns.tabTechgun);

        MECHANICAL_PARTS_TUNGSTEN_CARBIDE = new Item()
                .setUnlocalizedName("mechanical_parts_tungsten_carbide")
                .setRegistryName(TechgunsExpanded.MODID, "mechanical_parts_tungsten_carbide")
                .setCreativeTab(Techguns.tabTechgun);

        OVERHEATED_BLAZE_ROD = new Item()
                .setUnlocalizedName("overheated_blaze_rod")
                .setRegistryName(TechgunsExpanded.MODID, "overheated_blaze_rod")
                .setCreativeTab(Techguns.tabTechgun);
    }
}