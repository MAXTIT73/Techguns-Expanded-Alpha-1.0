package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk6 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK6 = 300; // 30 * 10

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK6;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk6";
    }
}
