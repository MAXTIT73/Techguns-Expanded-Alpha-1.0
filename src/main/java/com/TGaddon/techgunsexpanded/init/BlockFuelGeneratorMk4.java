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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import techguns.Techguns;

public class BlockFuelGeneratorMk4 extends Block implements ITileEntityProvider {

    public BlockFuelGeneratorMk4() {
        super(Material.IRON);
        setUnlocalizedName("fuel_generator_mk4");
        setRegistryName(TechgunsExpanded.MODID, "fuel_generator_mk4");
        setHardness(3.5f);
        setResistance(8.0f);
        setCreativeTab(Techguns.tabTechgun);
    }

    /**
     * Generators drop when broken with anything, including bare hands.
     * Material.IRON calls setRequiresTool() in vanilla, which would otherwise
     * gate the drop behind a pickaxe.
     */
    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFuelGeneratorMk4();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
            EntityPlayer player, EnumHand hand, EnumFacing facing,
            float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityFuelGeneratorMk4) {
                player.openGui(TechgunsExpanded.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
}
