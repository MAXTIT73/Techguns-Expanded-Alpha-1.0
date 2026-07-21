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

@Mod(modid = TechgunsExpanded.MODID, name = TechgunsExpanded.NAME, version = TechgunsExpanded.VERSION)
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
