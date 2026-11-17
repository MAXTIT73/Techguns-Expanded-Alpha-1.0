package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.oredict.OreDictionary;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.tileentities.BasicMachineTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineOperation;
import techguns.tileentities.operation.MachineSlotItem;
import techguns.tileentities.operation.MetalPressRecipes;

/**
 * A Metal Press with several production lines: the Advanced (2 lines, 1.5x
 * speed) and Professional (3 lines, 2x speed) Metal Presses.
 *
 * Each line (Line) is exactly Techguns' Metal Press - two inputs, one output,
 * Techguns' Metal Press recipes, auto split - and they all work at the same
 * time: different recipes, or the same one several times as fast. Each line is
 * also faster than a Metal Press (speed). The energy buffer and the stack
 * upgrade slot are shared; each working line draws the Metal Press's power.
 *
 * Built on Techguns' BasicMachineTileEnt (energy, redstone control, owner and
 * security, inventory, client sync), but with one operation per line instead
 * of its one: update() runs all lines, and the base class's own operation
 * fields are unused (an operation found there in an old save is moved into
 * line 1).
 *
 * Slots: 0, 1 inputs and 2 output of line 1; 3 stack upgrade; then 3 slots
 * for each further line (4-6 line 2, 7-9 line 3, ...). Slots 0-3 are where a
 * Metal Press keeps them, so presses placed before keep their items.
 */
public abstract class TileEntityMultiLineMetalPress extends BasicMachineTileEnt {

    public static final int SLOT_UPGRADE = 3;

    /** Same button id as the Metal Press's auto split button. */
    public static final int BUTTON_ID_AUTOSPLIT = techguns.tileentities.MetalPressTileEnt.BUTTON_ID_AUTOSPLIT;

    /** Metal Press values: 100 ticks per operation, 20 FE per tick, 20000 FE buffer. */
    public static final int BASE_TIME = 100;
    public static final int POWER_PER_TICK = 20;
    public static final int MAX_POWER = 20000;

    protected static final float SOUND_VOLUME = 0.5F;

    /** The lines. */
    public final Line[] lines;
    /** How much faster than a Metal Press each line works. */
    public final float speed;
    /** Slots: 3 per line plus the upgrade slot. */
    public final int slotCount;

    protected byte autoSplitMode = 0;

