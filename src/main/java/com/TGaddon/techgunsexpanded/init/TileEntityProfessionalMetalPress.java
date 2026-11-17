package com.TGaddon.techgunsexpanded.init;

/** Professional Metal Press (tier 3): 3 lines, 2x the Metal Press's speed. */
public class TileEntityProfessionalMetalPress extends TileEntityMultiLineMetalPress {

    public static final int LINES = 3;
    public static final float SPEED = 2.0F;

    public TileEntityProfessionalMetalPress() {
        super(LINES, SPEED);
    }

    @Override
    protected String getBlockName() {
        return "professional_metal_press";
    }
}
