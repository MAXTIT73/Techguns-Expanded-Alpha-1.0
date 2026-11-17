package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/** CLIENT ONLY. Professional Metal Press window (GuiMultiLineMetalPress, PROFESSIONAL layout). */
public class GuiProfessionalMetalPress extends GuiMultiLineMetalPress {

    public GuiProfessionalMetalPress(InventoryPlayer player, TileEntityProfessionalMetalPress press) {
        super(player, press, MetalPressLayout.PROFESSIONAL);
    }
}
