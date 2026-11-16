package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import com.TGaddon.techgunsexpanded.init.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Server side (and client prediction) of the Laser Drill upgrades window.
 * Layout from the MCreator design "Laserdrillupcrades": one input slot at
 * (62, 60) above the usual player inventory.
 *
 * The input slot: an upgrade put in disappears and is added to the drill's set
 * (LaserDrillUpgrades.install), up to the upgrade's maximum. Anything else -
 * and upgrades over the maximum - stays there and is given back when the
 * window closes, also when switching back to the settings with Tab.
 *
 * The drill itself stays where it is while the window is open: its hotbar slot
 * cannot be clicked or swapped (the window belongs to the drill in hand).
 */
public class ContainerLaserDrillUpgrades extends Container {

    /** Button id (vanilla "button pressed" packet): Tab - back to the settings window. */
    public static final int BUTTON_OPEN_SETTINGS = 100;

    private static final int INPUT_SLOT = 0;
    private static final int PLAYER_FIRST = 1;
    private static final int PLAYER_END = PLAYER_FIRST + 36;

    private final EntityPlayer player;
    private final IInventory input = new InventoryBasic("laser_drill_upgrade", false, 1);
    private final int lockedSlot;

    public ContainerLaserDrillUpgrades(EntityPlayer player) {
        this.player = player;
        InventoryPlayer inv = player.inventory;
        this.lockedSlot = inv.currentItem;

        this.addSlotToContainer(new UpgradeSlot(this.input, 0, 62, 60));
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(col == this.lockedSlot
                    ? new LockedSlot(inv, col, 8 + col * 18, 142)
                    : new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    /** Open only while the drill is still in the main hand. */
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() == ModTools.LASER_DRILL;
    }

    /** Tab: back to the settings window (opened by the server). */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == BUTTON_OPEN_SETTINGS && !player.world.isRemote) {
            player.openGui(TechgunsExpanded.instance, GuiHandler.LASER_DRILL, player.world, 0, 0, 0);
            return true;
        }
        return false;
    }

    /** Number keys must not swap the drill out of its slot either. */
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (clickType == ClickType.SWAP && dragType == this.lockedSlot) {
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }

    /** Shift-click: between the input slot and the player's inventory. */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (index == INPUT_SLOT) {
            if (!this.mergeItemStack(stack, PLAYER_FIRST, PLAYER_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.mergeItemStack(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return original;
    }

    /** Whatever is left in the input slot goes back to the player (or is dropped). */
    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.world.isRemote) {
            this.clearContainer(player, player.world, this.input);
        }
    }

    /** The input slot: installs upgrades as soon as one is put in. */
    private class UpgradeSlot extends Slot {

        UpgradeSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() != ModTools.LASER_DRILL;
        }

        @Override
        public void onSlotChanged() {
            super.onSlotChanged();
            ItemStack stack = this.getStack();
            ItemStack drill = ContainerLaserDrillUpgrades.this.player.getHeldItemMainhand();
            if (drill.getItem() != ModTools.LASER_DRILL) {
                return;
            }
            int installed = LaserDrillUpgrades.install(drill, stack);
            if (installed > 0) {
                ItemStack rest = stack.copy();
                rest.shrink(installed);
                // Straight into the inventory: Slot.putStack would come back here.
                this.inventory.setInventorySlotContents(this.getSlotIndex(), rest);
            }
        }
    }

    /** The slot holding the drill: nothing goes in or out. */
    private static class LockedSlot extends Slot {

        LockedSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
