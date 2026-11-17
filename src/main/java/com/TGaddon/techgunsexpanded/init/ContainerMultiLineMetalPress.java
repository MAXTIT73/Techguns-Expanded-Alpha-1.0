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
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Multi-line Metal Press window: Techguns' MetalPressContainer with one set of
 * press slots per line, placed by a MetalPressLayout. The upgrade slot and the
 * player inventory are where the Metal Press has them.
 *
 * Besides what BasicMachineContainer syncs (power, redstone, security), it
 * sends each line's progress and the auto split switch to the client.
 */
public class ContainerMultiLineMetalPress extends BasicMachineContainer {

    private static final int FIELD_SWITCH = FIELD_SYNC_ID_POWER_STORED + 1;
    /** Then progress and total time of line i: FIELD_LINES + 2i and + 2i + 1. */
    private static final int FIELD_LINES = FIELD_SYNC_ID_POWER_STORED + 2;

    protected final TileEntityMultiLineMetalPress press;
    /** Machine slots in this container: all of the press's, in its slot order. */
    private final int machineSlots;
    private final int playerSlotsEnd;
    private byte lastSwitch = -1;
    private final int[] lastLine;

    public ContainerMultiLineMetalPress(InventoryPlayer player, TileEntityMultiLineMetalPress press,
                                        MetalPressLayout layout) {
        super(player, press);
        this.press = press;
        this.machineSlots = press.slotCount;
        this.playerSlotsEnd = this.machineSlots + 36;
        this.lastLine = new int[2 * press.lines.length];
        java.util.Arrays.fill(this.lastLine, -1);

        IItemHandler inventory = press.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
        if (inventory instanceof ItemStackHandlerPlus) {
            ItemStackHandlerPlus handler = (ItemStackHandlerPlus) inventory;
            // In slot index order, so container slot i is press slot i (transferStackInSlot).
            for (int slot = 0; slot < press.slotCount; slot++) {
                if (slot == TileEntityMultiLineMetalPress.SLOT_UPGRADE) {
                    this.addSlotToContainer(new SlotMachineUpgrade(handler, slot,
                            MetalPressLayout.UPGRADE_X, MetalPressLayout.UPGRADE_Y));
                    continue;
                }
                int line = slot == 0 || slot == 1 || slot == 2 ? 0 : (slot - 1) / 3;
                int part = slot - TileEntityMultiLineMetalPress.firstSlotOfLine(line); // 0, 1 inputs; 2 output
                int x = layout.lineX[line];
                int y = layout.lineY[line];
                if (part < 2) {
                    this.addSlotToContainer(new SlotMachineInput(handler, slot,
                            x + part * MetalPressLayout.INPUT_SPACING, y));
                } else {
                    this.addSlotToContainer(new SlotItemHandlerOutput(inventory, slot,
                            x + MetalPressLayout.OUTPUT_OFFSET_X, y + MetalPressLayout.OUTPUT_DY));
                }
            }
        }
        this.addPlayerInventorySlots(player, layout.inventoryOffsetY);
    }

    /** TGBaseContainer.addPlayerInventorySlots, offsetY lower (taller windows). */
    protected void addPlayerInventorySlots(InventoryPlayer player, int offsetY) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(player, col + row * 9 + 9, 8 + col * 18, 84 + offsetY + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(player, col, 8 + col * 18, 142 + offsetY));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int[] now = new int[this.lastLine.length];
        for (int i = 0; i < this.press.lines.length; i++) {
            now[2 * i] = this.press.lines[i].progress;
            now[2 * i + 1] = this.press.lines[i].totaltime;
        }
        for (IContainerListener listener : this.listeners) {
            if (this.lastSwitch != this.press.getAutoSplitMode()) {
                listener.sendWindowProperty(this, FIELD_SWITCH, this.press.getAutoSplitMode());
            }
            for (int k = 0; k < now.length; k++) {
                if (this.lastLine[k] != now[k]) {
                    listener.sendWindowProperty(this, FIELD_LINES + k, now[k]);
                }
            }
        }
        this.lastSwitch = this.press.getAutoSplitMode();
        System.arraycopy(now, 0, this.lastLine, 0, now.length);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == FIELD_SWITCH) {
            this.press.setAutoSplitMode((byte) data);
        } else if (id >= FIELD_LINES && id < FIELD_LINES + this.lastLine.length) {
            TileEntityMultiLineMetalPress.Line line = this.press.lines[(id - FIELD_LINES) / 2];
            if ((id - FIELD_LINES) % 2 == 0) {
                line.progress = data;
            } else {
                line.totaltime = data;
            }
        } else {
            super.updateProgressBar(id, data);
        }
    }

    /** MetalPressContainer.transferStackInSlot with this press's machine slots. */
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
            // From the player into the slot the press wants it in.
            int target = this.press.getValidSlotForItemInMachine(stack1);
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
