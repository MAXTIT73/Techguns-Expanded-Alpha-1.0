package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import techguns.Techguns;

public class BlockFuelGeneratorCreative extends Block implements ITileEntityProvider {

    public BlockFuelGeneratorCreative() {
        super(Material.IRON);
        setUnlocalizedName("fuel_generator_creative");
        setRegistryName(TechgunsExpanded.MODID, "fuel_generator_creative");
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

    /**
     * Drop any fuel still inside the generator when it is broken, so the
     * player never loses expensive fuel items left in the slot.
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFuelGenerator) {
            ItemStackHandler inv = ((TileEntityFuelGenerator) te).getInventory();
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.breakBlock(world, pos, state);
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
