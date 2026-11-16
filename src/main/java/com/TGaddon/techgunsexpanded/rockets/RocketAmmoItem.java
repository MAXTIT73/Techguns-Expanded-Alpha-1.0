package com.TGaddon.techgunsexpanded.rockets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import techguns.api.render.IItemTGRenderer;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;

/**
 * Ammo item for the rocket launchers.
 *
 * ITGSpecialSlot: Techguns recognises a stack as ammo ONLY if its item implements
 * this and reports AMMOSLOT. The ammo-switch crafting recipe
 * (techguns:ammo_change_crafting) uses exactly that test to find which grid slot
 * holds the ammo; a plain Item is never found, the lookup falls back to slot 0
 * (the gun itself) and the crafted gun ends up with ammovariant = "default".
 *
 * IItemTGRenderer: marks the item for Techguns' item render hack, so it is drawn
 * as the 3D rocket model instead of the flat JSON model - the same way Techguns'
 * own rockets are drawn. The renderer itself is registered client-side in
 * ClientProxy via ItemRenderHack. Both interface methods default to true, which
 * is what we want (the model is a ModelBase), so nothing is overridden here.
 * If no renderer is registered, the hack falls back to the JSON model.
 */
public class RocketAmmoItem extends Item implements ITGSpecialSlot, IItemTGRenderer {

    @Override
    public TGSlotType getSlot(ItemStack stack) {
        return TGSlotType.AMMOSLOT;
    }
}
