package com.TGaddon.techgunsexpanded.init;

import net.minecraft.entity.player.InventoryPlayer;

/**
 * CLIENT ONLY. Elite Ammo Press window (GuiMultiLineAmmoPress, ELITE layout):
 * 328 pixels wide, wider than Techguns' windows can draw, so it draws its own
 * background (GuiMultiLineMachine.drawExtraWideBackground).
 */
public class GuiEliteAmmoPress extends GuiMultiLineAmmoPress {

    public GuiEliteAmmoPress(InventoryPlayer player, TileEntityEliteAmmoPress press) {
        super(player, press, AmmoPressLayout.ELITE);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawExtraWideBackground();
    }
}
