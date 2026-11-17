package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/** CLIENT ONLY. Advanced Metal Press window (GuiMultiLineMetalPress, ADVANCED layout). */
public class GuiAdvancedMetalPress extends GuiMultiLineMetalPress {

    public GuiAdvancedMetalPress(InventoryPlayer player, TileEntityAdvancedMetalPress press) {
        super(player, press, MetalPressLayout.ADVANCED);
    }
}
