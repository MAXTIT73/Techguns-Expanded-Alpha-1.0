package com.TGaddon.techgunsexpanded.init;

/** Elite Ammo Press (tier 4): 4 lines, 3x speed (BlockMultiLineMachine). */
public class BlockEliteAmmoPress extends BlockMultiLineMachine {

    public BlockEliteAmmoPress() {
        super("elite_ammo_press", TileEntityEliteAmmoPress::new, GuiHandler.ELITE_AMMO_PRESS);
    }
}
