package com.TGaddon.techgunsexpanded.init;

/** Professional Metal Press (tier 3): 3 lines, 2x speed (BlockMultiLineMetalPress). */
public class BlockProfessionalMetalPress extends BlockMultiLineMetalPress {

    public BlockProfessionalMetalPress() {
        super("professional_metal_press", TileEntityProfessionalMetalPress::new,
                GuiHandler.PROFESSIONAL_METAL_PRESS);
    }
}
