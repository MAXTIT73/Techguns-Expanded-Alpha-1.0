package com.TGaddon.techgunsexpanded.init;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.oredict.OreDictionary;
import techguns.TGItems;
import techguns.tileentities.BasicMachineTileEnt;
import techguns.tileentities.operation.ItemStackHandlerPlus;
import techguns.tileentities.operation.MachineOperation;

/**
 * A Techguns machine with several independent production lines - the base of
 * the higher tiers of Techguns' machines (multi-line Metal Press, Ammo Press).
 *
 * Each line (Line) is one copy of the Techguns machine's work: its own input
 * and output slots and its own operation, so the lines run different recipes
 * at the same time, or the same one several times as fast. Each line is also
 * faster than the Techguns machine (speed). The energy buffer and the stack
 * upgrade slot are shared; each working line draws the Techguns machine's
 * power per tick.
 *
 * Built on Techguns' BasicMachineTileEnt (energy, redstone control, owner and
 * security, inventory, client sync), but with one operation per line instead
 * of its one: update() runs all lines, and the base class's own operation
 * fields are unused (an operation found there in an old save is moved into
 * line 1).
 *
 * Slots: line 1 first (inputs, then its output), then the upgrade slot, then
 * each further line. With the Techguns machine's slot count per line, line 1
 * and the upgrade slot are exactly where the Techguns machine keeps them.
 *
 * A machine says what one line does (its Line subclass, made by createLine).
 */
public abstract class TileEntityMultiLineMachine extends BasicMachineTileEnt {

    /** Input slots and the output slot of one line. */
    public final int slotsPerLine;
    public final int slotCount;
    /** The stack upgrade slot, right after line 1. */
    public final int upgradeSlot;
    /** How much faster than the Techguns machine each line works. */
    public final float speed;
    /** The Techguns machine's operation time, in ticks. */
    public final int baseTime;
    /** The Techguns machine's power per tick, for one line. */
    public final int powerPerTick;

    public final Line[] lines;

