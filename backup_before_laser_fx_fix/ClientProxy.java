package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.item.Item;
import com.TGaddon.techgunsexpanded.rockets.HighPowerRocketProjectile;
import com.TGaddon.techgunsexpanded.rockets.HighPowerRocketProjectileHV;
import com.TGaddon.techgunsexpanded.rockets.HighPowerGuidedMissile;
import com.TGaddon.techgunsexpanded.rockets.HighPowerGuidedMissileHV;
import com.TGaddon.techgunsexpanded.rockets.ModRockets;
import com.TGaddon.techgunsexpanded.rockets.client.RenderHighPowerRocket;
import net.minecraft.util.ResourceLocation;
import techguns.client.models.projectiles.ModelRocket;
import techguns.client.render.ItemRenderHack;
import techguns.client.render.item.RenderItemBase;
import techguns.client.render.item.RenderItemBaseRocketItem;
import com.TGaddon.techgunsexpanded.equipment.ModEquipment;
import techguns.client.models.armor.ModelAntiGravPack;
import techguns.client.render.AdditionalSlotRenderRegistry;
import techguns.client.render.RenderAdditionalSlotItem;
import com.TGaddon.techgunsexpanded.tools.ModTools;
import techguns.client.models.guns.ModelLasergun;
import techguns.client.render.item.RenderGunBase90;
import techguns.client.render.fx.IScreenEffect;

@Mod.EventBusSubscriber(
        modid = TechgunsExpanded.MODID,
        value = Side.CLIENT
)
public final class ClientProxy {

