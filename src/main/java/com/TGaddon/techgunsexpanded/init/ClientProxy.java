package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
import techguns.client.render.item.GunAnimation;
import techguns.client.render.fx.IScreenEffect;
import techguns.client.render.fx.ScreenEffect;
import techguns.client.render.TGRenderHelper;
import techguns.client.particle.TGFX;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.TGaddon.techgunsexpanded.tools.LaserDrillBeam;
import com.TGaddon.techgunsexpanded.tools.LaserDrillShot;
import com.TGaddon.techgunsexpanded.tools.RenderLaserDrillBeam;

@Mod.EventBusSubscriber(
        modid = TechgunsExpanded.MODID,
        value = Side.CLIENT
)
public final class ClientProxy {

    private ClientProxy() {
    }

    /**
     * Laser Drill muzzle flash: Techguns' muzzleFlashLaser (same sprite sheet
     * layout: 4x4, 7 frames, additive) with the purple copy of its texture.
     * ScreenEffect's constructor only takes Techguns' own textures, so the
     * texture is swapped right after it.
     */
    private static final IScreenEffect LASER_DRILL_MUZZLE_FLASH =
            new ScreenEffect("textures/fx/laserflare02.png", 4, 4, 7, TGRenderHelper.RenderType.ADDITIVE) {
                {
                    this.fxTexture = new ResourceLocation(TechgunsExpanded.MODID, "textures/fx/laser_drill_flare.png");
                }
            };

    private static final Logger LOGGER = LogManager.getLogger(TechgunsExpanded.MODID);

    /** The drill's purple impact effect, read by Techguns' own particle loader. */
    private static final String LASER_DRILL_FX_FILE = "/assets/techguns_expanded/particles/laser_drill_fx.txt";

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

