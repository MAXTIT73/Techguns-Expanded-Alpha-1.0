package com.TGaddon.techgunsexpanded.init;

/** Elite Ammo Press (tier 4): 4 lines, 3x the Ammo Press's speed. */
public class TileEntityEliteAmmoPress extends TileEntityMultiLineAmmoPress {

    public static final int LINES = 4;
    public static final float SPEED = 3.0F;

    public TileEntityEliteAmmoPress() {
        super(LINES, SPEED);
    }

    @Override
    protected String getBlockName() {
        return "elite_ammo_press";
    }
}
