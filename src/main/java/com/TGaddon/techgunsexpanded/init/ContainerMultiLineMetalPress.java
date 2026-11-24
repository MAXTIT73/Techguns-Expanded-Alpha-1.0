package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineInput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Multi-line Metal Press window slots: the Metal Press's two inputs and output
 * once per line (MetalPressLayout), its upgrade slot, and the auto split
 * switch synced to the client.
 */
public class ContainerMultiLineMetalPress extends ContainerMultiLineMachine {

    protected final TileEntityMultiLineMetalPress press;

    public ContainerMultiLineMetalPress(InventoryPlayer player, TileEntityMultiLineMetalPress press,
                                        MetalPressLayout layout) {
        super(player, press, layout);
        this.press = press;
    }

    @Override
    protected void addMachineSlots(ItemStackHandlerPlus handler, MachineLayout layout) {
        TileEntityMultiLineMachine machine = this.machine;
        for (TileEntityMultiLineMachine.Line line : machine.lines) {
            int x = layout.lineX[line.index];
            int y = layout.lineY[line.index];
            this.addSlotToContainer(new SlotMachineInput(handler, line.firstSlot, x, y));
            this.addSlotToContainer(new SlotMachineInput(handler, line.firstSlot + 1,
                    x + MetalPressLayout.INPUT_SPACING, y));
            this.addSlotToContainer(new SlotItemHandlerOutput(handler, line.slotOut,
                    x + MetalPressLayout.OUTPUT_OFFSET_X, y + MetalPressLayout.OUTPUT_DY));
            if (line.index == 0) {
                this.addSlotToContainer(new SlotMachineUpgrade(handler, machine.upgradeSlot,
                        layout.upgradeX, layout.upgradeY));
            }
        }
    }

    @Override
    protected int getExtraFieldCount() {
        return 1;
    }

    @Override
    protected int getExtraField(int i) {
        return ((TileEntityMultiLineMetalPress) this.machine).getAutoSplitMode();
    }

    @Override
    protected void setExtraField(int i, int value) {
        ((TileEntityMultiLineMetalPress) this.machine).setAutoSplitMode((byte) value);
    }
}
