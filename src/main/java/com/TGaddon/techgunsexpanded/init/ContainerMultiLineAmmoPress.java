package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;
import techguns.gui.widgets.SlotAmmoPressInput;
import techguns.gui.widgets.SlotFabricator;
import techguns.gui.widgets.SlotItemHandlerOutput;
import techguns.gui.widgets.SlotMachineUpgrade;
import techguns.gui.widgets.SlotTG;
import techguns.tileentities.operation.ItemStackHandlerPlus;

/**
 * Multi-line Ammo Press window slots: the Ammo Press's metal, metal, powder
 * inputs (with its empty-slot pictures) and output once per line
 * (AmmoPressLayout), its upgrade slot, and each line's build plan synced to
 * the client.
 */
public class ContainerMultiLineAmmoPress extends ContainerMultiLineMachine {

    public ContainerMultiLineAmmoPress(InventoryPlayer player, TileEntityMultiLineAmmoPress press,
                                       AmmoPressLayout layout) {
        super(player, press, layout);
    }

    @Override
    protected void addMachineSlots(ItemStackHandlerPlus handler, MachineLayout layout) {
        for (TileEntityMultiLineMachine.Line line : this.machine.lines) {
            int x = layout.lineX[line.index];
            int y = layout.lineY[line.index];
            this.addSlotToContainer(new SlotAmmoPressInput(handler, line.firstSlot, x, y,
                    SlotTG.INGOTDARKSLOT_TEX));
            this.addSlotToContainer(new SlotAmmoPressInput(handler, line.firstSlot + 1,
                    x + AmmoPressLayout.INPUT_SPACING, y, SlotTG.INGOTSLOT_TEX));
            this.addSlotToContainer(new SlotAmmoPressInput(handler, line.firstSlot + 2,
                    x + 2 * AmmoPressLayout.INPUT_SPACING, y, SlotFabricator.FABRICATOR_SLOTTEX_POWDER));
            this.addSlotToContainer(new SlotItemHandlerOutput(handler, line.slotOut,
                    x + AmmoPressLayout.OUTPUT_OFFSET_X, y + AmmoPressLayout.OUTPUT_DY));
            if (line.index == 0) {
                this.addSlotToContainer(new SlotMachineUpgrade(handler, this.machine.upgradeSlot,
                        layout.upgradeX, layout.upgradeY));
            }
        }
    }

    /** One build plan per line. */
    @Override
    protected int getExtraFieldCount() {
        return this.machine.lines.length;
    }

    @Override
    protected int getExtraField(int i) {
        return ((TileEntityMultiLineAmmoPress) this.machine).ammoLine(i).buildPlan;
    }

    @Override
    protected void setExtraField(int i, int value) {
        ((TileEntityMultiLineAmmoPress) this.machine).ammoLine(i).buildPlan = (byte) value;
    }
}
