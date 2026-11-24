package com.TGaddon.techgunsexpanded.jei;

import com.TGaddon.techgunsexpanded.init.AmmoPressLayout;
import com.TGaddon.techgunsexpanded.init.GuiAdvancedAmmoPress;
import com.TGaddon.techgunsexpanded.init.GuiAdvancedMetalPress;
import com.TGaddon.techgunsexpanded.init.GuiEliteAmmoPress;
import com.TGaddon.techgunsexpanded.init.GuiProfessionalAmmoPress;
import com.TGaddon.techgunsexpanded.init.GuiEliteMetalPress;
import com.TGaddon.techgunsexpanded.init.GuiProfessionalMetalPress;
import com.TGaddon.techgunsexpanded.init.MachineLayout;
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
 * Multi-line machines use the Techguns machine's recipes, so they hook into
 * Techguns' own JEI categories ("techguns.metalpress", "techguns.ammopress"):
 *  - clicking a line's progress arrow shows the machine's recipes, as on the
 *    Techguns machine (Techguns registers that area for its own windows only;
 *    JEI matches the window's exact class);
 *  - the machines are listed as machines for those recipes.
 */
@JEIPlugin
public class TGEJeiPlugin implements IModPlugin {

    /** Techguns' Metal Press recipe category (TGJeiPlugin.METAL_PRESS). */
    public static final String TECHGUNS_METAL_PRESS = "techguns.metalpress";
    /** Techguns' Ammo Press recipe category (TGJeiPlugin.AMMO_PRESS). */
    public static final String TECHGUNS_AMMO_PRESS = "techguns.ammopress";

    @Override
    public void register(IModRegistry registry) {
        addArrows(registry, GuiAdvancedMetalPress.class, MetalPressLayout.ADVANCED, TECHGUNS_METAL_PRESS);
        addArrows(registry, GuiProfessionalMetalPress.class, MetalPressLayout.PROFESSIONAL, TECHGUNS_METAL_PRESS);
        addArrows(registry, GuiEliteMetalPress.class, MetalPressLayout.ELITE, TECHGUNS_METAL_PRESS);
        addArrows(registry, GuiAdvancedAmmoPress.class, AmmoPressLayout.ADVANCED, TECHGUNS_AMMO_PRESS);
        addArrows(registry, GuiProfessionalAmmoPress.class, AmmoPressLayout.PROFESSIONAL, TECHGUNS_AMMO_PRESS);
        addArrows(registry, GuiEliteAmmoPress.class, AmmoPressLayout.ELITE, TECHGUNS_AMMO_PRESS);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_METAL_PRESS), TECHGUNS_METAL_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.PROFESSIONAL_METAL_PRESS), TECHGUNS_METAL_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ELITE_METAL_PRESS), TECHGUNS_METAL_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_AMMO_PRESS), TECHGUNS_AMMO_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.PROFESSIONAL_AMMO_PRESS), TECHGUNS_AMMO_PRESS);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.ELITE_AMMO_PRESS), TECHGUNS_AMMO_PRESS);
    }

    /** Each line's progress arrow opens the machine's recipes, as on the Techguns machine. */
    private static void addArrows(IModRegistry registry, Class<? extends GuiContainer> gui,
                                  MachineLayout layout, String category) {
        for (int i = 0; i < layout.lineX.length; i++) {
            registry.addRecipeClickArea(gui, layout.lineX[i] + layout.arrowDX, layout.lineY[i] + layout.arrowDY,
                    MachineLayout.ARROW_W, MachineLayout.ARROW_CLICK_H, category);
        }
    }
}
