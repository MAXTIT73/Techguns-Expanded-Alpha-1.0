package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/** CLIENT ONLY. Advanced Ammo Press window (GuiMultiLineAmmoPress, ADVANCED layout). */
public class GuiAdvancedAmmoPress extends GuiMultiLineAmmoPress {

    public GuiAdvancedAmmoPress(InventoryPlayer player, TileEntityAdvancedAmmoPress press) {
        super(player, press, AmmoPressLayout.ADVANCED);
    }
}
