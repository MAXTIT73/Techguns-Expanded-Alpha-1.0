package com.TGaddon.techgunsexpanded.equipment;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import techguns.items.additionalslots.ItemAntiGravPack;
import techguns.items.additionalslots.ItemGlider;

/**
 * Anti-Gravity Jetpack: the Anti-Gravity Device plus gliding.
 *
 * Everything the anti-grav pack does is inherited unchanged - it goes in the
 * BACKSLOT, runs on Nuclear Power Cells, grants CREATIVE_FLIGHT and +25% FLYSPEED
 * while it has charge, burns one point of charge per tick of actual flight and
 * reloads itself from the player's inventory when empty.
 *
 * The only addition is the glide: ItemGlider.glide() is a public static helper,
 * so the glider's behaviour (sneak while falling faster than 0.1 b/t -> the fall
 * is slowed and converted into forward motion along the look vector) can simply
 * be run on top, exactly as ItemGlider itself does in its own tick.
 */
public class ItemAntiGravJetpack extends ItemAntiGravPack {

    public ItemAntiGravJetpack(String name, int camoCount, int maxCharge) {
        super(name, camoCount, maxCharge);
    }

    @Override
    public void onPlayerTick(ItemStack stack, TickEvent.PlayerTickEvent event) {
        super.onPlayerTick(stack, event);
        ItemGlider.glide(event.player);
    }
}
