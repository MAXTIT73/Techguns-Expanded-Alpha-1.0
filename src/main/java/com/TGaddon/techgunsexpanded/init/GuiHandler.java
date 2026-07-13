package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (id == 0 && te instanceof TileEntityFuelGenerator) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGenerator) te);
        }
        if (id == 1 && te instanceof TileEntityFuelGeneratorMk2) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk2) te);
        }
        if (id == 2 && te instanceof TileEntityFuelGeneratorMk3) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk3) te);
        }
        if (id == 3 && te instanceof TileEntityFuelGeneratorMk4) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk4) te);
        }
        if (id == 4 && te instanceof TileEntityFuelGeneratorMk5) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk5) te);
        }
        if (id == 5 && te instanceof TileEntityFuelGeneratorMk6) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk6) te);
        }
        if (id == 6 && te instanceof TileEntityFuelGeneratorCreative) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorCreative) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (id == 0 && te instanceof TileEntityFuelGenerator) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGenerator) te);
        }
        if (id == 1 && te instanceof TileEntityFuelGeneratorMk2) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk2) te);
        }
        if (id == 2 && te instanceof TileEntityFuelGeneratorMk3) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk3) te);
        }
        if (id == 3 && te instanceof TileEntityFuelGeneratorMk4) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk4) te);
        }
        if (id == 4 && te instanceof TileEntityFuelGeneratorMk5) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk5) te);
        }
        if (id == 5 && te instanceof TileEntityFuelGeneratorMk6) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk6) te);
        }
        if (id == 6 && te instanceof TileEntityFuelGeneratorCreative) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorCreative) te);
        }
        return null;
    }
}
