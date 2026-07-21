package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class RegistryHandler {

    private RegistryHandler() {}

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModFluids.OIL_BLOCK);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_ORE);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_BLOCK);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK2);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK3);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK4);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK5);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK6);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_CREATIVE);
        event.getRegistry().register(ModBlocks.ENRICHED_URANIUM_BLOCK);
        event.getRegistry().register(ModBlocks.CREATIVE_MECHANISM_BLOCK);
        event.getRegistry().register(ModBlocks.ADVANCED_METAL_PRESS);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModFluids.OIL_BUCKET);
        event.getRegistry().register(ModItems.TUNGSTEN_CARBIDE_INGOT);
        event.getRegistry().register(ModItems.DIAMOND_WIRE);
        event.getRegistry().register(ModItems.TITANIUM_SUPERWIRE);
        event.getRegistry().register(ModItems.COOLING_LIQUID_BOTTLE);
        event.getRegistry().register(ModItems.ANODA_SUPERWIRE);
        event.getRegistry().register(ModItems.ULTRATANIUM_INGOT);
        event.getRegistry().register(ModItems.THUMB_TUNGSTEN_ORE);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_ORE_ITEM);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_BLOCK_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK2_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK3_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK4_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK5_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK6_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_CREATIVE_ITEM);
        event.getRegistry().register(ModBlocks.ENRICHED_URANIUM_BLOCK_ITEM);
        event.getRegistry().register(ModBlocks.CREATIVE_MECHANISM_BLOCK_ITEM);
        event.getRegistry().register(ModBlocks.ADVANCED_METAL_PRESS_ITEM);
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        Item itemShared = Item.REGISTRY.getObject(new ResourceLocation("techguns", "itemshared"));
        if (itemShared == null) return;

        // Copper Wire = metadata 62, Mechanical Parts (Iron) = metadata 57
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR),
            "IWI",
            "MFM",
            "IWI",
            'I', Items.IRON_INGOT,
            'W', new ItemStack(itemShared, 1, 62),
            'M', new ItemStack(itemShared, 1, 57),
            'F', Blocks.FURNACE
        );

        // Steel Ingot = metadata 83, Copper Wire = metadata 62, Iron Plate = metadata 46
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator_mk2"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR_MK2),
            "SWS",
            "PGP",
            "SWS",
            'S', new ItemStack(itemShared, 1, 83),
            'W', new ItemStack(itemShared, 1, 62),
            'P', new ItemStack(itemShared, 1, 46),
            'G', new ItemStack(ModBlocks.FUEL_GENERATOR)
        );

        // Obsidian Steel Ingot = metadata 84, Gold Wire = metadata 63, Mechanical Parts (Hardened) = metadata 58
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator_mk3"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR_MK3),
            "OGO",
            "MFM",
            "OGO",
            'O', new ItemStack(itemShared, 1, 84),
            'G', new ItemStack(itemShared, 1, 63),
            'M', new ItemStack(itemShared, 1, 58),
            'F', new ItemStack(ModBlocks.FUEL_GENERATOR_MK2)
        );

        // Carbon Plate = metadata 53, Mechanical Parts (Carbon) = metadata 59
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator_mk4"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR_MK4),
            "CWC",
            "DFD",
            "CMC",
            'C', new ItemStack(itemShared, 1, 53),
            'W', ModItems.DIAMOND_WIRE,
            'D', Items.DIAMOND,
            'F', new ItemStack(ModBlocks.FUEL_GENERATOR_MK3),
            'M', new ItemStack(itemShared, 1, 59)
        );

        // Titanium Ingot = metadata 85, Elite Circuit Board = metadata 66
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator_mk5"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR_MK5),
            "TWT",
            "EGE",
            "TWT",
            'T', new ItemStack(itemShared, 1, 85),
            'W', ModItems.DIAMOND_WIRE,
            'E', new ItemStack(itemShared, 1, 66),
            'G', new ItemStack(ModBlocks.FUEL_GENERATOR_MK4)
        );

        // Tungsten Carbide Block: 9x Tungsten Carbide Ingot
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "tungsten_carbide_block"),
            null,
            new ItemStack(ModBlocks.TUNGSTEN_CARBIDE_BLOCK),
            "TTT",
            "TTT",
            "TTT",
            'T', ModItems.TUNGSTEN_CARBIDE_INGOT
        );

        // Anoda Technology Superwire: 8x Titanium Superwire + Plasma Generator (meta 131)
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "anoda_superwire"),
            null,
            new ItemStack(ModItems.ANODA_SUPERWIRE),
            "TTT",
            "TPT",
            "TTT",
            'T', ModItems.TITANIUM_SUPERWIRE,
            'P', new ItemStack(itemShared, 1, 131)
        );

        // Creative Generator: Fuel Generator Mk6 + Creative Mechanism Block + Plasma Generator (meta 131)
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator_creative"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR_CREATIVE),
            "FCF",
            "CPC",
            "FCF",
            'F', new ItemStack(ModBlocks.FUEL_GENERATOR_MK6),
            'C', new ItemStack(ModBlocks.CREATIVE_MECHANISM_BLOCK),
            'P', new ItemStack(itemShared, 1, 131)
        );

        // Creative Mechanism Block: Anoda Superwire + Anti Gravity Core (meta 92) + Ultratanium Ingot
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "creative_mechanism_block"),
            null,
            new ItemStack(ModBlocks.CREATIVE_MECHANISM_BLOCK),
            "AGA",
            "UUU",
            "AGA",
            'A', ModItems.ANODA_SUPERWIRE,
            'G', new ItemStack(itemShared, 1, 92),
            'U', ModItems.ULTRATANIUM_INGOT
        );

        // Enriched Uranium Block: 9x Enriched Uranium (meta 98)
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "enriched_uranium_block"),
            null,
            new ItemStack(ModBlocks.ENRICHED_URANIUM_BLOCK),
            "EEE",
            "EEE",
            "EEE",
            'E', new ItemStack(itemShared, 1, 98)
        );

        // Titanium Superwire: Diamond Wire + Cooling Liquid Bottle + Titanium Ingot (meta 85)
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "titanium_superwire"),
            null,
            new ItemStack(ModItems.TITANIUM_SUPERWIRE),
            "DCD",
            "TTT",
            "DCD",
            'D', ModItems.DIAMOND_WIRE,
            'C', ModItems.COOLING_LIQUID_BOTTLE,
            'T', new ItemStack(itemShared, 1, 85)
        );

        // Furnace: Thumb Tungsten Ore -> Tungsten Carbide Ingot
        GameRegistry.addSmelting(
            ModItems.THUMB_TUNGSTEN_ORE,
            new ItemStack(ModItems.TUNGSTEN_CARBIDE_INGOT),
            1.0f
        );

        // Titanium Plate = metadata 54
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator_mk6"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR_MK6),
            "TST",
            "PGP",
            "TST",
            'T', ModItems.TUNGSTEN_CARBIDE_INGOT,
            'S', ModItems.TITANIUM_SUPERWIRE,
            'P', new ItemStack(itemShared, 1, 54),
            'G', new ItemStack(ModBlocks.FUEL_GENERATOR_MK5)
        );
    }

    // 30000 тиков = 150 предметов за ведро (~2.3 стака), лава = 20000 тиков (~1.56 стака)
    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModFluids.OIL_BUCKET) {
            event.setBurnTime(30000);
        }
    }
}