        // Laser Drill beams (shot and mining beam): purple copy of the Laser
        // Rifle's beam renderer. Their impact effect is loaded into Techguns'
        // particle list here too - Techguns has loaded its own by now (preInit).
        RenderingRegistry.registerEntityRenderingHandler(LaserDrillShot.class, RenderLaserDrillBeam::new);
        // Breaching shotgun pellets look like Techguns' normal pellets.
        RenderingRegistry.registerEntityRenderingHandler(
                com.TGaddon.techgunsexpanded.ammo.BreachingShotgunProjectile.class,
                techguns.client.render.entities.projectiles.RenderGenericProjectile::new);
        ModelLoader.setCustomModelResourceLocation(com.TGaddon.techgunsexpanded.ammo.BreachingAmmo.BREACHING_SHELLS, 0,
                new ModelResourceLocation(com.TGaddon.techgunsexpanded.ammo.BreachingAmmo.BREACHING_SHELLS.getRegistryName(), "inventory"));
        RenderingRegistry.registerEntityRenderingHandler(LaserDrillBeam.class, RenderLaserDrillBeam::new);
        try {
            if (!TGFX.loadFXListFile(LASER_DRILL_FX_FILE)) {
                LOGGER.warn("Could not load " + LASER_DRILL_FX_FILE);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Could not load " + LASER_DRILL_FX_FILE, e);
        }

        // Laser Drill - placeholder visuals: the Laser Rifle's model, skin and
        // render transforms. This is Techguns' registration of TGuns.lasergun,
        // value for value - keep it complete: the muzzle flash is drawn at fixed
        // offsets, not attached to the model, so it only sits on the muzzle when
        // the model's per-view translations (and the third-person flash
        // position) are the rifle's too.
        ItemRenderHack.registerItemRenderer(ModTools.LASER_DRILL,
                new RenderGunBase90(new ModelLasergun(), 1)
                        .setBaseTranslation(0.25F, -0.2F, -0.06875F)
                        .setBaseScale(1.1F)
                        .setGUIScale(0.4F)
                        // The muzzle flash (purple; offsets are the rifle's).
                        // Without this the gun fires with no visible flash at all.
                        .setMuzzleFx(LASER_DRILL_MUZZLE_FLASH, 0.0F, 0.3F, -1.06F, 0.5F, 0.0F)
                        .setTransformTranslations(new float[][] {
                                {  0.0F,  0.15F,  0.04F },   // first person
                                {  0.0F,  0.02F,  0.01F },   // third person
                                {  0.13F, 0.01F,  0.0F  },   // GUI
                                {  0.0F,  0.0F,   0.15F },   // ground
                                { -0.18F, 0.0F,  -0.05F }    // item frame
                        })
                        .setMuzzleFXPos3P(0.11F, -0.83F)
                        .setRecoilAnim(GunAnimation.genericRecoil, 0.2F, 5.0F));

        ModelLoader.setCustomModelResourceLocation(ModTools.LASER_DRILL, 0,
                new ModelResourceLocation(ModTools.LASER_DRILL.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.SILK_TOUCH_UPGRADE, 0,
                new ModelResourceLocation(ModTools.SILK_TOUCH_UPGRADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.MAGNET_UPGRADE, 0,
                new ModelResourceLocation(ModTools.MAGNET_UPGRADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.AUTO_SMELTING_UPGRADE, 0,
                new ModelResourceLocation(ModTools.AUTO_SMELTING_UPGRADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.FLUID_CONVERTER_UPGRADE, 0,
                new ModelResourceLocation(ModTools.FLUID_CONVERTER_UPGRADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.AREA_EXPANSION_UPGRADE, 0,
                new ModelResourceLocation(ModTools.AREA_EXPANSION_UPGRADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.ENERGY_EFFICIENCY_UPGRADE, 0,
                new ModelResourceLocation(ModTools.ENERGY_EFFICIENCY_UPGRADE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModTools.CAPACITY_UPGRADE, 0,
                new ModelResourceLocation(ModTools.CAPACITY_UPGRADE.getRegistryName(), "inventory"));

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


        // Door Charge: the item shows the block model lying flat, like Techguns' charges.
        ModelLoader.setCustomModelResourceLocation(ModBlocks.DOOR_CHARGE_ITEM, 0,
                new ModelResourceLocation(ModBlocks.DOOR_CHARGE.getRegistryName(), "armed=false,facing=down"));

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

        // Multi-line machines: the Techguns machine's model and renderer with
        // each tier's own texture (yellow, red, purple trim), for the block and
        // (through the tile entity renderer) for the item - its item model is
        // builtin/entity.
        registerMachine(ModBlocks.ADVANCED_METAL_PRESS, TileEntityAdvancedMetalPress.class,
                new techguns.client.models.machines.ModelMetalPress(), "advanced_metal_press");
        registerMachine(ModBlocks.PROFESSIONAL_METAL_PRESS, TileEntityProfessionalMetalPress.class,
                new techguns.client.models.machines.ModelMetalPress(), "professional_metal_press");
        registerMachine(ModBlocks.ELITE_METAL_PRESS, TileEntityEliteMetalPress.class,
                new techguns.client.models.machines.ModelMetalPress(), "elite_metal_press");
        registerMachine(ModBlocks.ADVANCED_AMMO_PRESS, TileEntityAdvancedAmmoPress.class,
                new techguns.client.models.machines.ModelAmmoPress(), "advanced_ammo_press");
        registerMachine(ModBlocks.PROFESSIONAL_AMMO_PRESS, TileEntityProfessionalAmmoPress.class,
                new techguns.client.models.machines.ModelAmmoPress(), "professional_ammo_press");
        registerMachine(ModBlocks.ELITE_AMMO_PRESS, TileEntityEliteAmmoPress.class,
                new techguns.client.models.machines.ModelAmmoPress(), "elite_ammo_press");
    }

    private static void registerMachine(net.minecraft.block.Block block,
                                        Class<? extends TileEntityMultiLineMachine> tile,
                                        techguns.client.models.machines.ModelMachine model, String texture) {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(tile,
                new techguns.client.render.tileentities.RenderMachine(model,
                        new ResourceLocation(TechgunsExpanded.MODID, "textures/blocks/" + texture + ".png")));
        Item item = Item.getItemFromBlock(block);
        net.minecraftforge.client.ForgeHooksClient.registerTESRItemStack(item, 0, tile);
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    /**
     * Laser Drill: keeps the left-click beam going for as long as the button is
     * held.
     *
     * The beam is fired by the server for arm swings (ItemLaserDrill.onEntitySwing).
     * The game swings the arm every tick only while a block is being mined; held on
     * thin air or on a mob it swings once, for the click. So while the attack
     * button is down on anything but a block, the swing is repeated here - the
     * server paces the beams itself.
     *
     * (Each swing also resets the attack cooldown, so holding the button on a mob
     * weakens the drill's next melee hit a little.)
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null || mc.currentScreen != null || !mc.inGameHasFocus
                || !mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        ItemStack held = player.getHeldItemMainhand();
        if (held.getItem() != ModTools.LASER_DRILL
                || ModTools.LASER_DRILL.getCurrentAmmo(held) <= 0) {
            return;
        }
        RayTraceResult target = mc.objectMouseOver;
        if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
            return;   // mining: the game swings every tick itself
        }
        player.swingArm(EnumHand.MAIN_HAND);
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
