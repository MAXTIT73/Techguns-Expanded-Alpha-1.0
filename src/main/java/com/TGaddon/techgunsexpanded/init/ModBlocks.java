package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import techguns.Techguns;

public class ModBlocks {

    public static Block TUNGSTEN_CARBIDE_ORE;
    public static Block TUNGSTEN_CARBIDE_BLOCK;
    public static Block FUEL_GENERATOR;
    public static Block FUEL_GENERATOR_MK2;
    public static Block FUEL_GENERATOR_MK3;
    public static Block FUEL_GENERATOR_MK4;
    public static Block FUEL_GENERATOR_MK5;
    public static Block FUEL_GENERATOR_MK6;
    public static Block FUEL_GENERATOR_CREATIVE;
    public static Block ENRICHED_URANIUM_BLOCK;
    public static Block CREATIVE_MECHANISM_BLOCK;

    public static ItemBlock TUNGSTEN_CARBIDE_ORE_ITEM;
    public static ItemBlock TUNGSTEN_CARBIDE_BLOCK_ITEM;
    public static ItemBlock FUEL_GENERATOR_ITEM;
    public static ItemBlock FUEL_GENERATOR_MK2_ITEM;
    public static ItemBlock FUEL_GENERATOR_MK3_ITEM;
    public static ItemBlock FUEL_GENERATOR_MK4_ITEM;
    public static ItemBlock FUEL_GENERATOR_MK5_ITEM;
    public static ItemBlock FUEL_GENERATOR_MK6_ITEM;
    public static ItemBlock FUEL_GENERATOR_CREATIVE_ITEM;
    public static ItemBlock ENRICHED_URANIUM_BLOCK_ITEM;
    public static ItemBlock CREATIVE_MECHANISM_BLOCK_ITEM;

    public static void register() {
        TUNGSTEN_CARBIDE_ORE = new Block(Material.ROCK)
                .setUnlocalizedName("tungsten_carbide_ore")
                .setRegistryName(TechgunsExpanded.MODID, "tungsten_carbide_ore")
                .setHardness(3.0f)
                .setResistance(5.0f)
                .setCreativeTab(Techguns.tabTechgun);

        TUNGSTEN_CARBIDE_BLOCK = new Block(Material.IRON)
                .setUnlocalizedName("tungsten_carbide_block")
                .setRegistryName(TechgunsExpanded.MODID, "tungsten_carbide_block")
                .setHardness(5.0f)
                .setResistance(10.0f)
                .setCreativeTab(Techguns.tabTechgun);

        TUNGSTEN_CARBIDE_ORE_ITEM = new ItemBlock(TUNGSTEN_CARBIDE_ORE);
        TUNGSTEN_CARBIDE_ORE_ITEM.setRegistryName(TUNGSTEN_CARBIDE_ORE.getRegistryName());

        TUNGSTEN_CARBIDE_BLOCK_ITEM = new ItemBlock(TUNGSTEN_CARBIDE_BLOCK);
        TUNGSTEN_CARBIDE_BLOCK_ITEM.setRegistryName(TUNGSTEN_CARBIDE_BLOCK.getRegistryName());

        FUEL_GENERATOR = new BlockFuelGenerator();
        FUEL_GENERATOR_ITEM = new ItemBlock(FUEL_GENERATOR);
        FUEL_GENERATOR_ITEM.setRegistryName(FUEL_GENERATOR.getRegistryName());

        FUEL_GENERATOR_MK2 = new BlockFuelGeneratorMk2();
        FUEL_GENERATOR_MK2_ITEM = new ItemBlock(FUEL_GENERATOR_MK2);
        FUEL_GENERATOR_MK2_ITEM.setRegistryName(FUEL_GENERATOR_MK2.getRegistryName());

        FUEL_GENERATOR_MK3 = new BlockFuelGeneratorMk3();
        FUEL_GENERATOR_MK3_ITEM = new ItemBlock(FUEL_GENERATOR_MK3);
        FUEL_GENERATOR_MK3_ITEM.setRegistryName(FUEL_GENERATOR_MK3.getRegistryName());

        FUEL_GENERATOR_MK4 = new BlockFuelGeneratorMk4();
        FUEL_GENERATOR_MK4_ITEM = new ItemBlock(FUEL_GENERATOR_MK4);
        FUEL_GENERATOR_MK4_ITEM.setRegistryName(FUEL_GENERATOR_MK4.getRegistryName());

        FUEL_GENERATOR_MK5 = new BlockFuelGeneratorMk5();
        FUEL_GENERATOR_MK5_ITEM = new ItemBlock(FUEL_GENERATOR_MK5);
        FUEL_GENERATOR_MK5_ITEM.setRegistryName(FUEL_GENERATOR_MK5.getRegistryName());

        FUEL_GENERATOR_MK6 = new BlockFuelGeneratorMk6();
        FUEL_GENERATOR_MK6_ITEM = new ItemBlock(FUEL_GENERATOR_MK6);
        FUEL_GENERATOR_MK6_ITEM.setRegistryName(FUEL_GENERATOR_MK6.getRegistryName());

        FUEL_GENERATOR_CREATIVE = new BlockFuelGeneratorCreative();
        FUEL_GENERATOR_CREATIVE_ITEM = new ItemBlock(FUEL_GENERATOR_CREATIVE);
        FUEL_GENERATOR_CREATIVE_ITEM.setRegistryName(FUEL_GENERATOR_CREATIVE.getRegistryName());

        ENRICHED_URANIUM_BLOCK = new Block(Material.IRON)
                .setUnlocalizedName("enriched_uranium_block")
                .setRegistryName(TechgunsExpanded.MODID, "enriched_uranium_block")
                .setHardness(5.0f)
                .setResistance(10.0f)
                .setCreativeTab(Techguns.tabTechgun);

        ENRICHED_URANIUM_BLOCK_ITEM = new ItemBlock(ENRICHED_URANIUM_BLOCK);
        ENRICHED_URANIUM_BLOCK_ITEM.setRegistryName(ENRICHED_URANIUM_BLOCK.getRegistryName());

        // Diamond block hardness (5.0), 2x obsidian blast resistance (6000 * 2 = 12000)
        CREATIVE_MECHANISM_BLOCK = new Block(Material.IRON)
                .setUnlocalizedName("creative_mechanism_block")
                .setRegistryName(TechgunsExpanded.MODID, "creative_mechanism_block")
                .setHardness(5.0f)
                .setResistance(12000.0f)
                .setCreativeTab(Techguns.tabTechgun);

        CREATIVE_MECHANISM_BLOCK_ITEM = new ItemBlock(CREATIVE_MECHANISM_BLOCK);
        CREATIVE_MECHANISM_BLOCK_ITEM.setRegistryName(CREATIVE_MECHANISM_BLOCK.getRegistryName());
}}