package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.item.Item;

@Mod.EventBusSubscriber(
        modid = TechgunsExpanded.MODID,
        value = Side.CLIENT
)
public final class ClientProxy {

    private ClientProxy() {
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {


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
    }}
