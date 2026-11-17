package com.TGaddon.techgunsexpanded.init;

/** Elite Metal Press (tier 4): 4 lines, 3x speed (BlockMultiLineMetalPress). */
public class BlockEliteMetalPress extends BlockMultiLineMetalPress {

    public BlockEliteMetalPress() {
        super("elite_metal_press", TileEntityEliteMetalPress::new, GuiHandler.ELITE_METAL_PRESS);
    }
}
