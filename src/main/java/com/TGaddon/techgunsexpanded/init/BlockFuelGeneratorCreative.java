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
import net.minecraft.creativetab.CreativeTabs;

public class BlockFuelGeneratorCreative extends Block implements ITileEntityProvider {

    public BlockFuelGeneratorCreative() {
        super(Material.IRON);
        setUnlocalizedName("fuel_generator_creative");
        setRegistryName(TechgunsExpanded.MODID, "fuel_generator_creative");
        setHardness(-1.0f);        // нельзя сломать в выживании
        setResistance(6000000.0f); // как бедрок
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFuelGeneratorCreative();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
            EntityPlayer player, EnumHand hand, EnumFacing facing,
            float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityFuelGeneratorCreative) {
                player.openGui(TechgunsExpanded.instance, 6, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
