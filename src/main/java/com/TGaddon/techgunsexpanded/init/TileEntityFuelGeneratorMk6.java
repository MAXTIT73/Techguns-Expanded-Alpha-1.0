package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk6 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK6 = 2250; // 750 * 3 (v1.3 rebalance)

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK6;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk6";
    }
}
