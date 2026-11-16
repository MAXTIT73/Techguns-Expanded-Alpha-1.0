package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import techguns.TGuns;
import techguns.items.guns.GenericGunMeleeCharge;

/**
 * Techguns' Mining Drill with the carbon drill head digs 1.5 times faster.
 *
 * The "carbon drill" is not an item of its own: it is TGuns.miningdrill with
 * miningHead = 1 in its NBT (the head is put in by a crafting recipe). Techguns
 * gives the head only +2 dig speed (MiningDrill.getExtraDigSpeed: 14 -> 16).
 * That code is Techguns', so the speed is raised here instead, on the game's
 * break speed event, for that drill with that head only; the plain drill
 * (obsidian head, miningHead = 0) is left as it is.
 *
 * Only blocks the drill is the right tool for are sped up, like the drill's own
 * dig speed.
 */
@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class CarbonDrillSpeed {

    /** Techguns' Mining Drill: base dig speed and the carbon head's bonus. */
    public static final float TG_DRILL_DIG_SPEED = 14.0F;
    public static final float TG_CARBON_HEAD_BONUS = 2.0F;

    /** How much faster the carbon drill digs than Techguns makes it. */
    public static final float CARBON_DRILL_MULTIPLIER = 1.5F;

    /** The carbon drill's resulting dig speed: (14 + 2) * 1.5 = 24. */
    public static final float CARBON_DRILL_DIG_SPEED =
            (TG_DRILL_DIG_SPEED + TG_CARBON_HEAD_BONUS) * CARBON_DRILL_MULTIPLIER;

    /** miningHead value of the carbon head (MiningDrill's head list: carbon is 1). */
    private static final int CARBON_HEAD_LEVEL = 1;

    private CarbonDrillSpeed() {
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || TGuns.miningdrill == null) {
            return;
        }
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != TGuns.miningdrill) {
            return;
        }
        GenericGunMeleeCharge drill = (GenericGunMeleeCharge) TGuns.miningdrill;
        if (drill.getMiningHeadLevel(stack) >= CARBON_HEAD_LEVEL
                && drill.isEffectiveToolForState(stack, event.getState())) {
            event.setNewSpeed(event.getNewSpeed() * CARBON_DRILL_MULTIPLIER);
        }
    }
}
