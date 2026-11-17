package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/** Advanced Metal Press window slots (ContainerMultiLineMetalPress, ADVANCED layout). */
public class ContainerAdvancedMetalPress extends ContainerMultiLineMetalPress {

    public ContainerAdvancedMetalPress(InventoryPlayer player, TileEntityAdvancedMetalPress press) {
        super(player, press, MetalPressLayout.ADVANCED);
    }
}
