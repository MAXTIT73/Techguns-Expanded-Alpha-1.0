package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerFuelGenerator extends Container {

    private final TileEntityFuelGenerator te;

    private int lastEnergy;
    private int lastBurnTime;
    private int lastTotalBurnTime;

    public ContainerFuelGenerator(InventoryPlayer playerInventory, TileEntityFuelGenerator te) {
        this.te = te;

        // Слот для топлива
        addSlotToContainer(new SlotItemHandler(te.getInventory(), 0, 80, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return TileEntityFurnace.getItemBurnTime(stack) > 0;
            }
        });

        // Инвентарь игрока (3 ряда)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Хотбар
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    // Синхронизация данных сервер -> клиент
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            // Энергия делится на 10 чтобы влезть в short (макс 32767)
            if (lastEnergy != te.getEnergyStored() / 10) {
                listener.sendWindowProperty(this, 0, te.getEnergyStored() / 10);
                lastEnergy = te.getEnergyStored() / 10;
            }
            if (lastBurnTime != te.getBurnTime()) {
                listener.sendWindowProperty(this, 1, te.getBurnTime());
                lastBurnTime = te.getBurnTime();
            }
            if (lastTotalBurnTime != te.getTotalBurnTime()) {
                listener.sendWindowProperty(this, 2, te.getTotalBurnTime());
                lastTotalBurnTime = te.getTotalBurnTime();
            }
        }
    }

    // Получение данных на клиенте
    @Override
    public void updateProgressBar(int id, int data) {
        switch (id) {
            case 0: te.setEnergyStored(data * 10); break;
            case 1: te.setBurnTime(data); break;
            case 2: te.setTotalBurnTime(data); break;
        }
    }

    // Shift+клик: перемещает топливо в слот автоматически
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index == 0) {
                if (!mergeItemStack(stack, 1, 37, true)) return ItemStack.EMPTY;
            } else {
                if (TileEntityFurnace.getItemBurnTime(stack) > 0) {
                    if (!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
                } else {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
            if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stack);
        }
        return result;
    }
}
