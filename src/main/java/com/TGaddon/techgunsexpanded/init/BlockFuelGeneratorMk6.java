package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import techguns.Techguns;

public class BlockFuelGeneratorMk6 extends Block implements ITileEntityProvider {

    public BlockFuelGeneratorMk6() {
        super(Material.IRON);
        setUnlocalizedName("fuel_generator_mk6");
        setRegistryName(TechgunsExpanded.MODID, "fuel_generator_mk6");
        setHardness(3.5f);
        setResistance(8.0f);
        setCreativeTab(Techguns.tabTechgun);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFuelGeneratorMk6();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
            EntityPlayer player, EnumHand hand, EnumFacing facing,
            float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityFuelGeneratorMk6) {
                player.openGui(TechgunsExpanded.instance, 5, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
