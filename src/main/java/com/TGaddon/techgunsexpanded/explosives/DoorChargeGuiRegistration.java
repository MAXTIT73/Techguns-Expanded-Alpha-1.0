package com.TGaddon.techgunsexpanded.explosives;

import techguns.Techguns;
import techguns.gui.containers.ExplosiveChargeContainer;

/**
 * Adds the Door Charge to Techguns' machine window handler, so right-clicking
 * it opens its window (TechgunsGuiHandler finds the window by the tile entity
 * class; the container is the Mining Charge's). Called in postInit, after
 * Techguns has added its own windows, on both sides: the window ids are
 * handed out in order, so client and server must add the same entries.
 */
public final class DoorChargeGuiRegistration {

    private DoorChargeGuiRegistration() {
    }

    /** Dedicated server: the container only. */
    public static void registerServer() {
        Techguns.proxy.getGuihandlers().<TileDoorCharge>addEntry(TileDoorCharge.class,
                ExplosiveChargeContainer<TileDoorCharge>::new);
    }

    /** Client (also the single-player server): window and container. */
    public static void registerClient() {
        Techguns.proxy.getGuihandlers().<TileDoorCharge>addEntry(TileDoorCharge.class,
                GuiDoorCharge::new, ExplosiveChargeContainer<TileDoorCharge>::new);
    }
}
