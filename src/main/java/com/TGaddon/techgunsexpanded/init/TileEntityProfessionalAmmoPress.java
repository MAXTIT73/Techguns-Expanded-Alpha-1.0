package com.TGaddon.techgunsexpanded.init;

/** Professional Ammo Press (tier 3): 3 lines, 2x the Ammo Press's speed. */
public class TileEntityProfessionalAmmoPress extends TileEntityMultiLineAmmoPress {

    public static final int LINES = 3;
    public static final float SPEED = 2.0F;

    public TileEntityProfessionalAmmoPress() {
        super(LINES, SPEED);
    }

    @Override
    protected String getBlockName() {
        return "professional_ammo_press";
    }
}
