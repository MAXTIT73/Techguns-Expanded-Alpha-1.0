package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.tileentities.MetalPressTileEnt;
import techguns.tileentities.operation.MachineOperation;
import techguns.tileentities.operation.MachineSlotItem;
import techguns.tileentities.operation.MetalPressRecipes;

/**
 * A Metal Press with several production lines (TileEntityMultiLineMachine):
 * the Advanced (2 lines, 1.5x speed), Professional (3 lines, 2x) and Elite
 * (4 lines, 3x) Metal Presses.
 *
 * Each line is Techguns' Metal Press - two inputs, one output, Techguns'
 * Metal Press recipes (100 ticks, 20 FE per tick) - and one auto split switch
 * works for all lines. Slots: 0, 1 inputs and 2 output of line 1; 3 stack
 * upgrade (as in the Metal Press); 4-6 line 2, 7-9 line 3, 10-12 line 4.
 */
public abstract class TileEntityMultiLineMetalPress extends TileEntityMultiLineMachine {

    /** Same button id as the Metal Press's auto split button. */
    public static final int BUTTON_ID_AUTOSPLIT = MetalPressTileEnt.BUTTON_ID_AUTOSPLIT;

    /** Metal Press values: 100 ticks per operation, 20 FE per tick, 20000 FE buffer. */
    public static final int BASE_TIME = 100;
    public static final int POWER_PER_TICK = 20;
    public static final int MAX_POWER = 20000;

    protected static final float SOUND_VOLUME = 0.5F;

    protected byte autoSplitMode = 0;

    protected TileEntityMultiLineMetalPress(int lineCount, float speed) {
        super(lineCount, 3, speed, BASE_TIME, POWER_PER_TICK, MAX_POWER);
    }

    @Override
    protected Line createLine(int index, int firstSlot) {
        return new PressLine(index, firstSlot);
    }

    // ------------------------------------------------------------------
    // Auto split (one switch for all lines)
    // ------------------------------------------------------------------

    public byte getAutoSplitMode() {
        return this.autoSplitMode;
    }

    public void setAutoSplitMode(byte mode) {
        this.autoSplitMode = mode;
    }

    @Override
    public void buttonClicked(int id, EntityPlayer ply, String data) {
        if (id == BUTTON_ID_AUTOSPLIT) {
            if (this.isUseableByPlayer(ply)) {
                this.autoSplitMode = (byte) (this.autoSplitMode == 0 ? 1 : 0);
                this.setContentsChanged(true);
            }
        } else {
            super.buttonClicked(id, ply, data);
        }
    }

    @Override
    public void writeClientDataToNBT(NBTTagCompound tags) {
        super.writeClientDataToNBT(tags);
        tags.setByte("autoSplitMode", this.autoSplitMode);
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tags) {
        super.readClientDataFromNBT(tags);
        this.autoSplitMode = tags.getByte("autoSplitMode");
    }

    // ------------------------------------------------------------------
    // One line: Techguns' MetalPressTileEnt logic on its own three slots
    // ------------------------------------------------------------------

    public class PressLine extends Line {
        public final MachineSlotItem input1;
        public final MachineSlotItem input2;

        PressLine(int index, int firstSlot) {
            super(index, firstSlot);
            this.input1 = new MachineSlotItem(TileEntityMultiLineMetalPress.this, firstSlot);
            this.input2 = new MachineSlotItem(TileEntityMultiLineMetalPress.this, firstSlot + 1);
        }

        /** MetalPressTileEnt.checkAndStartOperation on this line's slots. */
        @Override
        protected void checkAndStart() {
            if (autoSplitMode > 0) {
                if (!this.input1.get().isEmpty() && this.input2.get().isEmpty() && this.input1.get().getCount() > 1) {
                    if (!MetalPressRecipes.getOutputFor(this.input1.get(), this.input1.get()).isEmpty()) {
                        split(this.input1, this.input2);
                    }
                } else if (this.input1.get().isEmpty() && !this.input2.get().isEmpty()
                        && this.input2.get().getCount() > 1) {
                    if (!MetalPressRecipes.getOutputFor(this.input2.get(), this.input2.get()).isEmpty()) {
                        split(this.input2, this.input1);
                    }
                }
            }

            ItemStack output = MetalPressRecipes.getOutputFor(this.input1.get(), this.input2.get());
            if (output.isEmpty() || !this.canOutputHere(output)) {
                return;
            }

            // Stack upgrade: as many at once as the upgrade, the inputs and the output allow.
            ItemStack outputStack = TGItems.newStack(output, output.getCount());
            int multiplier = 1;
            for (int i = this.maxStack(); i > 1; --i) {
                outputStack.setCount(output.getCount() * i);
                if (this.canOutputHere(outputStack)
                        && this.input1.get().getCount() >= i && this.input2.get().getCount() >= i) {
                    multiplier = i;
                    break;
                }
            }

            ItemStack in1 = this.input1.getTypeWithSize(1);
            ItemStack in2 = this.input2.getTypeWithSize(1);
            this.input1.consume(multiplier);
            this.input2.consume(multiplier);

            MachineOperation operation = new MachineOperation(output, in1, in2);
            operation.setStackMultiplier(multiplier);
            this.start(operation);
        }

        /** The press sound twice per operation, as the Metal Press. */
        @Override
        protected void playSound() {
            int soundTick1 = Math.round(this.totaltime * 0.075F);
            int halfTime = Math.round(this.totaltime * 0.5F);
            if (this.progress == soundTick1 || this.progress == soundTick1 + halfTime) {
                world.playSound(getPos().getX(), getPos().getY(), getPos().getZ(), TGSounds.METAL_PRESS_WORK,
                        SoundCategory.BLOCKS, SOUND_VOLUME, 1.0F, true);
            }
        }

        /** MetalPressTileEnt.isItemValidForSlot for this line. */
        @Override
        protected boolean isItemValid(int slot, ItemStack item) {
            boolean empty1 = this.input1.get().isEmpty();
            boolean empty2 = this.input2.get().isEmpty();
            if (empty1 && empty2) {
                return MetalPressRecipes.hasRecipeUsing(item);
            } else if (empty1 && slot == this.firstSlot) {
                return !MetalPressRecipes.getOutputFor(item, this.input2.get()).isEmpty();
            } else if (empty2 && slot == this.firstSlot + 1) {
                return !MetalPressRecipes.getOutputFor(this.input1.get(), item).isEmpty();
            } else {
                return this.stackIn(slot).isItemEqual(item);
            }
        }
    }

    /** MetalPressTileEnt.splitSlot: half of one input into the empty other one. */
    protected static void split(MachineSlotItem src, MachineSlotItem target) {
        int amount = src.get().getCount();
        int amount2 = amount / 2;
        ItemStack targetStack = src.get().copy();
        src.get().setCount(amount - amount2);
        targetStack.setCount(amount2);
        target.setStackInSlot(targetStack);
    }
}
