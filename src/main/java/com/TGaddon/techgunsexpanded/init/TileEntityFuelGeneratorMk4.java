package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk4 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK4 = 105; // 30 * 3.5

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK4;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk4";
    }
}
