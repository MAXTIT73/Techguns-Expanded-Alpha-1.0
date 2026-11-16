package com.TGaddon.techgunsexpanded.ammo;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import techguns.TGuns;
import techguns.items.guns.IProjectileFactory;
import techguns.items.guns.ProjectileSelector;
import techguns.items.guns.ammo.AmmoTypes;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Breaching Shotgun Shells: a new variant of Techguns' shotgun ammo, next to
 * "default" and "incendiary".
 *
 * Techguns picks a gun's projectile by its ammo variant: the variant is a key
 * on the gun (switched by crafting the gun with the shells - the
 * techguns:ammo_change_crafting recipes in our recipes folder), the reload
 * takes that variant's item, and the gun's ProjectileSelector gives that
 * variant's projectile factory. So this adds the variant to the shotgun ammo
 * type and its factory to the shotgun selector (TGuns.SHOTGUN_PROJECTILES:
 * Sawed-Off and Combat Shotgun). The selector's table is not public, hence
 * the reflection. Runs in our preInit, after Techguns' (it built both).
 */
public final class BreachingAmmo {

    public static final String VARIANT = "breaching";

    public static Item BREACHING_SHELLS;

    private BreachingAmmo() {
    }

    /** preInit: the item (registered by RegistryHandler), the variant and the projectile. */
    public static void init() {
        BREACHING_SHELLS = new ItemBreachingShells();

        AmmoTypes.SHOTGUN_ROUNDS.addVariant(VARIANT, new ItemStack(BREACHING_SHELLS));
        addFactory(TGuns.SHOTGUN_PROJECTILES, VARIANT, new BreachingShotgunProjectile.Factory());

        EntityRegistry.registerModEntity(
                new ResourceLocation(TechgunsExpanded.MODID, "breaching_shotgun_pellet"),
                BreachingShotgunProjectile.class, "breaching_shotgun_pellet", 6,
                TechgunsExpanded.instance, 128, 1, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addFactory(ProjectileSelector selector, String variant, IProjectileFactory factory) {
        try {
            Field field = ProjectileSelector.class.getDeclaredField("map");
            field.setAccessible(true);
            ((Map) field.get(selector)).put(variant, factory);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Techguns Expanded: cannot add the breaching shells to the shotguns", e);
        }
    }
}
