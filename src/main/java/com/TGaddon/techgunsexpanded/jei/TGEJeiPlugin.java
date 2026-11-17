package com.TGaddon.techgunsexpanded.jei;

import com.TGaddon.techgunsexpanded.init.GuiAdvancedMetalPress;
import com.TGaddon.techgunsexpanded.init.GuiEliteMetalPress;
import com.TGaddon.techgunsexpanded.init.GuiProfessionalMetalPress;
import com.TGaddon.techgunsexpanded.init.MetalPressLayout;
import com.TGaddon.techgunsexpanded.init.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

/**
 * JEI support for this mod's machines (found by JEI through @JEIPlugin; only
 * loaded when JEI is installed).
 *
 * Multi-line Metal Presses use Techguns' Metal Press recipes, so they hook
 * into Techguns' own JEI category ("techguns.metalpress"):
 *  - clicking a line's progress arrow shows the Metal Press recipes, as on
 *    Techguns' Metal Press (Techguns registers that area for its own window
 *    only; JEI matches the window's exact class);
 *  - the presses are listed as machines for those recipes.
 */
@JEIPlugin
public class TGEJeiPlugin implements IModPlugin {

    /** Techguns' Metal Press recipe category (TGJeiPlugin.METAL_PRESS). */
    public static final String TECHGUNS_METAL_PRESS = "techguns.metalpress";

    /** Techguns' Metal Press arrow click area, relative to the first input slot of a line. */
    private static final int ARROW_DX = 9;
    private static final int ARROW_DY = 19;
    private static final int ARROW_W = 19;
    private static final int ARROW_H = 22;

    @Override
    public void register(IModRegistry registry) {
        addArrows(registry, GuiAdvancedMetalPress.class, MetalPressLayout.ADVANCED);
        addArrows(registry, GuiProfessionalMetalPress.class, MetalPressLayout.PROFESSIONAL);
        addArrows(registry, GuiEliteMetalPress.class, MetalPressLayout.ELITE);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_METAL_PRESS), TECHGUNS_METAL_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.PROFESSIONAL_METAL_PRESS), TECHGUNS_METAL_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ELITE_METAL_PRESS), TECHGUNS_METAL_PRESS);
    }

    private static void addArrows(IModRegistry registry, Class<? extends GuiContainer> gui,
                                  MetalPressLayout layout) {
        for (int i = 0; i < layout.lineX.length; i++) {
            registry.addRecipeClickArea(gui, layout.lineX[i] + ARROW_DX, layout.lineY[i] + ARROW_DY,
                    ARROW_W, ARROW_H, TECHGUNS_METAL_PRESS);
        }
    }
}
