package com.TGaddon.techgunsexpanded.init;

import techguns.tileentities.MetalPressTileEnt;

public class TileEntityAdvancedMetalPress extends MetalPressTileEnt {

    public TileEntityAdvancedMetalPress() {
        super();
    }

    @Override
    protected void checkAndStartOperation() {
        super.checkAndStartOperation();
        // Make 1.5x faster: reduce totaltime from 100 to ~67
        if (this.currentOperation != null && this.totaltime > 0) {
            this.totaltime = Math.round(this.totaltime / 1.5f);
        }
    }
}
