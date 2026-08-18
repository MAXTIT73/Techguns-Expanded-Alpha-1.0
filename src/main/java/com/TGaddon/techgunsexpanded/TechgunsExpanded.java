package com.TGaddon.techgunsexpanded;

import com.TGaddon.techgunsexpanded.init.GuiHandler;
import com.TGaddon.techgunsexpanded.init.ModBlocks;
import com.TGaddon.techgunsexpanded.init.ModFluids;
import com.TGaddon.techgunsexpanded.init.ModItems;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGenerator;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGeneratorMk2;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGeneratorMk3;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGeneratorMk4;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGeneratorMk5;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGeneratorMk6;
import com.TGaddon.techgunsexpanded.init.TileEntityFuelGeneratorCreative;
import com.TGaddon.techgunsexpanded.init.TileEntityAdvancedMetalPress;
import com.TGaddon.techgunsexpanded.world.WorldGenTungstenCarbideOre;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fluids.Fluid;
import techguns.tileentities.operation.BlastFurnaceRecipes;
import techguns.tileentities.operation.ChemLabRecipes;
import techguns.tileentities.operation.FabricatorRecipe;
import techguns.tileentities.operation.MetalPressRecipes;
import techguns.tileentities.operation.ReactionChamberRecipe;
import techguns.util.ItemStackOreDict;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import techguns.Techguns;
import techguns.TGFluids;
import techguns.blocks.EnumOreClusterType;

@Mod(
    modid = TechgunsExpanded.MODID,
    name = TechgunsExpanded.NAME,
    version = TechgunsExpanded.VERSION,
    // Techguns is a hard dependency: this mod calls its classes directly, so
    // Forge must load Techguns first and refuse to start (with a clear "missing
    // mod" screen) instead of crashing with NoClassDefFoundError if it's absent.
    dependencies = "required-after:techguns"
)
public class TechgunsExpanded
{
    public static final String MODID = "techguns_expanded";
    public static final String NAME = "Techguns Expanded";
    public static final String VERSION = "1.0";

    public static TechgunsExpanded instance;

    private static Logger logger;


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        logger = event.getModLog();
        ModFluids.registerFluid();
        ModItems.register();
        ModBlocks.register();
        GameRegistry.registerTileEntity(TileEntityFuelGenerator.class,
                new ResourceLocation(MODID, "fuel_generator"));
        GameRegistry.registerTileEntity(TileEntityFuelGeneratorMk2.class,
                new ResourceLocation(MODID, "fuel_generator_mk2"));
        GameRegistry.registerTileEntity(TileEntityFuelGeneratorMk3.class,
                new ResourceLocation(MODID, "fuel_generator_mk3"));
        GameRegistry.registerTileEntity(TileEntityFuelGeneratorMk4.class,
                new ResourceLocation(MODID, "fuel_generator_mk4"));
        GameRegistry.registerTileEntity(TileEntityFuelGeneratorMk5.class,
                new ResourceLocation(MODID, "fuel_generator_mk5"));
        GameRegistry.registerTileEntity(TileEntityFuelGeneratorMk6.class,
                new ResourceLocation(MODID, "fuel_generator_mk6"));
        GameRegistry.registerTileEntity(TileEntityFuelGeneratorCreative.class,
                new ResourceLocation(MODID, "fuel_generator_creative"));
        GameRegistry.registerTileEntity(TileEntityAdvancedMetalPress.class,
                new ResourceLocation(MODID, "advanced_metal_press"));
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        GameRegistry.registerWorldGenerator(new WorldGenTungstenCarbideOre(), 0);
        logger.info("[TechgunsExpanded] Tungsten Carbide Ore world generator registered (Nether, dim -1)");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TGFluids.worldspawn_oils.add(0, ModFluids.OIL);
        Techguns.orecluster.addOreToCluster(
            new FluidStack(ModFluids.OIL, 1000),
            EnumOreClusterType.OIL,
            100
        );

        Item itemShared = Item.REGISTRY.getObject(new ResourceLocation("techguns", "itemshared"));

