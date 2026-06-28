package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import techguns.Techguns;


public class ModFluids {

    public static Fluid OIL;
    public static BlockOilFluid OIL_BLOCK;
    public static ItemBucket OIL_BUCKET;

    public static void registerFluid() {
        OIL = new Fluid("tgexpanded_oil",
                new net.minecraft.util.ResourceLocation("techguns_expanded", "blocks/oil_still"),
                new net.minecraft.util.ResourceLocation("techguns_expanded", "blocks/oil_flow"));

        FluidRegistry.registerFluid(OIL);

        OIL_BLOCK = new BlockOilFluid(OIL);
        OIL_BUCKET = new ItemBucket(OIL_BLOCK);

        OIL_BUCKET.setUnlocalizedName("bucket_oil");
        OIL_BUCKET.setRegistryName(
                TechgunsExpanded.MODID,
                "bucket_oil"
        );
        OIL_BUCKET.setMaxStackSize(1);
        OIL_BUCKET.setContainerItem(Items.BUCKET);
        OIL_BUCKET.setCreativeTab(Techguns.tabTechgun);

    }

}