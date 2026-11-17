package com.TGaddon.techgunsexpanded.init;

/** Elite Metal Press (tier 4): 4 lines, 3x the Metal Press's speed. */
public class TileEntityEliteMetalPress extends TileEntityMultiLineMetalPress {

    public static final int LINES = 4;
    public static final float SPEED = 3.0F;

    public TileEntityEliteMetalPress() {
        super(LINES, SPEED);
    }

    @Override
    protected String getBlockName() {
        return "elite_metal_press";
    }
}
