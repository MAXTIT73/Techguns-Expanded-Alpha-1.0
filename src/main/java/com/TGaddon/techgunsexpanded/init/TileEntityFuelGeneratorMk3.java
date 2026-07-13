package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk3 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK3 = 66; // 30 * 2.2

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK3;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk3";
    }
}
