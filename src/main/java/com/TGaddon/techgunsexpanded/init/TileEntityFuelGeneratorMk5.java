package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk5 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK5 = 1125; // 375 * 3 (v1.3 rebalance)

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK5;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk5";
    }
}