    private ClientProxy() {
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {

        // Entity renderers for the high-power rockets (reuse Techguns' rocket model
        // via our RenderHighPowerRocket, each with its own recolored texture).
        RenderingRegistry.registerEntityRenderingHandler(HighPowerRocketProjectile.class,
                manager -> new RenderHighPowerRocket(manager, ModRockets.TEX_HP));
        RenderingRegistry.registerEntityRenderingHandler(HighPowerRocketProjectileHV.class,
                manager -> new RenderHighPowerRocket(manager, ModRockets.TEX_HP_HV));

        // Same rockets fired from the Guided Missile Launcher / Grim Reaper.
        // Techguns renders its guided missiles with the same ModelRocket, so the
        // renderer (and the textures) are shared.
        RenderingRegistry.registerEntityRenderingHandler(HighPowerGuidedMissile.class,
                manager -> new RenderHighPowerRocket(manager, ModRockets.TEX_HP));
        RenderingRegistry.registerEntityRenderingHandler(HighPowerGuidedMissileHV.class,
                manager -> new RenderHighPowerRocket(manager, ModRockets.TEX_HP_HV));

        // 3D inventory render for the rocket ammo. Techguns does not use the JSON
        // model for its rockets: it registers an IItemRenderer in ItemRenderHack,
        // which its coremod calls from vanilla's RenderItem for any item that
        // implements IItemTGRenderer. These are the same model, scales and
        // translations Techguns uses for its own rockets - only the texture
        // differs, and it is the very texture the flying projectile already uses.
        ItemRenderHack.registerItemRenderer(ModRockets.AMMO_HP,
                rocketItemRenderer(ModRockets.TEX_HP));
        ItemRenderHack.registerItemRenderer(ModRockets.AMMO_HP_HV,
                rocketItemRenderer(ModRockets.TEX_HP_HV));

        // Back-slot model for the Anti-Gravity Jetpack. Techguns draws whatever
        // sits in a TG special slot through TGLayerRendererer, which looks the
        // item up in AdditionalSlotRenderRegistry - so reusing its anti-grav pack
        // model and skin is all that is needed.
        AdditionalSlotRenderRegistry.register(ModEquipment.ANTIGRAV_JETPACK,
                new RenderAdditionalSlotItem(new ModelAntiGravPack(),
                        ModEquipment.ARMOR_TEX_DOMAIN,
                        ModEquipment.ARMOR_TEX_PATH,
                        ModEquipment.ARMOR_CAMO_COUNT));

        ModelLoader.setCustomModelResourceLocation(ModEquipment.ANTIGRAV_JETPACK, 0,
                new ModelResourceLocation(ModEquipment.ANTIGRAV_JETPACK.getRegistryName(), "inventory"));

        // Laser Drill - placeholder visuals: the Laser Rifle's model, skin and
        // render transforms, straight from Techguns' own registration.
        ItemRenderHack.registerItemRenderer(ModTools.LASER_DRILL,
                new RenderGunBase90(new ModelLasergun(), 1)
                        .setBaseTranslation(0.25F, -0.2F, -0.06875F)
                        .setBaseScale(1.1F)
                        .setGUIScale(0.4F)
                        // The red muzzle flash: same screen effect and offsets
                        // Techguns gives the Laser Rifle. Without this the gun
                        // fires with no visible flash at all.
                        .setMuzzleFx(IScreenEffect.muzzleFlashLaser, 0.0F, 0.3F, -1.06F, 0.5F, 0.0F)
                        .setMuzzleFXPos3P(-0.06F, -0.45F));

        ModelLoader.setCustomModelResourceLocation(ModTools.LASER_DRILL, 0,
                new ModelResourceLocation(ModTools.LASER_DRILL.getRegistryName(), "inventory"));

        // Fallback JSON models (used if the render hack is unavailable).
        ModelLoader.setCustomModelResourceLocation(ModRockets.AMMO_HP, 0,
                new ModelResourceLocation(ModRockets.AMMO_HP.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModRockets.AMMO_HP_HV, 0,
                new ModelResourceLocation(ModRockets.AMMO_HP_HV.getRegistryName(), "inventory"));


        ModelLoader.setCustomModelResourceLocation(
                ModFluids.OIL_BUCKET, 0,
                new ModelResourceLocation(ModFluids.OIL_BUCKET.getRegistryName(), "inventory")
        );


        ModelLoader.setCustomStateMapper(
                ModFluids.OIL_BLOCK,
                new StateMapperBase() {
                    @Override
                    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                        return new ModelResourceLocation(TechgunsExpanded.MODID + ":oil", "fluid");
                    }
                }
        );


        ModelLoader.setCustomModelResourceLocation(
                ModItems.TUNGSTEN_CARBIDE_INGOT, 0,
                new ModelResourceLocation(ModItems.TUNGSTEN_CARBIDE_INGOT.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.DIAMOND_WIRE, 0,
                new ModelResourceLocation(ModItems.DIAMOND_WIRE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.TITANIUM_SUPERWIRE, 0,
                new ModelResourceLocation(ModItems.TITANIUM_SUPERWIRE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.COOLING_LIQUID_BOTTLE, 0,
                new ModelResourceLocation(ModItems.COOLING_LIQUID_BOTTLE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.ANODA_SUPERWIRE, 0,
                new ModelResourceLocation(ModItems.ANODA_SUPERWIRE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.ULTRATANIUM_INGOT, 0,
                new ModelResourceLocation(ModItems.ULTRATANIUM_INGOT.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.THUMB_TUNGSTEN_ORE, 0,
                new ModelResourceLocation(ModItems.THUMB_TUNGSTEN_ORE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.NETHER_ARMOR_PLATING, 0,
                new ModelResourceLocation(ModItems.NETHER_ARMOR_PLATING.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.ADVANCED_CYBERNETIC_PARTS, 0,
                new ModelResourceLocation(ModItems.ADVANCED_CYBERNETIC_PARTS.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.POWER_ARMOR_PLATING_MK2, 0,
                new ModelResourceLocation(ModItems.POWER_ARMOR_PLATING_MK2.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.ELITE_CYBERNETIC_PARTS, 0,
                new ModelResourceLocation(ModItems.ELITE_CYBERNETIC_PARTS.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.MECHANICAL_PARTS_TITAN, 0,
                new ModelResourceLocation(ModItems.MECHANICAL_PARTS_TITAN.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.TUNGSTEN_CARBIDE_PLATE, 0,
                new ModelResourceLocation(ModItems.TUNGSTEN_CARBIDE_PLATE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.MECHANICAL_PARTS_TUNGSTEN_CARBIDE, 0,
                new ModelResourceLocation(ModItems.MECHANICAL_PARTS_TUNGSTEN_CARBIDE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.OVERHEATED_BLAZE_ROD, 0,
                new ModelResourceLocation(ModItems.OVERHEATED_BLAZE_ROD.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.CARBON_FIBER_WING, 0,
                new ModelResourceLocation(ModItems.CARBON_FIBER_WING.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.ROCKET_FAIRING, 0,
                new ModelResourceLocation(ModItems.ROCKET_FAIRING.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                ModItems.ROCKET_ENGINE, 0,
                new ModelResourceLocation(ModItems.ROCKET_ENGINE.getRegistryName(), "inventory")
        );


        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.TUNGSTEN_CARBIDE_BLOCK), 0,
                new ModelResourceLocation(ModBlocks.TUNGSTEN_CARBIDE_BLOCK.getRegistryName(), "inventory")
        );


        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.TUNGSTEN_CARBIDE_ORE), 0,
                new ModelResourceLocation(ModBlocks.TUNGSTEN_CARBIDE_ORE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR_MK2), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR_MK2.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR_MK3), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR_MK3.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR_MK4), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR_MK4.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR_MK5), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR_MK5.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR_MK6), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR_MK6.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.FUEL_GENERATOR_CREATIVE), 0,
                new ModelResourceLocation(ModBlocks.FUEL_GENERATOR_CREATIVE.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.ENRICHED_URANIUM_BLOCK), 0,
                new ModelResourceLocation(ModBlocks.ENRICHED_URANIUM_BLOCK.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.CREATIVE_MECHANISM_BLOCK), 0,
                new ModelResourceLocation(ModBlocks.CREATIVE_MECHANISM_BLOCK.getRegistryName(), "inventory")
        );

        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(ModBlocks.ADVANCED_METAL_PRESS), 0,
                new ModelResourceLocation(ModBlocks.ADVANCED_METAL_PRESS.getRegistryName(), "inventory")
        );
    }

    /**
     * Builds a rocket item renderer with Techguns' own rocket settings
     * (ClientProxy: base scale 1.5, GUI scale 0.5, base translation 0/0/0.1,
     * first-person scale 0.35, and the per-transform offsets below).
     */
    private static RenderItemBase rocketItemRenderer(ResourceLocation texture) {
        float[][] transformTranslations = new float[][] {
                { 0.0F,  0.0F,  0.0F  },
                { 0.0F, -0.1F,  0.02F },
                { 0.0F,  0.0F,  0.0F  },
                { 0.0F,  0.0F,  0.0F  },
                { 0.0F,  0.0F,  0.0F  }
        };

        return new RenderItemBaseRocketItem(new ModelRocket(), texture)
                .setBaseScale(1.5F)
                .setGUIScale(0.5F)
                .setBaseTranslation(0.0F, 0.0F, 0.1F)
                .setTransformTranslations(transformTranslations)
                .setFirstPersonScale(0.35F);
    }
}
