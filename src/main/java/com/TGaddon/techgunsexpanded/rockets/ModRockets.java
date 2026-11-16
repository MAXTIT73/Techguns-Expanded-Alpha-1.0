package com.TGaddon.techgunsexpanded.rockets;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import techguns.TGuns;
import techguns.Techguns;
import techguns.items.guns.ProjectileSelector;
import techguns.items.guns.ammo.AmmoType;
import techguns.items.guns.IProjectileFactory;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Registration + wiring for the two new rocket types (High-Power and High-Power
 * High-Velocity). Entity registration runs in preInit; the ammo variants are
 * injected into Techguns' launchers at loadComplete (after Techguns has finished
 * building its rocket ammo/selectors).
 *
 * Two separate selectors are patched, because Techguns splits rocket weapons:
 *   - ROCKET_PROJECTILES        (AmmoTypes.ROCKETS)          -> Rocket Launcher
 *   - GUIDED_MISSILE_PROJECTILES(AmmoTypes.ROCKETS_NO_NUKES) -> Guided Missile
 *                                                               Launcher + Grim Reaper
 * The guided guns need their own projectile classes: GuidedMissileLauncher casts
 * the spawned entity to GuidedMissileProjectile to set the lock-on target.
 */
public final class ModRockets {

    public static final String KEY_HP    = "high_power";
    public static final String KEY_HP_HV = "high_power_hv";

    public static final ResourceLocation TEX_HP =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/guns/rocket_high_power.png");
    public static final ResourceLocation TEX_HP_HV =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/guns/rocket_high_power_hv.png");

    // Ammo items shown in the inventory / loaded into the launcher.
    public static Item AMMO_HP;
    public static Item AMMO_HP_HV;

    private ModRockets() {}

    /** preInit: create the ammo items (registered by RegistryHandler). */
    public static void createItems() {
        AMMO_HP = new RocketAmmoItem()
                .setUnlocalizedName("rocket_high_power")
                .setRegistryName(TechgunsExpanded.MODID, "rocket_high_power")
                .setMaxStackSize(64)
                .setCreativeTab(Techguns.tabTechgun);

        AMMO_HP_HV = new RocketAmmoItem()
                .setUnlocalizedName("rocket_high_power_hv")
                .setRegistryName(TechgunsExpanded.MODID, "rocket_high_power_hv")
                .setMaxStackSize(64)
                .setCreativeTab(Techguns.tabTechgun);
    }

    /** preInit: register the four projectile entities the same way Techguns does. */
    public static void registerEntities() {
        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "high_power_rocket"),
                HighPowerRocketProjectile.class, "high_power_rocket", 0,
                TechgunsExpanded.instance, 128, 1, true);

        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "high_power_rocket_hv"),
                HighPowerRocketProjectileHV.class, "high_power_rocket_hv", 1,
                TechgunsExpanded.instance, 128, 1, true);

        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "high_power_guided_missile"),
                HighPowerGuidedMissile.class, "high_power_guided_missile", 2,
                TechgunsExpanded.instance, 128, 1, true);

        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "high_power_guided_missile_hv"),
                HighPowerGuidedMissileHV.class, "high_power_guided_missile_hv", 3,
                TechgunsExpanded.instance, 128, 1, true);
    }

    /**
     * loadComplete: register the new ammo variants on both rocket AmmoTypes and
     * map each variant key to the matching projectile factory in each selector.
     */
    public static void injectVariants() {
        inject("rocketlauncher", TGuns.ROCKET_PROJECTILES,
                new HighPowerRocketFactory(), new HighPowerRocketHVFactory());

        // Guided Missile Launcher and Grim Reaper share this selector, so one
        // injection covers both guns.
        inject("guided", TGuns.GUIDED_MISSILE_PROJECTILES,
                new HighPowerGuidedMissileFactory(), new HighPowerGuidedMissileHVFactory());
    }

    @SuppressWarnings("unchecked")
    private static void inject(String label, ProjectileSelector<?> selector,
                               IProjectileFactory<?> factoryHP, IProjectileFactory<?> factoryHPHV) {
        if (selector == null || selector.ammoType == null) {
            System.out.println("[TechgunsExpanded][rockets] ABORT (" + label
                    + "): selector/ammoType not available.");
            return;
        }

        AmmoType ammoType = selector.ammoType;

        // getIDforVariantKey returns 0 ("default") for an unknown key, so a
        // non-zero id means the variant is already there - never add it twice.
        if (ammoType.getIDforVariantKey(KEY_HP) == 0) {
            ammoType.addVariant(KEY_HP, new ItemStack(AMMO_HP), new ItemStack(AMMO_HP));
        }
        if (ammoType.getIDforVariantKey(KEY_HP_HV) == 0) {
            ammoType.addVariant(KEY_HP_HV, new ItemStack(AMMO_HP_HV), new ItemStack(AMMO_HP_HV));
        }

        try {
            Field mapField = ProjectileSelector.class.getDeclaredField("map");
            mapField.setAccessible(true);
            HashMap<String, IProjectileFactory> map =
                    (HashMap<String, IProjectileFactory>) mapField.get(selector);
            map.put(KEY_HP, factoryHP);
            map.put(KEY_HP_HV, factoryHPHV);
            System.out.println("[TechgunsExpanded][rockets] " + label
                    + ": variants=" + ammoType.getVariants().size()
                    + " factories=" + map.keySet());
        } catch (Exception e) {
            System.out.println("[TechgunsExpanded][rockets] FAILED to patch selector ("
                    + label + "): " + e);
            e.printStackTrace();
        }
    }
}
