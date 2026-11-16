package com.TGaddon.techgunsexpanded.combat;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Mobs killed by energy weapons (Laser Rifle, Laser Pistol, the blasters, the
 * Laser Drill's shot - everything that deals Techguns' ENERGY damage type)
 * drop cooked meat, exactly as if they had died burning.
 *
 * How: the mob is set on fire at the moment it dies. LivingDeathEvent is fired
 * from onDeath before the loot is dropped, and the loot tables decide raw or
 * cooked by the "on_fire" condition, i.e. entity.isBurning(). So the game cooks
 * the drops itself, by the same rules as for a mob that burned to death -
 * including modded mobs that use that condition, and nothing else (a zombie's
 * potato stays a potato, as it does in a fire).
 *
 * The body also visibly burns for the second before it disappears.
 */
@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class EnergyKillCookingHandler {

    /**
     * Damage source name of Techguns' energy weapons
     * (TGDamageSource.causeEnergyDamage - the only way they create ENERGY damage).
     *
     * The name is used instead of TGDamageSource's damageType field: that field
     * shadows vanilla's DamageSource.damageType and is obfuscated together with
     * it, so it cannot be read reliably from here.
     */
    private static final String ENERGY_DAMAGE = "tg_energy";

    /** Burn time in seconds; only has to cover the moment the loot is rolled. */
    private static final int BURN_SECONDS = 1;

    private EnergyKillCookingHandler() {
    }

    /** LOW, so mods that cancel the death (revive items etc.) are seen first. */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || entity instanceof EntityPlayer
                || !ENERGY_DAMAGE.equals(event.getSource().getDamageType())) {
            return;
        }
        if (!entity.isBurning()) {
            entity.setFire(BURN_SECONDS);
        }
    }
}
