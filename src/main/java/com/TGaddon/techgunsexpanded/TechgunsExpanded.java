package com.TGaddon.techgunsexpanded;

import com.TGaddon.techgunsexpanded.init.ModBlocks;
import com.TGaddon.techgunsexpanded.init.ModFluids;
import com.TGaddon.techgunsexpanded.init.ModItems;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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

    private static Logger logger;


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        ModFluids.registerFluid();
        ModItems.register();
        ModBlocks.register();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TGFluids.worldspawn_oils.add(0, ModFluids.OIL);
        Techguns.orecluster.addOreToCluster(
            new FluidStack(ModFluids.OIL, 1000),
            EnumOreClusterType.OIL,
            100
        );
    }

}
