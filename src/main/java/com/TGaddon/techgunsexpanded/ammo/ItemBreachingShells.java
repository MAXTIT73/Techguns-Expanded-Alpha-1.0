package com.TGaddon.techgunsexpanded.ammo;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import techguns.Techguns;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;

/**
 * Breaching Shotgun Shells: a third kind of shotgun ammo next to Techguns'
 * normal and incendiary shells (BreachingAmmo). An ammo-slot item like
 * Techguns' shells: it fits Techguns' ammo slots, and the ammo-change crafting
 * recipe (shotgun + shell) recognises it by that slot type.
 */
public class ItemBreachingShells extends Item implements ITGSpecialSlot {

    public static final String NAME = "shotgunrounds_breaching";

    public ItemBreachingShells() {
        this.setRegistryName(TechgunsExpanded.MODID, NAME);
        this.setUnlocalizedName(TechgunsExpanded.MODID + "." + NAME);
        this.setCreativeTab(Techguns.tabTechgun);
    }

    @Override
    public TGSlotType getSlot(ItemStack item) {
        return TGSlotType.AMMOSLOT;
    }
}
