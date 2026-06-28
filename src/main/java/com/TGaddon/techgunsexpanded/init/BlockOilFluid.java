package com.TGaddon.techgunsexpanded.init;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockOilFluid extends BlockFluidClassic {

    public BlockOilFluid(Fluid fluid) {
        super(fluid, Material.LAVA);
        setUnlocalizedName("oil");
        setRegistryName("techguns_expanded", "oil");
    }
}