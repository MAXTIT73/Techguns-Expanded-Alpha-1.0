package com.TGaddon.techgunsexpanded.init;

/** Advanced Ammo Press (tier 2): 2 lines, 1.5x speed (BlockMultiLineMachine). */
public class BlockAdvancedAmmoPress extends BlockMultiLineMachine {

    public BlockAdvancedAmmoPress() {
        super("advanced_ammo_press", TileEntityAdvancedAmmoPress::new, GuiHandler.ADVANCED_AMMO_PRESS);
    }
}