    protected TileEntityMultiLineMachine(int lineCount, int slotsPerLine, float speed, int baseTime,
                                         int powerPerTick, int maxPower) {
        super(slotsPerLine * lineCount + 1, true, maxPower);
        this.slotsPerLine = slotsPerLine;
        this.slotCount = slotsPerLine * lineCount + 1;
        this.upgradeSlot = slotsPerLine;
        this.speed = speed;
        this.baseTime = baseTime;
        this.powerPerTick = powerPerTick;
        this.lines = new Line[lineCount];
        for (int i = 0; i < lineCount; i++) {
            this.lines[i] = this.createLine(i, this.firstSlotOfLine(i));
        }

        this.inventory = new ItemStackHandlerPlus(this.slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setContentsChanged(true);
            }

            @Override
            protected boolean allowItemInSlot(int slot, ItemStack stack) {
                if (slot == upgradeSlot) {
                    return TGItems.isMachineUpgrade(stack);
                }
                Line line = lineOfSlot(slot);
                return line != null && slot != line.slotOut && line.isItemValid(slot, stack);
            }

            @Override
            protected boolean allowExtractFromSlot(int slot, int amount) {
                Line line = lineOfSlot(slot);
                return line != null && slot == line.slotOut;
            }
        };
    }

    /** Makes line number index, whose slots start at firstSlot. */
    protected abstract Line createLine(int index, int firstSlot);

    /** The block's name, for getDisplayName. */
    protected abstract String getBlockName();

    /** Line 1 starts at 0; the upgrade slot follows it; then each further line. */
    public int firstSlotOfLine(int line) {
        return line == 0 ? 0 : this.slotsPerLine * line + 1;
    }

    /** The line a slot belongs to, or null (the upgrade slot). */
    public Line lineOfSlot(int slot) {
        for (Line line : this.lines) {
            if (slot >= line.firstSlot && slot <= line.slotOut) {
                return line;
            }
        }
        return null;
    }

    /** Operation time of a line, in ticks. */
    public int lineTime() {
        return Math.max(1, Math.round(this.baseTime / this.speed));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile." + this.getBlockName() + ".name");
    }

    // ------------------------------------------------------------------
    // Ticking: every line, in place of BasicMachineTileEnt's one operation
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
        // As Techguns' machines: look for a new recipe when the inventory changed.
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
        return this.powerPerTick;
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

    /** For the machine model's animation (RenderMachine): the line furthest along. */
    @Override
    public float getProgress() {
        float max = 0.0F;
        for (Line line : this.lines) {
            max = Math.max(max, line.getProgress());
        }
        return max;
    }

    // ------------------------------------------------------------------
    // Shift-click into the machine
    // ------------------------------------------------------------------

    /**
     * The slot a shift-clicked item goes to, or -1: an input slot already
     * holding that item first, then an empty input slot that takes it (lines in
     * order), then the upgrade slot.
     */
    public int getValidSlotForItemInMachine(ItemStack item) {
        for (Line line : this.lines) {
            for (int slot = line.firstSlot; slot < line.slotOut; slot++) {
                ItemStack in = this.inventory.getStackInSlot(slot);
                if (!in.isEmpty() && OreDictionary.itemMatches(in, item, true)) {
                    return slot;
                }
            }
        }
        for (Line line : this.lines) {
            for (int slot = line.firstSlot; slot < line.slotOut; slot++) {
                if (this.inventory.getStackInSlot(slot).isEmpty() && line.isItemValid(slot, item)) {
                    return slot;
                }
            }
        }
        if (TGItems.isMachineUpgrade(item)) {
            return this.upgradeSlot;
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // Saving and client sync
    // ------------------------------------------------------------------

    @Override
    public void writeClientDataToNBT(NBTTagCompound tags) {
        super.writeClientDataToNBT(tags);
        for (int i = 0; i < this.lines.length; i++) {
            tags.setTag("line" + i, this.lines[i].write());
        }
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tags) {
        super.readClientDataFromNBT(tags);
        for (int i = 0; i < this.lines.length; i++) {
            this.lines[i].read(tags.getCompoundTag("line" + i));
        }
        // A machine saved as a one-line machine ran its operation in the base
        // class's fields: carry it on in line 1 (its slots are line 1's).
        if (this.currentOperation != null && this.lines[0].operation == null) {
            this.lines[0].operation = this.currentOperation;
            this.lines[0].progress = this.progress;
            this.lines[0].totaltime = this.totaltime > 0 ? this.totaltime : this.lineTime();
        }
        this.currentOperation = null;
        this.progress = 0;
        this.totaltime = 0;
    }

    // ------------------------------------------------------------------
    // One line
    // ------------------------------------------------------------------

    /** One production line: its slots (inputs, then the output) and its operation. */
    public abstract class Line {
        public final int index;
        public final int firstSlot;
        public final int slotOut;

        public MachineOperation operation;
        public int progress;
        public int totaltime;

        protected Line(int index, int firstSlot) {
            this.index = index;
            this.firstSlot = firstSlot;
            this.slotOut = firstSlot + slotsPerLine - 1;
        }

        /** Starts an operation if this line's inputs make a recipe (server). */
        protected abstract void checkAndStart();

        /** Whether an item may go into one of this line's input slots. */
        protected abstract boolean isItemValid(int slot, ItemStack item);

        /** The machine's work sounds, from progress and totaltime. */
        protected abstract void playSound();

        public boolean isWorking() {
            return this.operation != null;
        }

        public float getProgress() {
            return this.totaltime > 0 ? (float) this.progress / (float) this.totaltime : 0.0F;
        }

        public int getProgressScaled(int limit) {
            return this.totaltime > 0 ? this.progress * limit / this.totaltime : 0;
        }

        /** Starts an operation (inputs already taken) with this machine's line time. */
        protected void start(MachineOperation operation) {
            this.progress = 0;
            this.totaltime = lineTime();
            this.operation = operation;
            if (!world.isRemote) {
                needUpdate();
            }
        }

        /** BasicMachineTileEnt.update for one operation. */
        void tick() {
            if (!consumePower(powerPerTick * this.operation.getStackMultiplier())) {
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

        /** Puts the operation's output into this line's output slot (Techguns' finishedOperation). */
        protected void finish() {
            ItemStack result = this.operation.getItemOutput0();
            if (inventory.getStackInSlot(this.slotOut).isEmpty()) {
                inventory.setStackInSlot(this.slotOut, result);
            } else {
                inventory.insertItemNoCheck(this.slotOut, result, false);
            }
        }

        /** Room in this line's output slot for this stack. */
        protected boolean canOutputHere(ItemStack output) {
            return canOutput(output, this.slotOut);
        }

        /** How many operations at once the upgrade allows (1 without). */
        protected int maxStack() {
            return getMaxMachineUpgradeMultiplier(upgradeSlot);
        }

        protected ItemStack stackIn(int slot) {
            return inventory.getStackInSlot(slot);
        }

        /** Extra per-line data a machine saves and syncs (e.g. the Ammo Press's build plan). */
        protected void writeExtra(NBTTagCompound tag) {
        }

        protected void readExtra(NBTTagCompound tag) {
        }

        NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("progress", this.progress);
            tag.setInteger("totaltime", this.totaltime);
            if (this.operation != null) {
                this.operation.writeToNBT(tag);
            }
            this.writeExtra(tag);
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
            this.readExtra(tag);
        }
    }
}
