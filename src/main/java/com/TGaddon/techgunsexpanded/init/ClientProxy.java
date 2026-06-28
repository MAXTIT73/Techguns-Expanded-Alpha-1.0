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
    }}
