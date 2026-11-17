package com.TGaddon.techgunsexpanded.init;

/** Advanced Metal Press (tier 2): 2 lines, 1.5x the Metal Press's speed. */
public class TileEntityAdvancedMetalPress extends TileEntityMultiLineMetalPress {

    public static final int LINES = 2;
    public static final float SPEED = 1.5F;

    public TileEntityAdvancedMetalPress() {
        super(LINES, SPEED);
    }

    @Override
    protected String getBlockName() {
        return "advanced_metal_press";
    }
}
