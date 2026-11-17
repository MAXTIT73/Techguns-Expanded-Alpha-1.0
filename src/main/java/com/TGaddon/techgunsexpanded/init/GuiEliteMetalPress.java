package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/** CLIENT ONLY. Elite Metal Press window (GuiMultiLineMetalPress, ELITE layout: taller, lines in 2 rows). */
public class GuiEliteMetalPress extends GuiMultiLineMetalPress {

    public GuiEliteMetalPress(InventoryPlayer player, TileEntityEliteMetalPress press) {
        super(player, press, MetalPressLayout.ELITE);
    }
}
