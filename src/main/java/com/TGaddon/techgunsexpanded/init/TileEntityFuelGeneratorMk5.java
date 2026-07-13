package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk5 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK5 = 150; // 30 * 5

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK5;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk5";
    }
}