    protected TileEntityMultiLineMetalPress(int lineCount, float speed) {
        super(slotCount(lineCount), true, MAX_POWER);
        this.speed = speed;
        this.slotCount = slotCount(lineCount);
        this.lines = new Line[lineCount];
        for (int i = 0; i < lineCount; i++) {
            int first = firstSlotOfLine(i);
            this.lines[i] = new Line(first, first + 1, first + 2);
        }

        this.inventory = new ItemStackHandlerPlus(this.slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setContentsChanged(true);
            }

            @Override
            protected boolean allowItemInSlot(int slot, ItemStack stack) {
                if (slot == SLOT_UPGRADE) {
                    return TGItems.isMachineUpgrade(stack);
                }
                Line line = lineWithInput(slot);
                return line != null && line.isItemValid(slot, stack);
            }

            @Override
            protected boolean allowExtractFromSlot(int slot, int amount) {
                for (Line line : lines) {
                    if (slot == line.slotOut) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static int slotCount(int lineCount) {
        return 3 * lineCount + 1;
    }

    /** Line 1: slots 0-2; the upgrade slot is 3; line 2: 4-6, line 3: 7-9, ... */
    public static int firstSlotOfLine(int line) {
        return line == 0 ? 0 : 1 + 3 * line;
    }

    /** The line that has this input slot, or null. */
    protected Line lineWithInput(int slot) {
        for (Line line : this.lines) {
            if (slot == line.slotIn1 || slot == line.slotIn2) {
                return line;
            }
        }
        return null;
    }

    /** The block's name, for the window title (unused by the window now). */
    protected abstract String getBlockName();

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile." + this.getBlockName() + ".name");
    }

    // ------------------------------------------------------------------
    // Ticking: both lines, in place of BasicMachineTileEnt's one operation
    // ------------------------------------------------------------------

    @Override
    public void update() {
        if (!this.isRedstoneEnabled() || !this.enabled()) {
            return;
        }
        boolean idleLine = false;
        for (Line line : this.lines) {
            if (line.operation != null) {
                line.tick();
            } else {
                idleLine = true;
            }
        }
        // As the Metal Press: look for a new recipe when the inventory changed.
        if (idleLine && !this.world.isRemote && this.contentsChanged) {
            this.checkAndStartOperation();
        }
    }

    /** Starts what can be started on every idle line. */
    @Override
    protected void checkAndStartOperation() {
        this.setContentsChanged(false);
        for (Line line : this.lines) {
            if (line.operation == null) {
                line.checkAndStart();
            }
        }
    }

    /** Unused: each line finishes its own operation (Line.finish). */
    @Override
    protected void finishedOperation() {
    }

    /** Unused: each line plays its own sound (Line.playSound). */
    @Override
    protected void playAmbientSound() {
    }

    /** Power of one line; Line.tick multiplies it by its stack multiplier. */
    @Override
    protected int getNeededPower() {
        return POWER_PER_TICK;
    }

    @Override
    public boolean isWorking() {
        for (Line line : this.lines) {
            if (line.operation != null) {
                return true;
            }
        }
        return false;
    }

    /** For the press model's animation (RenderMachine): the line furthest along. */
    @Override
    public float getProgress() {
        float max = 0.0F;
        for (Line line : this.lines) {
            max = Math.max(max, line.getProgress());
        }
        return max;
    }

    // ------------------------------------------------------------------
    // Auto split (one switch for both lines)
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

    // ------------------------------------------------------------------
    // Shift-click into the machine
    // ------------------------------------------------------------------

    /**
     * The slot a shift-clicked item goes to, or -1: a line already holding that
     * item first, then a line it can start a recipe on, then the upgrade slot.
     */
    public int getValidSlotForItemInMachine(ItemStack item) {
        for (Line line : this.lines) {
            if (!line.input1.get().isEmpty() && OreDictionary.itemMatches(line.input1.get(), item, true)) {
                return line.slotIn1;
            }
            if (!line.input2.get().isEmpty() && OreDictionary.itemMatches(line.input2.get(), item, true)) {
                return line.slotIn2;
            }
        }
        for (Line line : this.lines) {
            if (line.input1.get().isEmpty() && line.isItemValid(line.slotIn1, item)) {
                return line.slotIn1;
            }
            if (line.input2.get().isEmpty() && line.isItemValid(line.slotIn2, item)) {
                return line.slotIn2;
            }
        }
        if (TGItems.isMachineUpgrade(item)) {
            return SLOT_UPGRADE;
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // Saving and client sync
    // ------------------------------------------------------------------

    @Override
    public void writeClientDataToNBT(NBTTagCompound tags) {
        super.writeClientDataToNBT(tags);
        tags.setByte("autoSplitMode", this.autoSplitMode);
        for (int i = 0; i < this.lines.length; i++) {
            tags.setTag("line" + i, this.lines[i].write());
        }
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tags) {
        super.readClientDataFromNBT(tags);
        this.autoSplitMode = tags.getByte("autoSplitMode");
        for (int i = 0; i < this.lines.length; i++) {
            this.lines[i].read(tags.getCompoundTag("line" + i));
        }
        // A press saved before this version ran its one operation in the base
        // class's fields: carry it on in line 1 (its slots are line 1's).
        if (this.currentOperation != null && this.lines[0].operation == null) {
            this.lines[0].operation = this.currentOperation;
            this.lines[0].progress = this.progress;
            this.lines[0].totaltime = this.totaltime > 0 ? this.totaltime : Math.round(BASE_TIME / this.speed);
        }
        this.currentOperation = null;
        this.progress = 0;
        this.totaltime = 0;
    }

    // ------------------------------------------------------------------
    // One line: Techguns' MetalPressTileEnt logic on its own three slots
    // ------------------------------------------------------------------

    public class Line {
        public final int slotIn1;
        public final int slotIn2;
        public final int slotOut;
        public final MachineSlotItem input1;
        public final MachineSlotItem input2;

        public MachineOperation operation;
        public int progress;
        public int totaltime;

        Line(int slotIn1, int slotIn2, int slotOut) {
            this.slotIn1 = slotIn1;
            this.slotIn2 = slotIn2;
            this.slotOut = slotOut;
            this.input1 = new MachineSlotItem(TileEntityMultiLineMetalPress.this, slotIn1);
            this.input2 = new MachineSlotItem(TileEntityMultiLineMetalPress.this, slotIn2);
        }

        public boolean isWorking() {
            return this.operation != null;
        }

        public float getProgress() {
            return this.totaltime > 0 ? (float) this.progress / (float) this.totaltime : 0.0F;
        }

        public int getProgressScaled(int limit) {
            return this.totaltime > 0 ? this.progress * limit / this.totaltime : 0;
        }

        /** BasicMachineTileEnt.update for one operation. */
        void tick() {
            if (!consumePower(POWER_PER_TICK * this.operation.getStackMultiplier())) {
                return;
            }
            this.progress++;
            this.playSound();
            if (this.progress >= this.totaltime) {
                if (!world.isRemote) {
                    this.finish();
                }
                this.progress = 0;
                this.totaltime = 0;
                this.operation = null;
                if (!world.isRemote) {
                    this.checkAndStart();
                    needUpdate();
                }
            }
        }

        /** MetalPressTileEnt.checkAndStartOperation on this line's slots. */
        void checkAndStart() {
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
            if (output.isEmpty() || !canOutput(output, this.slotOut)) {
                return;
            }

            // Stack upgrade: as many at once as the upgrade, the inputs and the output allow.
            ItemStack outputStack = TGItems.newStack(output, output.getCount());
            int maxStack = getMaxMachineUpgradeMultiplier(SLOT_UPGRADE);
            int multiplier = 1;
            for (int i = maxStack; i > 1; --i) {
                outputStack.setCount(output.getCount() * i);
                if (canOutput(outputStack, this.slotOut)
                        && this.input1.get().getCount() >= i && this.input2.get().getCount() >= i) {
                    multiplier = i;
                    break;
                }
            }

            ItemStack in1 = this.input1.getTypeWithSize(1);
            ItemStack in2 = this.input2.getTypeWithSize(1);
            this.input1.consume(multiplier);
            this.input2.consume(multiplier);

            this.progress = 0;
            this.totaltime = Math.round(BASE_TIME / speed);
            this.operation = new MachineOperation(output, in1, in2);
            this.operation.setStackMultiplier(multiplier);

            if (!world.isRemote) {
                needUpdate();
            }
        }

        /** MetalPressTileEnt.finishedOperation on this line's output slot. */
        void finish() {
            ItemStack result = this.operation.getItemOutput0();
            if (inventory.getStackInSlot(this.slotOut).isEmpty()) {
                inventory.setStackInSlot(this.slotOut, result);
            } else {
                inventory.insertItemNoCheck(this.slotOut, result, false);
            }
        }

        /** The press sound twice per operation, as the Metal Press. */
        void playSound() {
            int soundTick1 = Math.round(this.totaltime * 0.075F);
            int halfTime = Math.round(this.totaltime * 0.5F);
            if (this.progress == soundTick1 || this.progress == soundTick1 + halfTime) {
                world.playSound(getPos().getX(), getPos().getY(), getPos().getZ(), TGSounds.METAL_PRESS_WORK,
                        SoundCategory.BLOCKS, SOUND_VOLUME, 1.0F, true);
            }
        }

        /** MetalPressTileEnt.isItemValidForSlot for this line. */
        boolean isItemValid(int slot, ItemStack item) {
            boolean empty1 = this.input1.get().isEmpty();
            boolean empty2 = this.input2.get().isEmpty();
            if (empty1 && empty2) {
                return MetalPressRecipes.hasRecipeUsing(item);
            } else if (empty1 && slot == this.slotIn1) {
                return !MetalPressRecipes.getOutputFor(item, this.input2.get()).isEmpty();
            } else if (empty2 && slot == this.slotIn2) {
                return !MetalPressRecipes.getOutputFor(this.input1.get(), item).isEmpty();
            } else {
                return inventory.getStackInSlot(slot).isItemEqual(item);
            }
        }

        NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("progress", this.progress);
            tag.setInteger("totaltime", this.totaltime);
            if (this.operation != null) {
                this.operation.writeToNBT(tag);
            }
            return tag;
        }

        void read(NBTTagCompound tag) {
            this.progress = tag.getInteger("progress");
            this.totaltime = tag.getInteger("totaltime");
            if (tag.hasKey("operation")) {
                this.operation = new MachineOperation(null, null, null, null, 0);
                this.operation.readFromNBT(tag);
            } else {
                this.operation = null;
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
