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
        event.getRegistry().register(ModItems.NETHER_ARMOR_PLATING);
        event.getRegistry().register(ModItems.ADVANCED_CYBERNETIC_PARTS);
        event.getRegistry().register(ModItems.POWER_ARMOR_PLATING_MK2);
        event.getRegistry().register(ModItems.ELITE_CYBERNETIC_PARTS);
        event.getRegistry().register(ModItems.MECHANICAL_PARTS_TITAN);
        event.getRegistry().register(ModItems.TUNGSTEN_CARBIDE_PLATE);
        event.getRegistry().register(ModItems.MECHANICAL_PARTS_TUNGSTEN_CARBIDE);
        event.getRegistry().register(ModItems.OVERHEATED_BLAZE_ROD);
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

        // The Infiltrator: upgrade an M4 Assault Rifle into the silenced variant.
        //   [Steel Plate 50] [Glass Pane]     [Steel Plate 50]
        //   [Redstone]       [M4 Assault Rifle][Plastic Gun Stock 43]
        //   [Redstone]       [Mech. Parts Hardened 58] [ - ]
        Item m4            = Item.REGISTRY.getObject(new ResourceLocation("techguns", "m4"));
        Item m4Infiltrator = Item.REGISTRY.getObject(new ResourceLocation("techguns", "m4_infiltrator"));
        if (m4 != null && m4Infiltrator != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "m4_infiltrator"),
                null,
                new ItemStack(m4Infiltrator),
                "SGS",
                "RMP",
                "RH ",
                'S', new ItemStack(itemShared, 1, 50),  // Steel Plate
                'G', new ItemStack(Blocks.GLASS_PANE),  // Glass Pane
                'R', new ItemStack(Items.REDSTONE),     // Redstone
                'M', new ItemStack(m4),                 // M4 Assault Rifle
                'P', new ItemStack(itemShared, 1, 43),  // Plastic Gun Stock
                'H', new ItemStack(itemShared, 1, 58)   // Mechanical Parts (Hardened)
            );
        }

        // Nether Combat Boots (techguns t4 praetor boots):
        //   [ - ]                 [Diamond Wire]        [ - ]
        //   [Adv. Cybernetic Parts][ - ]                [Adv. Cybernetic Parts]
        //   [Nether Armor Plating][ - ]                 [Nether Armor Plating]
        Item netherCombatBoots = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_praetor_boots"));
        if (netherCombatBoots != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "nether_combat_boots"),
                null,
                new ItemStack(netherCombatBoots),
                " W ",
                "C C",
                "P P",
                'W', ModItems.DIAMOND_WIRE,               // Diamond Wire
                'C', ModItems.ADVANCED_CYBERNETIC_PARTS,  // Advanced Cybernetic Parts
                'P', ModItems.NETHER_ARMOR_PLATING        // Nether Armor Plating
            );
        }

        // Nether Combat Chestplate (techguns t4 praetor chestplate):
        //   [Nether Armor Plating] [ - ]          [Nether Armor Plating]
        //   [Adv. Cybernetic Parts][Diamond Wire] [Adv. Cybernetic Parts]
        //   [Nether Armor Plating] [Nether Armor Plating][Nether Armor Plating]
        Item netherCombatChestplate = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_praetor_chestplate"));
        if (netherCombatChestplate != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "nether_combat_chestplate"),
                null,
                new ItemStack(netherCombatChestplate),
                "P P",
                "CWC",
                "PPP",
                'P', ModItems.NETHER_ARMOR_PLATING,       // Nether Armor Plating
                'C', ModItems.ADVANCED_CYBERNETIC_PARTS,  // Advanced Cybernetic Parts
                'W', ModItems.DIAMOND_WIRE                // Diamond Wire
            );
        }

        // Nether Combat Leggings (techguns t4 praetor leggings):
        //   [Nether Armor Plating] [Diamond Wire] [Nether Armor Plating]
        //   [Adv. Cybernetic Parts][ - ]          [Adv. Cybernetic Parts]
        //   [Nether Armor Plating] [ - ]          [Nether Armor Plating]
        Item netherCombatLeggings = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_praetor_leggings"));
        if (netherCombatLeggings != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "nether_combat_leggings"),
                null,
                new ItemStack(netherCombatLeggings),
                "PWP",
                "C C",
                "P P",
                'P', ModItems.NETHER_ARMOR_PLATING,       // Nether Armor Plating
                'C', ModItems.ADVANCED_CYBERNETIC_PARTS,  // Advanced Cybernetic Parts
                'W', ModItems.DIAMOND_WIRE                // Diamond Wire
            );
        }

        // Nether Combat Helmet (techguns t4 praetor helmet):
        //   [Adv. Cybernetic Parts][Nether Armor Plating][Adv. Cybernetic Parts]
        //   [Nether Armor Plating] [Tactical Mask]       [Nether Armor Plating]
        //   [ - ]                  [Diamond Wire]        [ - ]
        Item tacticalMask       = Item.REGISTRY.getObject(new ResourceLocation("techguns", "tacticalmask"));
        Item netherCombatHelmet = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_praetor_helmet"));
        if (tacticalMask != null && netherCombatHelmet != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "nether_combat_helmet"),
                null,
                new ItemStack(netherCombatHelmet),
                "CPC",
                "PMP",
                " W ",
                'C', ModItems.ADVANCED_CYBERNETIC_PARTS,  // Advanced Cybernetic Parts
                'P', ModItems.NETHER_ARMOR_PLATING,       // Nether Armor Plating
                'M', new ItemStack(tacticalMask),         // Tactical Mask
                'W', ModItems.DIAMOND_WIRE                // Diamond Wire
            );
        }

        // Advanced Cybernetic Parts:
        //   [Cybernetic Parts] [Diamond Wire]     [ - ]
        //   [Titanium Ingot]   [Cybernetic Parts] [ - ]
        //   [ - ]              [ - ]              [ - ]
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "advanced_cybernetic_parts"),
            null,
            new ItemStack(ModItems.ADVANCED_CYBERNETIC_PARTS),
            "CW",
            "TC",
            'C', new ItemStack(itemShared, 1, 69),  // Cybernetic Parts
            'W', ModItems.DIAMOND_WIRE,             // Diamond Wire
            'T', new ItemStack(itemShared, 1, 85)   // Titanium Ingot
        );

        // Power Armor Mk2 Boots (techguns t4 power boots):
        //   [ - ]                    [Titanium Superwire]     [ - ]
        //   [Elite Cybernetic Parts] [ - ]                    [Elite Cybernetic Parts]
        //   [Power Armor Plating Mk2][ - ]                    [Power Armor Plating Mk2]
        Item powerBootsMk2 = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_power_boots"));
        if (powerBootsMk2 != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "power_armor_mk2_boots"),
                null,
                new ItemStack(powerBootsMk2),
                " W ",
                "C C",
                "P P",
                'W', ModItems.TITANIUM_SUPERWIRE,        // Titanium Superwire
                'C', ModItems.ELITE_CYBERNETIC_PARTS,    // Elite Cybernetic Parts
                'P', ModItems.POWER_ARMOR_PLATING_MK2    // Power Armor Plating Mk2
            );
        }

        // Power Armor Mk2 Leggings (techguns t4 power leggings):
        //   [Power Armor Plating Mk2][Titanium Superwire]     [Power Armor Plating Mk2]
        //   [Elite Cybernetic Parts] [ - ]                    [Elite Cybernetic Parts]
        //   [Power Armor Plating Mk2][ - ]                    [Power Armor Plating Mk2]
        Item powerLeggingsMk2 = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_power_leggings"));
        if (powerLeggingsMk2 != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "power_armor_mk2_leggings"),
                null,
                new ItemStack(powerLeggingsMk2),
                "PWP",
                "C C",
                "P P",
                'P', ModItems.POWER_ARMOR_PLATING_MK2,   // Power Armor Plating Mk2
                'W', ModItems.TITANIUM_SUPERWIRE,        // Titanium Superwire
                'C', ModItems.ELITE_CYBERNETIC_PARTS     // Elite Cybernetic Parts
            );
        }

        // Power Armor Mk2 Chestplate (techguns t4 power chestplate):
        //   [Power Armor Plating Mk2][ - ]                       [Power Armor Plating Mk2]
        //   [Elite Cybernetic Parts] [Anoda Technology Superwire][Elite Cybernetic Parts]
        //   [Power Armor Plating Mk2][Power Armor Plating Mk2]   [Power Armor Plating Mk2]
        Item powerChestplateMk2 = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_power_chestplate"));
        if (powerChestplateMk2 != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "power_armor_mk2_chestplate"),
                null,
                new ItemStack(powerChestplateMk2),
                "P P",
                "CAC",
                "PPP",
                'P', ModItems.POWER_ARMOR_PLATING_MK2,   // Power Armor Plating Mk2
                'C', ModItems.ELITE_CYBERNETIC_PARTS,    // Elite Cybernetic Parts
                'A', ModItems.ANODA_SUPERWIRE            // Anoda Technology Superwire
            );
        }

        // Power Armor Mk2 Helmet (techguns t4 power helmet):
        //   [Elite Cybernetic Parts] [Power Armor Plating Mk2][Elite Cybernetic Parts]
        //   [Power Armor Plating Mk2][Tactical Mask]          [Power Armor Plating Mk2]
        //   [ - ]                    [Titanium Superwire]     [ - ]
        Item tacticalMaskPh    = Item.REGISTRY.getObject(new ResourceLocation("techguns", "tacticalmask"));
        Item powerHelmetMk2    = Item.REGISTRY.getObject(new ResourceLocation("techguns", "t4_power_helmet"));
        if (tacticalMaskPh != null && powerHelmetMk2 != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "power_armor_mk2_helmet"),
                null,
                new ItemStack(powerHelmetMk2),
                "CPC",
                "PMP",
                " W ",
                'C', ModItems.ELITE_CYBERNETIC_PARTS,    // Elite Cybernetic Parts
                'P', ModItems.POWER_ARMOR_PLATING_MK2,   // Power Armor Plating Mk2
                'M', new ItemStack(tacticalMaskPh),      // Tactical Mask
                'W', ModItems.TITANIUM_SUPERWIRE         // Titanium Superwire
            );
        }

        // Alien Blaster (techguns alienblaster):
        //   [Laser Barrel]       [Mechanical Parts (Carbon)][Titanium Plate]
        //   [ - ]                [Carbon Receiver]          [Diamond Wire]
        //   [ - ]                [ - ]                      [Energy Cell]
        Item alienBlaster = Item.REGISTRY.getObject(new ResourceLocation("techguns", "alienblaster"));
        if (alienBlaster != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "alien_blaster"),
                null,
                new ItemStack(alienBlaster),
                "BMT",
                " RW",
                "  E",
                'B', new ItemStack(itemShared, 1, 41),  // Laser Barrel
                'M', new ItemStack(itemShared, 1, 59),  // Mechanical Parts (Carbon)
                'T', new ItemStack(itemShared, 1, 54),  // Titanium Plate
                'R', new ItemStack(itemShared, 1, 36),  // Carbon Receiver
                'W', ModItems.DIAMOND_WIRE,             // Diamond Wire
                'E', new ItemStack(itemShared, 1, 29)   // Energy Cell
            );
        }
    }

    // 30000 тиков = 150 предметов за ведро (~2.3 стака), лава = 20000 тиков (~1.56 стака)
    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModFluids.OIL_BUCKET) {
            event.setBurnTime(30000);
        }
    }
}