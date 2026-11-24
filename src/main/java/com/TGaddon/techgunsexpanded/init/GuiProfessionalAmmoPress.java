package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/** CLIENT ONLY. Professional Ammo Press window (GuiMultiLineAmmoPress, PROFESSIONAL layout: wider). */
public class GuiProfessionalAmmoPress extends GuiMultiLineAmmoPress {

    public GuiProfessionalAmmoPress(InventoryPlayer player, TileEntityProfessionalAmmoPress press) {
        super(player, press, AmmoPressLayout.PROFESSIONAL);
    }
}
