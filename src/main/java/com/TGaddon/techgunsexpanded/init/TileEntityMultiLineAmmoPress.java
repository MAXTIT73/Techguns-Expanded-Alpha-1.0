package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import techguns.TGItems;
import techguns.TGSounds;
import techguns.gui.ButtonConstants;
import techguns.tileentities.operation.AmmoPressBuildPlans;
import techguns.tileentities.operation.MachineOperation;
import techguns.tileentities.operation.MachineSlotItem;

/**
 * An Ammo Press with several production lines (TileEntityMultiLineMachine).
 *
 * Each line is Techguns' Ammo Press - metal, metal (x2) and powder in, ammo
 * out, Techguns' Ammo Press build plans (100 ticks, 5 FE per tick) - with its
 * own build plan, so the lines can make different ammo at the same time.
 * Slots: 0 metal, 1 metal, 2 powder, 3 output of line 1; 4 stack upgrade (as
 * in the Ammo Press); then 4 per further line (5-8 line 2, ...).
 */
public abstract class TileEntityMultiLineAmmoPress extends TileEntityMultiLineMachine {

    /** Ammo Press values: 100 ticks per operation, 5 FE per tick, 20000 FE buffer. */
    public static final int BASE_TIME = 100;
    public static final int POWER_PER_TICK = 5;
    public static final int MAX_POWER = 20000;
    /** Build plans 0-3: pistol, shotgun, rifle, sniper rounds. */
    public static final byte MAX_PLAN = 3;

    /**
     * Build plan buttons: line i's ">" is FIRST_PLAN_BUTTON + 2i, its "<" one
     * more - for line 1 the Ammo Press's own next / previous ids.
     */
    public static final int FIRST_PLAN_BUTTON = ButtonConstants.BUTTON_ID_REDSTONE + 1;

    protected static final float SOUND_VOLUME = 0.5F;

    protected TileEntityMultiLineAmmoPress(int lineCount, float speed) {
        super(lineCount, 4, speed, BASE_TIME, POWER_PER_TICK, MAX_POWER);
    }

    @Override
    protected Line createLine(int index, int firstSlot) {
        return new AmmoLine(index, firstSlot);
    }

    public AmmoLine ammoLine(int i) {
        return (AmmoLine) this.lines[i];
    }

    public static int nextButton(int line) {
        return FIRST_PLAN_BUTTON + 2 * line;
    }

    public static int prevButton(int line) {
        return FIRST_PLAN_BUTTON + 2 * line + 1;
    }

    @Override
    public void buttonClicked(int id, EntityPlayer ply, String data) {
        int k = id - FIRST_PLAN_BUTTON;
        if (k < 0 || k >= 2 * this.lines.length) {
            super.buttonClicked(id, ply, data);
            return;
        }
        if (this.isUseableByPlayer(ply)) {
            AmmoLine line = this.ammoLine(k / 2);
            int plan = line.buildPlan + (k % 2 == 0 ? 1 : -1);
            line.buildPlan = (byte) (plan > MAX_PLAN ? 0 : plan < 0 ? MAX_PLAN : plan);
            this.setContentsChanged(true);
            this.needUpdate();
        }
    }

    // ------------------------------------------------------------------
    // One line: Techguns' AmmoPressTileEnt logic on its own four slots
    // ------------------------------------------------------------------

    public class AmmoLine extends Line {
        public final MachineSlotItem metal1;
        public final MachineSlotItem metal2;
        public final MachineSlotItem powder;
        /** 0-3: pistol, shotgun, rifle, sniper rounds. */
        public byte buildPlan = 0;

        AmmoLine(int index, int firstSlot) {
            super(index, firstSlot);
            this.metal1 = new MachineSlotItem(TileEntityMultiLineAmmoPress.this, firstSlot);
            this.metal2 = new MachineSlotItem(TileEntityMultiLineAmmoPress.this, firstSlot + 1);
            this.powder = new MachineSlotItem(TileEntityMultiLineAmmoPress.this, firstSlot + 2);
        }

        /** What this line's build plan makes (for the window). */
        public ItemStack getPlanResult() {
            return AmmoPressBuildPlans.getPossibleResults()[this.buildPlan];
        }

        /** AmmoPressTileEnt.checkAndStartOperation on this line's slots. */
        @Override
        protected void checkAndStart() {
            ItemStack output = AmmoPressBuildPlans.getOutputFor(this.metal1.get(), this.metal2.get(),
                    this.powder.get(), this.buildPlan);
            if (output.isEmpty() || !this.canOutputHere(output)) {
                return;
            }

            // Stack upgrade: as many at once as the upgrade, the inputs and the output allow.
            ItemStack outputStack = TGItems.newStack(output, output.getCount());
            int multiplier = 1;
            for (int i = this.maxStack(); i > 1; --i) {
                outputStack.setCount(output.getCount() * i);
                if (this.canOutputHere(outputStack) && this.metal1.canConsume(i)
                        && this.metal2.canConsume(2 * i) && this.powder.canConsume(i)) {
                    multiplier = i;
                    break;
                }
            }

            ItemStack m1 = this.metal1.getTypeWithSize(1);
            ItemStack m2 = this.metal2.getTypeWithSize(2);
            ItemStack pw = this.powder.getTypeWithSize(1);
            this.metal1.consume(multiplier);
            this.metal2.consume(multiplier * 2);
            this.powder.consume(multiplier);

            MachineOperation operation = new MachineOperation(output, m1, m2, pw);
            operation.setStackMultiplier(multiplier);
            this.start(operation);
        }

        /** The Ammo Press's two work sounds, twice per operation. */
        @Override
        protected void playSound() {
            int soundTick1 = Math.round(this.totaltime * 0.05F);
            int soundTick2 = Math.round(this.totaltime * 0.30F);
            int halfTime = Math.round(this.totaltime * 0.5F);
            double x = getPos().getX() + 0.5D;
            double y = getPos().getY() + 0.5D;
            double z = getPos().getZ() + 0.5D;
            if (this.progress == soundTick1 || this.progress == soundTick1 + halfTime) {
                world.playSound(x, y, z, TGSounds.AMMO_PRESS_WORK1, SoundCategory.BLOCKS, SOUND_VOLUME, 1.0F, true);
            } else if (this.progress == soundTick2 || this.progress == soundTick2 + halfTime) {
                world.playSound(x, y, z, TGSounds.AMMO_PRESS_WORK2, SoundCategory.BLOCKS, SOUND_VOLUME, 1.0F, true);
            }
        }

        /** The Ammo Press's slot rules: metal 1, metal 2, powder lists. */
        @Override
        protected boolean isItemValid(int slot, ItemStack item) {
            switch (slot - this.firstSlot) {
                case 0:
                    return AmmoPressBuildPlans.isInList(item, AmmoPressBuildPlans.metal1);
                case 1:
                    return AmmoPressBuildPlans.isInList(item, AmmoPressBuildPlans.metal2);
                case 2:
                    return AmmoPressBuildPlans.isInList(item, AmmoPressBuildPlans.powder);
                default:
                    return false;
            }
        }

        @Override
        protected void writeExtra(NBTTagCompound tag) {
            tag.setByte("BuildPlan", this.buildPlan);
        }

        @Override
        protected void readExtra(NBTTagCompound tag) {
            this.buildPlan = tag.getByte("BuildPlan");
        }
    }
}
