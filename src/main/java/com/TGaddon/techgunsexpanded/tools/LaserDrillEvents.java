package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Laser Drill events:
 *  - no digging while the drill is shooting (right click). ItemLaserDrill
 *    already stops the left-click beam while shooting; digging is the game's
 *    own block breaking, so it is stopped here (client and server);
 *  - auto smelting: drops of blocks broken with the drill are replaced by
 *    their furnace result; it wins over silk touch (autoSmelt) (server);
 *  - magnet (upgrade): those drops go straight into the player's inventory,
 *    whatever does not fit drops as usual (server);
 *  - no sneak silk touch: Techguns gives every MiningDrill - this one too -
 *    silk touch drops and no XP while the player sneaks (TGEventHandler
 *    onBlockDrops / onBlockBreakEvent). This drill has silk touch as a setting
 *    instead, and sneaking (e.g. going down with a jetpack) must not change how
 *    it digs, so drops and XP are put back to what they were before Techguns
 *    changed them (server).
 */
@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class LaserDrillEvents {

    private LaserDrillEvents() {
    }

    /** Survival digging: no progress on the block while shooting. */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (isShootingWithDrill(event.getEntityPlayer())) {
            event.setNewSpeed(0.0F);
        }
    }

    /** Creative breaks a block on the click itself, without break speed: cancel the click. */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (isShootingWithDrill(event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    /** Drops and XP as they were before Techguns' sneak handlers ran; same thread, same event. */
    private static final ThreadLocal<Object[]> DROPS_BEFORE = new ThreadLocal<Object[]>();
    private static final ThreadLocal<Object[]> XP_BEFORE = new ThreadLocal<Object[]>();

    /** First of all handlers: remember the drops while sneaking with the drill. */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rememberDrops(BlockEvent.HarvestDropsEvent event) {
        if (isSneakingWithDrill(event.getHarvester())) {
            DROPS_BEFORE.set(new Object[] {event,
                    new java.util.ArrayList<ItemStack>(event.getDrops()), event.getDropChance()});
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rememberXp(BlockEvent.BreakEvent event) {
        if (isSneakingWithDrill(event.getPlayer())) {
            XP_BEFORE.set(new Object[] {event, event.getExpToDrop()});
        }
    }

    /** Last of all handlers: undo Techguns' "no XP while sneaking". */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void restoreXp(BlockEvent.BreakEvent event) {
        Object[] saved = XP_BEFORE.get();
        XP_BEFORE.remove();
        if (saved != null && saved[0] == event) {
            event.setExpToDrop((Integer) saved[1]);
        }
    }

    private static boolean isSneakingWithDrill(EntityPlayer player) {
        return player != null && player.isSneaking()
                && player.getHeldItemMainhand().getItem() == ModTools.LASER_DRILL;
    }

    /**
     * Last of all handlers: first undo Techguns' sneak silk touch (see the class
     * comment), then auto smelting.
     *
     * Auto smelting. Fired for every block the drill breaks - the one hit and
     * the rest of the area (Block.harvestBlock records the player as harvester).
     * Each drop with a furnace recipe becomes its result: ore -> ingot, sand ->
     * glass, cobblestone -> stone... Drops without a recipe stay as they are.
     * With silk touch on as well, see autoSmelt.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        Object[] saved = DROPS_BEFORE.get();
        DROPS_BEFORE.remove();
        if (saved != null && saved[0] == event) {
            @SuppressWarnings("unchecked")
            java.util.List<ItemStack> before = (java.util.List<ItemStack>) saved[1];
            event.getDrops().clear();
            event.getDrops().addAll(before);
            event.setDropChance((Float) saved[2]);
        }

        EntityPlayer player = event.getHarvester();
        if (player == null || event.getWorld().isRemote) {
            return;
        }
        ItemStack held = player.getHeldItemMainhand();
        if (held.getItem() != ModTools.LASER_DRILL) {
            return;
        }
        if (LaserDrillSettings.isOn(held, LaserDrillSettings.AUTO_SMELTING)) {
            autoSmelt(event);
        }
        // After smelting, so the inventory gets the ingots, not the ore.
        if (LaserDrillUpgrades.has(held, LaserDrillUpgrades.MAGNET)) {
            pullIntoInventory(event, player);
        }
    }

    /**
     * Auto smelting, which wins over silk touch. Without silk touch: the drops
     * are smelted (smelt). With silk touch (the event's drops are then the
     * silk touch ones): if the block's ordinary drops can be smelted, the block
     * drops those, smelted - iron ore -> ingot, stone (cobblestone) -> stone,
     * clay -> bricks; if they cannot, it drops as with silk touch, unsmelted -
     * diamond ore, coal ore, grass, glass, ice stay themselves.
     */
    private static void autoSmelt(BlockEvent.HarvestDropsEvent event) {
        if (!event.isSilkTouching()) {
            smelt(event.getDrops());
            return;
        }
        IBlockState state = event.getState();
        NonNullList<ItemStack> ordinary = NonNullList.create();
        state.getBlock().getDrops(ordinary, event.getWorld(), event.getPos(), state,
                event.getFortuneLevel());
        if (anySmeltable(ordinary)) {
            smelt(ordinary);
            event.getDrops().clear();
            event.getDrops().addAll(ordinary);
        }
    }

    private static boolean anySmeltable(java.util.List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (!drop.isEmpty() && !FurnaceRecipes.instance().getSmeltingResult(drop).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Replaces each drop that has a furnace recipe by its result (see onHarvestDrops). */
    private static void smelt(java.util.List<ItemStack> drops) {
        java.util.List<ItemStack> smelted = new java.util.ArrayList<ItemStack>();
        for (ItemStack drop : drops) {
            ItemStack result = drop.isEmpty() ? ItemStack.EMPTY
                    : FurnaceRecipes.instance().getSmeltingResult(drop);
            if (result.isEmpty()) {
                smelted.add(drop);
                continue;
            }
            int total = result.getCount() * drop.getCount();
            while (total > 0) {
                ItemStack part = result.copy();
                part.setCount(Math.min(total, part.getMaxStackSize()));
                total -= part.getCount();
                smelted.add(part);
            }
        }
        drops.clear();
        drops.addAll(smelted);
    }

    /**
     * Magnet: the drops go into the player's inventory instead of the world;
     * what does not fit stays in the list and drops as usual.
     *
     * The game drops each item only with the event's drop chance (1 for almost
     * every block), so that chance is rolled here for each item first - the
     * magnet must not give items the block would not have dropped. The ones
     * left over then drop for sure.
     */
    private static void pullIntoInventory(BlockEvent.HarvestDropsEvent event, EntityPlayer player) {
        java.util.List<ItemStack> drops = event.getDrops();
        float chance = event.getDropChance();
        java.util.List<ItemStack> left = new java.util.ArrayList<ItemStack>();
        boolean picked = false;
        for (ItemStack drop : drops) {
            if (drop.isEmpty() || (chance < 1.0F && event.getWorld().rand.nextFloat() > chance)) {
                continue;
            }
            ItemStack rest = drop.copy();
            int before = rest.getCount();
            player.inventory.addItemStackToInventory(rest); // leaves in rest what did not fit
            if (rest.getCount() < before) {
                picked = true;
            }
            if (!rest.isEmpty()) {
                left.add(rest);
            }
        }
        drops.clear();
        drops.addAll(left);
        event.setDropChance(1.0F);

        if (picked) {
            // The usual item pick-up sound, so it is noticeable where the loot went.
            event.getWorld().playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                    ((event.getWorld().rand.nextFloat() - event.getWorld().rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

    private static boolean isShootingWithDrill(EntityPlayer player) {
        return player != null
                && player.getHeldItemMainhand().getItem() == ModTools.LASER_DRILL
                && ItemLaserDrill.isShooting(player);
    }
}
