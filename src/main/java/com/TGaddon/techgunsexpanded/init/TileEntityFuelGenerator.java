package com.TGaddon.techgunsexpanded.init;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityFuelGenerator extends TileEntity implements ITickable {

    public static final int MAX_ENERGY = 100000;
    public static final int RF_PER_TICK = 30;
    public static final int TRANSFER_RATE = 120;

    private int energyStored = 0;
    private int burnTime = 0;
    private int totalBurnTime = 0;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return TileEntityFurnace.getItemBurnTime(stack) > 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    private final IEnergyStorage energyStorage = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = Math.min(energyStored, maxExtract);
            if (!simulate) { energyStored -= extracted; markDirty(); }
            return extracted;
        }

        @Override public int getEnergyStored() { return energyStored; }
        @Override public int getMaxEnergyStored() { return MAX_ENERGY; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    @Override
    public void update() {
        if (world.isRemote) return;

        // Генерируем RF пока есть топливо
        if (burnTime > 0) {
            burnTime--;
            if (energyStored < MAX_ENERGY) {
                energyStored = Math.min(energyStored + getRfPerTick(), MAX_ENERGY);
            }
            markDirty();
        }

        // Берём следующее топливо если закончилось и есть место в буфере
        if (burnTime == 0 && energyStored < MAX_ENERGY) {
            ItemStack fuel = inventory.getStackInSlot(0);
            int fuelBurnTime = TileEntityFurnace.getItemBurnTime(fuel);
            if (fuelBurnTime > 0) {
                totalBurnTime = fuelBurnTime;
                burnTime = fuelBurnTime;
                inventory.extractItem(0, 1, false);
                // Возвращаем контейнер (например, пустое ведро после ведра лавы/нефти)
                ItemStack container = fuel.getItem().getContainerItem(fuel);
                if (!container.isEmpty() && inventory.getStackInSlot(0).isEmpty()) {
                    inventory.setStackInSlot(0, container);
                }
                markDirty();
            }
        }

        // Отдаём RF соседним блокам
        if (energyStored > 0) {
            for (EnumFacing facing : EnumFacing.values()) {
                TileEntity neighbor = world.getTileEntity(pos.offset(facing));
                if (neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    IEnergyStorage neighborStorage = neighbor.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                    if (neighborStorage != null && neighborStorage.canReceive()) {
                        int toSend = Math.min(energyStored, TRANSFER_RATE);
                        int sent = neighborStorage.receiveEnergy(toSend, false);
                        if (sent > 0) {
                            energyStored -= sent;
                            markDirty();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return true;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return CapabilityEnergy.ENERGY.cast(energyStorage);
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("energy", energyStored);
        compound.setInteger("burnTime", burnTime);
        compound.setInteger("totalBurnTime", totalBurnTime);
        compound.setTag("inventory", inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        energyStored = compound.getInteger("energy");
        burnTime = compound.getInteger("burnTime");
        totalBurnTime = compound.getInteger("totalBurnTime");
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    protected int getRfPerTick() { return RF_PER_TICK; }
    public String getGuiTitle() { return "Fuel Generator"; }

    public ItemStackHandler getInventory() { return inventory; }
    public int getEnergyStored() { return energyStored; }
    public int getMaxEnergy() { return MAX_ENERGY; }
    public int getBurnTime() { return burnTime; }
    public int getTotalBurnTime() { return totalBurnTime; }
    public boolean isBurning() { return burnTime > 0; }

    public void setEnergyStored(int energy) { this.energyStored = energy; }
    public void setBurnTime(int burnTime) { this.burnTime = burnTime; }
    public void setTotalBurnTime(int totalBurnTime) { this.totalBurnTime = totalBurnTime; }
}
