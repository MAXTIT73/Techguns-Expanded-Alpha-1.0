package com.TGaddon.techgunsexpanded.init;

/** Professional Ammo Press (tier 3): 3 lines, 2x speed (BlockMultiLineMachine). */
public class BlockProfessionalAmmoPress extends BlockMultiLineMachine {

    public BlockProfessionalAmmoPress() {
        super("professional_ammo_press", TileEntityProfessionalAmmoPress::new,
                GuiHandler.PROFESSIONAL_AMMO_PRESS);
    }
}
