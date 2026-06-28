package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import techguns.Techguns;

public class ModBlocks {

    public static Block TUNGSTEN_CARBIDE_ORE;
    public static Block TUNGSTEN_CARBIDE_BLOCK;

    public static ItemBlock TUNGSTEN_CARBIDE_ORE_ITEM;
    public static ItemBlock TUNGSTEN_CARBIDE_BLOCK_ITEM;

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
}}