        // Metal Press: 1x Gold Wire + 1x Diamond -> 1x Diamond Wire
        if (itemShared != null) {
            MetalPressRecipes.addRecipe(
                new ItemStack(itemShared, 1, 63),        // 1x Gold Wire (meta 63)
                new ItemStack(Items.DIAMOND, 1),         // 1x Diamond
                new ItemStack(ModItems.DIAMOND_WIRE, 1), // output: 1x Diamond Wire
                false
            );
        }

        // Metal Press: 2x Tungsten Carbide Ingot -> 1x Tungsten Carbide Plate
        MetalPressRecipes.addRecipe(
            new ItemStack(ModItems.TUNGSTEN_CARBIDE_INGOT, 1), // 1x Tungsten Carbide Ingot
            new ItemStack(ModItems.TUNGSTEN_CARBIDE_INGOT, 1), // 1x Tungsten Carbide Ingot
            new ItemStack(ModItems.TUNGSTEN_CARBIDE_PLATE, 1), // output: 1x Tungsten Carbide Plate
            false
        );

        // Fabricator: 2x Diamond Block + 2x Anti Gravity Core + 3x Enriched Uranium Block + 4x Tungsten Carbide Block -> 1x Ultratanium Ingot
        if (itemShared != null) {
            ItemStackOreDict inputDiamond  = new ItemStackOreDict(new ItemStack(Blocks.DIAMOND_BLOCK), 1);
            ItemStackOreDict wireAntiGrav  = new ItemStackOreDict(new ItemStack(itemShared, 1, 92), 1); // Anti Gravity Core (meta 92)
            ItemStackOreDict powderUranium = new ItemStackOreDict(new ItemStack(ModBlocks.ENRICHED_URANIUM_BLOCK), 1);
            ItemStackOreDict plateTungsten = new ItemStackOreDict(new ItemStack(ModBlocks.TUNGSTEN_CARBIDE_BLOCK), 1);

            // Register items in Fabricator slot whitelists so the machine knows which slot each item belongs to
            FabricatorRecipe.items_wireslot.add(wireAntiGrav);
            FabricatorRecipe.items_powderslot.add(powderUranium);
            FabricatorRecipe.items_plateslot.add(plateTungsten);

            FabricatorRecipe.addRecipe(
                inputDiamond,  2,
                wireAntiGrav,  2,
                powderUranium, 3,
                plateTungsten, 4,
                new ItemStack(ModItems.ULTRATANIUM_INGOT), 1
            );
        }

        // Fabricator: 1x Laser Rifle + 2x Gold Wire + 1x Mechanical Parts (Carbon)
        //   + 1x Carbon Fibers -> 1x Blaster Shotgun
        // Slots are (main, wire, powder, plate); the Laser Rifle is the core
        // component so it goes in the main slot.
        Item laserRifle    = Item.REGISTRY.getObject(new ResourceLocation("techguns", "lasergun"));
        Item blasterShotgun = Item.REGISTRY.getObject(new ResourceLocation("techguns", "scatterbeamrifle"));
        if (itemShared != null && laserRifle != null && blasterShotgun != null) {
            ItemStackOreDict mainLaser  = new ItemStackOreDict(new ItemStack(laserRifle), 1);
            ItemStackOreDict wireGold   = new ItemStackOreDict(new ItemStack(itemShared, 1, 63), 1); // Gold Wire (meta 63)
            ItemStackOreDict powderMech = new ItemStackOreDict(new ItemStack(itemShared, 1, 59), 1); // Mechanical Parts (Carbon) (meta 59)
            ItemStackOreDict plateCarbon = new ItemStackOreDict(new ItemStack(itemShared, 1, 64), 1); // Carbon Fibers (meta 64)

            // Register non-default items in the Fabricator slot whitelists so the
            // machine accepts each one in its slot. (Main slot needs no whitelist.)
            FabricatorRecipe.items_wireslot.add(wireGold);
            FabricatorRecipe.items_powderslot.add(powderMech);
            FabricatorRecipe.items_plateslot.add(plateCarbon);

            FabricatorRecipe.addRecipe(
                mainLaser,   1,
                wireGold,    2,
                powderMech,  1,
                plateCarbon, 1,
                new ItemStack(blasterShotgun), 1
            );
        }

