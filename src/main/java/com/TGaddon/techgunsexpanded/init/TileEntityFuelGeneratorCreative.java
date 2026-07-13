package com.TGaddon.techgunsexpanded.init;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityFuelGeneratorCreative extends TileEntityFuelGenerator {

    @Override
    public String getGuiTitle() {
        return "Creative Generator";
    }

    @Override
    public void update() {
        if (world.isRemote) return;

        // Всегда держим буфер полным - бесконечная энергия
        setEnergyStored(MAX_ENERGY);

        // Передаём RF соседям, не уменьшая наш буфер
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            if (neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                IEnergyStorage neighborStorage = neighbor.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (neighborStorage != null && neighborStorage.canReceive()) {
                    neighborStorage.receiveEnergy(MAX_ENERGY, false);
                }
            }
        }

        markDirty();
    }
}
