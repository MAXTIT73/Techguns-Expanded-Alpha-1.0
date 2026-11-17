package com.TGaddon.techgunsexpanded.init;

/** Advanced Metal Press (tier 2): 2 lines, 1.5x speed (BlockMultiLineMetalPress). */
public class BlockAdvancedMetalPress extends BlockMultiLineMetalPress {

    public BlockAdvancedMetalPress() {
        super("advanced_metal_press", TileEntityAdvancedMetalPress::new, GuiHandler.ADVANCED_METAL_PRESS);
    }
}
