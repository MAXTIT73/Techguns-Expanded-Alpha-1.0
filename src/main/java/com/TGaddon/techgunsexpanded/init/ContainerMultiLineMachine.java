package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import techguns.gui.containers.BasicMachineContainer;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Window slots of a multi-line machine (TileEntityMultiLineMachine): the
 * machine's slots in its own slot order (addMachineSlots, placed by a
 * MachineLayout), then the player inventory (lower in taller windows).
 *
 * Besides what Techguns' BasicMachineContainer syncs (power, redstone,
 * security), it sends each line's progress to the client, plus the machine's
 * own extra values (getExtraFieldCount / getExtraField / setExtraField).
 */
public abstract class ContainerMultiLineMachine extends BasicMachineContainer {

    /** Window property ids after Techguns' own: the machine's extras, then 2 per line. */
    private static final int FIELD_FIRST = FIELD_SYNC_ID_POWER_STORED + 1;

    protected final TileEntityMultiLineMachine machine;
    private final int machineSlots;
    private final int playerSlotsEnd;
    private final int[] last;

    protected ContainerMultiLineMachine(InventoryPlayer player, TileEntityMultiLineMachine machine,
                                        MachineLayout layout) {
        super(player, machine);
        this.machine = machine;
        this.machineSlots = machine.slotCount;
        this.playerSlotsEnd = this.machineSlots + 36;

        IItemHandler inventory = machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
        if (inventory instanceof ItemStackHandlerPlus) {
            // Container slot i must be machine slot i (transferStackInSlot).
            this.addMachineSlots((ItemStackHandlerPlus) inventory, layout);
        }
        this.addPlayerInventorySlots(player, layout.inventoryOffsetX(), layout.inventoryOffsetY);

        this.last = new int[this.getExtraFieldCount() + 2 * machine.lines.length];
        java.util.Arrays.fill(this.last, Integer.MIN_VALUE);
    }

    /** Adds all of the machine's slots, in its slot order. */
    protected abstract void addMachineSlots(ItemStackHandlerPlus handler, MachineLayout layout);

    /** Extra values of the machine to send to the client (none by default). */
    protected int getExtraFieldCount() {
        return 0;
    }

    protected int getExtraField(int i) {
        return 0;
    }

    protected void setExtraField(int i, int value) {
    }

    /** TGBaseContainer.addPlayerInventorySlots, moved by (offsetX, offsetY) in wider / taller windows. */
    protected void addPlayerInventorySlots(InventoryPlayer player, int offsetX, int offsetY) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(player, col + row * 9 + 9, 8 + offsetX + col * 18,
                        84 + offsetY + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(player, col, 8 + offsetX + col * 18, 142 + offsetY));
        }
    }

    private int[] values() {
        int extras = this.getExtraFieldCount();
        int[] now = new int[this.last.length];
        for (int i = 0; i < extras; i++) {
            now[i] = this.getExtraField(i);
        }
        for (int i = 0; i < this.machine.lines.length; i++) {
            now[extras + 2 * i] = this.machine.lines[i].progress;
            now[extras + 2 * i + 1] = this.machine.lines[i].totaltime;
        }
        return now;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int[] now = this.values();
        for (IContainerListener listener : this.listeners) {
            for (int k = 0; k < now.length; k++) {
                if (this.last[k] != now[k]) {
                    listener.sendWindowProperty(this, FIELD_FIRST + k, now[k]);
                }
            }
        }
        System.arraycopy(now, 0, this.last, 0, now.length);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        int k = id - FIELD_FIRST;
        if (k < 0 || k >= this.last.length) {
            super.updateProgressBar(id, data);
            return;
        }
        int extras = this.getExtraFieldCount();
        if (k < extras) {
            this.setExtraField(k, data);
        } else {
            TileEntityMultiLineMachine.Line line = this.machine.lines[(k - extras) / 2];
            if ((k - extras) % 2 == 0) {
                line.progress = data;
            } else {
                line.totaltime = data;
            }
        }
    }

    /** Techguns' machine transferStackInSlot with this machine's slots. */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer ply, int id) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(id);
        if (slot == null || !slot.getHasStack()) {
            return stack;
        }
        ItemStack stack1 = slot.getStack();
        stack = stack1.copy();

        if (id < this.machineSlots) {
            // From the machine into the player's inventory.
            if (!this.mergeItemStack(stack1, this.machineSlots, this.playerSlotsEnd, false)) {
                return ItemStack.EMPTY;
            }
            slot.onSlotChange(stack1, stack);
        } else if (id < this.playerSlotsEnd) {
            // From the player into the slot the machine wants it in.
            int target = this.machine.getValidSlotForItemInMachine(stack1);
            if (target < 0 || !this.mergeItemStack(stack1, target, target + 1, false)) {
                return ItemStack.EMPTY;
            }
            slot.onSlotChange(stack1, stack);
        }

        if (stack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        if (stack1.getCount() == stack.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(ply, stack1);
        return stack;
    }
}
