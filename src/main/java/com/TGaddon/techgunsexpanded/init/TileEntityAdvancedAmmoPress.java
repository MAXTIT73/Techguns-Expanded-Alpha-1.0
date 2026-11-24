package com.TGaddon.techgunsexpanded.init;

/** Advanced Ammo Press (tier 2): 2 lines, 1.5x the Ammo Press's speed. */
public class TileEntityAdvancedAmmoPress extends TileEntityMultiLineAmmoPress {

    public static final int LINES = 2;
    public static final float SPEED = 1.5F;

    public TileEntityAdvancedAmmoPress() {
        super(LINES, SPEED);
    }

    @Override
    protected String getBlockName() {
        return "advanced_ammo_press";
    }
}
