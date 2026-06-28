package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class RegistryHandler {

    private RegistryHandler() {}

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModFluids.OIL_BLOCK);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_ORE);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_BLOCK);
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModFluids.OIL_BUCKET);
        event.getRegistry().register(ModItems.TUNGSTEN_CARBIDE_INGOT);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_ORE_ITEM);
        event.getRegistry().register(ModBlocks.TUNGSTEN_CARBIDE_BLOCK_ITEM);
    }

    // 30000 тиков = 150 предметов за ведро (~2.3 стака), лава = 20000 тиков (~1.56 стака)
    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModFluids.OIL_BUCKET) {
            event.setBurnTime(30000);
        }
    }
}