package com.TGaddon.techgunsexpanded.equipment;

import net.minecraft.item.Item;
import techguns.TGItems;

/**
 * Called from Techguns' HUD code (patched in by TGEClassTransformer).
 *
 * TGGuiEvents.onRenderGameOverlyEvent draws the back-slot charge line
 * ("<cells in inventory>x <charge>%", bottom right) only when the back-slot item
 * is one of three hard-coded instances:
 *     item == JETPACK || item == JUMPPACK || item == ANTI_GRAV_PACK
 * The transformer replaces the last comparison with a call to this method, so our
 * packs get the very same line. Everything drawn after the check (the icon,
 * the fuel item via ItemTGSpecialSlotAmmo.getAmmo(), the charge percentage and
 * the vertical stacking with the other HUD lines) stays Techguns' own code.
 */
public final class JetpackHudHooks {

    private JetpackHudHooks() {}

    public static boolean showsChargeHud(Item item) {
        return item == TGItems.ANTI_GRAV_PACK || item instanceof ItemAntiGravJetpack;
    }
}
