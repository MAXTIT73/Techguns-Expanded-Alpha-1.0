package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class RegistryHandler {

    private RegistryHandler() {}

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModFluids.OIL_BLOCK);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_ORE);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_BLOCK);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK2);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK3);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK4);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK5);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK6);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_CREATIVE);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModFluids.OIL_BUCKET);
        event.getRegistry().register(ModItems.TUNGSTEN_CARBIDE_INGOT);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_ORE_ITEM);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_BLOCK_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK2_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK3_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK4_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK5_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_MK6_ITEM);
        event.getRegistry().register(ModBlocks.FUEL_GENERATOR_CREATIVE_ITEM);
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        Item itemShared = Item.REGISTRY.getObject(new ResourceLocation("techguns", "itemshared"));
        if (itemShared == null) return;

        // Copper Wire = metadata 62, Mechanical Parts (Iron) = metadata 57
        GameRegistry.addShapedRecipe(
            new ResourceLocation(TechgunsExpanded.MODID, "fuel_generator"),
            null,
            new ItemStack(ModBlocks.FUEL_GENERATOR),
            "IWI",
            "MFM",
            "IWI",
            'I', Items.IRON_INGOT,
            'W', new ItemStack(itemShared, 1, 62),
            'M', new ItemStack(itemShared, 1, 57),
            'F', Blocks.FURNACE
        );
    }

    // 30000 тиков = 150 предметов за ведро (~2.3 стака), лава = 20000 тиков (~1.56 стака)
    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModFluids.OIL_BUCKET) {
            event.setBurnTime(30000);
        }
    }
}