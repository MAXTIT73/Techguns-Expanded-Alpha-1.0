package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

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
        event.getRegistry().register(ModBlocks.PROFESSIONAL_METAL_PRESS);
        event.getRegistry().register(ModBlocks.ELITE_METAL_PRESS);
        event.getRegistry().register(ModBlocks.ADVANCED_AMMO_PRESS);
        event.getRegistry().register(ModBlocks.PROFESSIONAL_AMMO_PRESS);
        event.getRegistry().register(ModBlocks.ELITE_AMMO_PRESS);
        event.getRegistry().register(ModBlocks.DOOR_CHARGE);
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
        event.getRegistry().register(ModItems.CARBON_FIBER_WING);
        event.getRegistry().register(ModItems.ROCKET_FAIRING);
        event.getRegistry().register(ModItems.ROCKET_ENGINE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.rockets.ModRockets.AMMO_HP);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.rockets.ModRockets.AMMO_HP_HV);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.equipment.ModEquipment.ANTIGRAV_JETPACK);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.LASER_DRILL);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.SILK_TOUCH_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.MAGNET_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.AUTO_SMELTING_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.FLUID_CONVERTER_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.AREA_EXPANSION_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.ENERGY_EFFICIENCY_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.tools.ModTools.CAPACITY_UPGRADE);
        event.getRegistry().register(com.TGaddon.techgunsexpanded.ammo.BreachingAmmo.BREACHING_SHELLS);
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
        event.getRegistry().register(ModBlocks.PROFESSIONAL_METAL_PRESS_ITEM);
        event.getRegistry().register(ModBlocks.ELITE_METAL_PRESS_ITEM);
        event.getRegistry().register(ModBlocks.ADVANCED_AMMO_PRESS_ITEM);
        event.getRegistry().register(ModBlocks.PROFESSIONAL_AMMO_PRESS_ITEM);
        event.getRegistry().register(ModBlocks.ELITE_AMMO_PRESS_ITEM);
        event.getRegistry().register(ModBlocks.DOOR_CHARGE_ITEM);
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        Item itemShared = Item.REGISTRY.getObject(new ResourceLocation("techguns", "itemshared"));
        if (itemShared == null) return;

        // Rocket Fairing:
        //   [ - ]                 [Titanium Plate]  [ - ]
        //   [ - ]                 [Obsidian Steel]  [ - ]
        //   [Obsidian Steel]      [Titanium Plate]  [Obsidian Steel]
        // Obsidian Steel Plate = metadata 51, Titanium Plate = metadata 54
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "rocket_fairing"),
            null,
            new ItemStack(ModItems.ROCKET_FAIRING),
            " T ",
            " O ",
            "OTO",
            'T', new ItemStack(itemShared, 1, 54),
            'O', new ItemStack(itemShared, 1, 51)
        );

        // Rocket Engine:
        //   [Steel Plate]    [Fuel Tank (Empty)] [Steel Plate]
        //   [Obsidian Steel] [Pump Mechanism]    [Obsidian Steel]
        //   [Obsidian Steel] [ - ]               [Obsidian Steel]
        // Fuel Tank (Empty) = 28, Steel Plate = 50, Obsidian Steel Plate = 51,
        // Pump Mechanism = 72
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "rocket_engine"),
            null,
            new ItemStack(ModItems.ROCKET_ENGINE),
            "SFS",
            "OPO",
            "O O",
            'S', new ItemStack(itemShared, 1, 50),
            'F', new ItemStack(itemShared, 1, 28),
            'O', new ItemStack(itemShared, 1, 51),
            'P', new ItemStack(itemShared, 1, 72)
        );

        // Carbon Fiber Wing:
        //   [Titanium Ingot] [Titanium Ingot] [Titanium Ingot]
        //   [Carbon Plate]   [Carbon Plate]   [Carbon Plate]
        //   [Carbon Plate]   [Carbon Plate]   [Carbon Plate]
        // Carbon Plate = metadata 53, Titanium Ingot = metadata 85
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "carbon_fiber_wing"),
            null,
            new ItemStack(ModItems.CARBON_FIBER_WING),
            "TTT",
            "CCC",
            "CCC",
            'T', new ItemStack(itemShared, 1, 85),
            'C', new ItemStack(itemShared, 1, 53)
        );

        // Anti-Gravity Jetpack:
        //   [Carbon Plate]      [Elite Circuit Board] [Carbon Plate]
        //   [Carbon Fiber Wing] [Anti-Gravity Device] [Carbon Fiber Wing]
        //   [Carbon Plate]      [Elite Circuit Board] [Carbon Plate]
        // Carbon Plate = metadata 53, Elite Circuit Board = metadata 66.
        // The Anti-Gravity Device is damageable (its charge is stored as damage),
        // so it is matched with WILDCARD_VALUE - otherwise a pack that has been
        // flown even once would no longer fit the recipe.
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "antigrav_jetpack"),
            null,
            new ItemStack(com.TGaddon.techgunsexpanded.equipment.ModEquipment.ANTIGRAV_JETPACK),
            "CEC",
            "WAW",
            "CEC",
            'C', new ItemStack(itemShared, 1, 53),
            'E', new ItemStack(itemShared, 1, 66),
            'W', new ItemStack(ModItems.CARBON_FIBER_WING),
            'A', new ItemStack(techguns.TGItems.ANTI_GRAV_PACK, 1,
                    net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE)
        );

        // High-Power Rocket:
        //   [ - ]         [Steel Plate]            [ - ]
        //   [Steel Plate] [Advanced Mining Charge] [Steel Plate]
        //   [Steel Plate] [TGX]                    [Steel Plate]
        // Steel Plate = itemshared 50, TGX = itemshared 75,
        // Advanced Mining Charge = block techguns:explosive_charge, metadata 1.
        Item explosiveCharge = Item.REGISTRY.getObject(new ResourceLocation("techguns", "explosive_charge"));
        if (explosiveCharge != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "rocket_high_power"),
                null,
                new ItemStack(com.TGaddon.techgunsexpanded.rockets.ModRockets.AMMO_HP),
                " S ",
                "SMS",
                "STS",
                'S', new ItemStack(itemShared, 1, 50),
                'M', new ItemStack(explosiveCharge, 1, 1),
                'T', new ItemStack(itemShared, 1, 75)
            );
        }

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

        // Power Hammer (techguns powerhammer):
        //   [Steel Plate]  [ - ]                 [ - ]
        //   [Steel Plate]  [Steel Receiver]      [Plastic Gun Stock]
        //   [Steel Plate]  [Compressed Air Tank] [Mechanical Parts (Iron)]
        Item powerHammer = Item.REGISTRY.getObject(new ResourceLocation("techguns", "powerhammer"));
        if (powerHammer != null) {
            GameRegistry.addShapedRecipe(
                new ResourceLocation(TechgunsExpanded.MODID, "power_hammer"),
                null,
                new ItemStack(powerHammer),
                "P  ",
                "PRG",
                "PAM",
                'P', new ItemStack(itemShared, 1, 50),  // Steel Plate
                'R', new ItemStack(itemShared, 1, 34),  // Steel Receiver
                'G', new ItemStack(itemShared, 1, 43),  // Plastic Gun Stock
                'A', new ItemStack(itemShared, 1, 23),  // Compressed Air Tank
                'M', new ItemStack(itemShared, 1, 57)   // Mechanical Parts (Iron)
            );
        }
    }

    /**
     * Replaces Techguns' default Vector crafting recipe with our own.
     *
     * Techguns ships two crafting recipes for the Vector (techguns:vector and
     * techguns:vector_alt). We remove both, then register our new one. Runs at
     * LOWEST priority so Techguns' recipes are already registered (and thus
     * removable) by the time this fires. The recipe registry is modifiable in
     * 1.12.2, which is what makes remove() possible.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRecipeOverride(RegistryEvent.Register<IRecipe> event) {
        Item itemShared = Item.REGISTRY.getObject(new ResourceLocation("techguns", "itemshared"));
        Item vector = Item.REGISTRY.getObject(new ResourceLocation("techguns", "vector"));
        if (itemShared == null || vector == null) {
            return;
        }

        IForgeRegistry<IRecipe> registry = event.getRegistry();
        if (registry instanceof IForgeRegistryModifiable) {
            IForgeRegistryModifiable<IRecipe> mod = (IForgeRegistryModifiable<IRecipe>) registry;
            mod.remove(new ResourceLocation("techguns", "vector"));
            mod.remove(new ResourceLocation("techguns", "vector_alt"));
            // Remove Techguns' default Power Hammer crafts (we keep our own, added
            // in onRecipeRegister). The head-upgrade recipes are left untouched.
            mod.remove(new ResourceLocation("techguns", "powerhammer"));
            mod.remove(new ResourceLocation("techguns", "powerhammer_alt"));
        }

        // New Vector recipe:
        //   [ - ]         [Glass Pane]     [Plastic Sheet]
        //   [Iron Barrel] [Iron Receiver]  [Plastic Gun Stock]
        //   [ - ]         [SMG Magazine]   [ - ]
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "vector"),
            null,
            new ItemStack(vector),
            " GS",
            "IRP",
            " M ",
            'G', new ItemStack(Blocks.GLASS_PANE),   // Glass Pane
            'S', new ItemStack(itemShared, 1, 55),   // Plastic Sheet
            'I', new ItemStack(itemShared, 1, 38),   // Iron Barrel
            'R', new ItemStack(itemShared, 1, 33),   // Iron Receiver
            'P', new ItemStack(itemShared, 1, 43),   // Plastic Gun Stock
            'M', new ItemStack(itemShared, 1, 9)     // SMG Magazine
        );
    }

    // 30000 тиков = 150 предметов за ведро (~2.3 стака), лава = 20000 тиков (~1.56 стака)
    /** Diagnostic: logs every rocket projectile spawn with its class + damage. */
    @SubscribeEvent
    public static void onRocketSpawn(net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
        net.minecraft.entity.Entity ent = event.getEntity();
        if (ent instanceof techguns.entities.projectiles.RocketProjectile) {
            String dmg = "?";
            try {
                java.lang.reflect.Field f =
                        techguns.entities.projectiles.GenericProjectile.class.getDeclaredField("damage");
                f.setAccessible(true);
                dmg = String.valueOf(f.getFloat(ent));
            } catch (Exception ignored) {}
            System.out.println("[TechgunsExpanded][rockets] ROCKET SPAWNED class="
                    + ent.getClass().getName() + " damage=" + dmg
                    + " side=" + (ent.world != null && ent.world.isRemote ? "client" : "server"));
        }
    }

    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModFluids.OIL_BUCKET) {
            event.setBurnTime(30000);
        }
    }

    /**
     * Lets an empty vanilla bucket scoop up a placed Oil source block.
     *
     * The custom OIL_BUCKET can place oil, but a plain empty bucket knows
     * nothing about our fluid, so vanilla does nothing when you right-click
     * spilled oil. This handler fills the empty bucket from a full source
     * block and removes it, mirroring how water/lava pickup works.
     */
    @SubscribeEvent
    public static void onFillBucket(FillBucketEvent event) {
        if (event.getResult() != Event.Result.DEFAULT) {
            return; // someone already handled it
        }
        ItemStack empty = event.getEmptyBucket();
        if (empty == null || empty.getItem() != Items.BUCKET) {
            return; // only plain empty buckets
        }
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = target.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModFluids.OIL_BLOCK) {
            return;
        }
        // Only a full source block (level 0) can be picked up.
        if (state.getValue(BlockFluidBase.LEVEL) != 0) {
            return;
        }

        world.setBlockToAir(pos);
        event.setFilledBucket(new ItemStack(ModFluids.OIL_BUCKET));
        event.setResult(Event.Result.ALLOW);
    }
}