        // Fabricator: 4x Titanium Ingot + 4x Elite Circuit Board
        //   + 1x Mechanical Parts (Carbon) + 4x Titanium Plate -> 1x Nether Armor Plating
        // Slots are (main, wire, powder, plate).
        if (itemShared != null) {
            ItemStackOreDict mainTitanium   = new ItemStackOreDict(new ItemStack(itemShared, 1, 85), 1); // Titanium Ingot (meta 85)
            ItemStackOreDict wireCircuit    = new ItemStackOreDict(new ItemStack(itemShared, 1, 66), 1); // Elite Circuit Board (meta 66)
            ItemStackOreDict powderMechNap  = new ItemStackOreDict(new ItemStack(itemShared, 1, 59), 1); // Mechanical Parts (Carbon) (meta 59)
            ItemStackOreDict plateTitanium  = new ItemStackOreDict(new ItemStack(itemShared, 1, 54), 1); // Titanium Plate (meta 54)

            // Whitelist the non-default items into their slots. (Main slot needs none.)
            FabricatorRecipe.items_wireslot.add(wireCircuit);
            FabricatorRecipe.items_powderslot.add(powderMechNap);
            FabricatorRecipe.items_plateslot.add(plateTitanium);

            FabricatorRecipe.addRecipe(
                mainTitanium,  4,
                wireCircuit,   4,
                powderMechNap, 1,
                plateTitanium, 4,
                new ItemStack(ModItems.NETHER_ARMOR_PLATING), 1
            );
        }

        // Fabricator: 1x Advanced Cybernetic Parts + 2x Titanium Superwire
        //   + 3x Titanium Ingot + 2x Elite Circuit Board -> 1x Elite Cybernetic Parts
        // Slots are (main, wire, powder, plate); Advanced Cybernetic Parts is the
        // core component so it goes in the main slot.
        if (itemShared != null) {
            ItemStackOreDict mainAdvCyb    = new ItemStackOreDict(new ItemStack(ModItems.ADVANCED_CYBERNETIC_PARTS), 1);
            ItemStackOreDict wireSuper     = new ItemStackOreDict(new ItemStack(ModItems.TITANIUM_SUPERWIRE), 1);
            ItemStackOreDict powderTitanium = new ItemStackOreDict(new ItemStack(itemShared, 1, 85), 1); // Titanium Ingot (meta 85)
            ItemStackOreDict plateCircuit  = new ItemStackOreDict(new ItemStack(itemShared, 1, 66), 1); // Elite Circuit Board (meta 66)

            // Whitelist the non-default items into their slots. (Main slot needs none.)
            FabricatorRecipe.items_wireslot.add(wireSuper);
            FabricatorRecipe.items_powderslot.add(powderTitanium);
            FabricatorRecipe.items_plateslot.add(plateCircuit);

            FabricatorRecipe.addRecipe(
                mainAdvCyb,      1,
                wireSuper,       2,
                powderTitanium,  3,
                plateCircuit,    2,
                new ItemStack(ModItems.ELITE_CYBERNETIC_PARTS), 1
            );
        }

        // Fabricator: 3x Tungsten Carbide Ingot + 3x Advanced Cybernetic Parts
        //   + 1x Mechanical Parts (Tungsten Carbide) + 3x Tungsten Carbide Plate
        //   -> 1x Power Armor Plating Mk2
        // Slots are (main, wire, powder, plate); Tungsten Carbide Ingot is the
        // base material so it goes in the main slot.
        {
            ItemStackOreDict mainTungsten  = new ItemStackOreDict(new ItemStack(ModItems.TUNGSTEN_CARBIDE_INGOT), 1);
            ItemStackOreDict wireAdvCyb    = new ItemStackOreDict(new ItemStack(ModItems.ADVANCED_CYBERNETIC_PARTS), 1);
            ItemStackOreDict powderMechTC  = new ItemStackOreDict(new ItemStack(ModItems.MECHANICAL_PARTS_TUNGSTEN_CARBIDE), 1);
            ItemStackOreDict plateTC       = new ItemStackOreDict(new ItemStack(ModItems.TUNGSTEN_CARBIDE_PLATE), 1);

            // Whitelist the non-default items into their slots. (Main slot needs none.)
            FabricatorRecipe.items_wireslot.add(wireAdvCyb);
            FabricatorRecipe.items_powderslot.add(powderMechTC);
            FabricatorRecipe.items_plateslot.add(plateTC);

            FabricatorRecipe.addRecipe(
                mainTungsten,   3,
                wireAdvCyb,     3,
                powderMechTC,   1,
                plateTC,        3,
                new ItemStack(ModItems.POWER_ARMOR_PLATING_MK2), 1
            );
        }

