package com.TGaddon.techgunsexpanded.init;

public class TileEntityFuelGeneratorMk2 extends TileEntityFuelGenerator {

    public static final int RF_PER_TICK_MK2 = 45; // 30 * 1.5

    @Override
    protected int getRfPerTick() {
        return RF_PER_TICK_MK2;
    }

    @Override
    public String getGuiTitle() {
        return "Fuel Generator Mk2";
    }
}