        // Metal Press: 1x Titanium Plate + 1x Blaze Rod -> 1x Mechanical Parts (Titan)
        if (itemShared != null) {
            MetalPressRecipes.addRecipe(
                new ItemStack(itemShared, 1, 54),               // Titanium Plate (meta 54)
                new ItemStack(Items.BLAZE_ROD),                 // Blaze Rod
                new ItemStack(ModItems.MECHANICAL_PARTS_TITAN), // output
                false
            );
        }

        // Metal Press: 1x Tungsten Carbide Plate + 1x Overheated Blaze Rod
        //   -> 1x Mechanical Parts (Tungsten Carbide)
        MetalPressRecipes.addRecipe(
            new ItemStack(ModItems.TUNGSTEN_CARBIDE_PLATE),            // Tungsten Carbide Plate
            new ItemStack(ModItems.OVERHEATED_BLAZE_ROD),             // Overheated Blaze Rod
            new ItemStack(ModItems.MECHANICAL_PARTS_TUNGSTEN_CARBIDE), // output
            false
        );

        // Blast Furnace: 1x Magma Block + 1x Blaze Rod -> 1x Overheated Blaze Rod
        // Signature (input1, input2, output, int, int); Techguns' own recipes pass
        // a constant 10 as the first int and a per-recipe duration (ticks) as the second.
        BlastFurnaceRecipes.addRecipe(
            new ItemStack(Blocks.MAGMA),                    // Magma Block
            new ItemStack(Items.BLAZE_ROD),                 // Blaze Rod
            new ItemStack(ModItems.OVERHEATED_BLAZE_ROD),   // output
            10,                                             // XP / heat (Techguns default)
            200                                             // duration in ticks
        );

        // Reaction Chamber: 1x Tungsten Carbide Ore + Creeper Acid (5000mb, consumes 250mb) + Heatray Focus
        //   -> 1x Thumb Tungsten Ore + 2x Titanium Ore
        Fluid creeperAcid = FluidRegistry.getFluid("creeper_acid");
        if (itemShared != null && creeperAcid != null) {
            ReactionChamberRecipe.addRecipe(
                "techguns_expanded:thumb_tungsten_ore",
                new ItemStackOreDict(new ItemStack(ModBlocks.TUNGSTEN_CARBIDE_ORE), 1),
                new ItemStack(itemShared, 1, 104),         // Heatray Focus (meta 104)
                creeperAcid,
                new ItemStack[]{
                    new ItemStack(ModItems.THUMB_TUNGSTEN_ORE, 1),
                    new ItemStack(itemShared, 2, 78)        // 2x Titanium Ore (meta 78)
                },
                2,                                          // ticks (Recipe Ticks)
                1,                                          // requiredCompletion
                6,                                          // preferredIntensity (Starting Intensity)
                0,                                          // intensityMargin
                5,                                          // liquidLevel (50% = 5 buckets = 5000mb)
                250,                                        // liquidConsumtion (mb consumed)
                0.0f,                                       // instability
                ReactionChamberRecipe.RiskType.BREAK_ITEM,
                1000                                        // RF per reaction tick
            );
        }

        // Cooling Liquid Bottle: 4x Ice + 1x Diamond + Water Bottle + 2000mb Water -> Cooling Liquid Bottle
        ChemLabRecipes.addRecipe(
            new ItemStack(Blocks.ICE, 4), 4,
            new ItemStack(Items.DIAMOND, 1), 1,
            new ItemStack(Items.POTIONITEM, 1, 0), 1,
            new FluidStack(FluidRegistry.WATER, 2000),
            null,
            new ItemStack(ModItems.COOLING_LIQUID_BOTTLE),
            false,
            20
        );
    }

}
