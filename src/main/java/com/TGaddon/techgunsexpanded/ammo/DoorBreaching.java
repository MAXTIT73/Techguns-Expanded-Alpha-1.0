package com.TGaddon.techgunsexpanded.ammo;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import techguns.blocks.BlockTGDoor2x1;
import techguns.blocks.BlockTGDoor3x3;
import techguns.blocks.EnumDoorState;
import techguns.tileentities.Door3x3TileEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * What a Breaching Shotgun Shell does to a door (server side).
 *
 * A door anyone can open by hand (a wooden door, Techguns' bunker door, a
 * Techguns 3x3 door set to open on right click that the shooter may use) is
 * simply shot open. Any other door - an iron door, a 3x3 door on redstone or
 * locked to its owner, a mod's door that does not open by hand - stays as it
 * is, but its "lock" is shot out: from then on anyone opens and closes it with
 * right click like a wooden door. Nothing about the block changes (same block,
 * model, texture); the breached doors are a list saved with each dimension
 * (Data), which a right click on them checks (onRightClickBlock).
 *
 * Doors: BlockDoor (vanilla and almost every mod's door, Techguns' 2x1 doors)
 * and Techguns' 3x3 doors. A door is remembered by its lower half (BlockDoor)
 * or its centre block (3x3) and forgotten when it is broken or a block is
 * placed there.
 */
@Mod.EventBusSubscriber(modid = TechgunsExpanded.MODID)
public final class DoorBreaching {

    private DoorBreaching() {
    }

    // ------------------------------------------------------------------
    // Shot
    // ------------------------------------------------------------------

    /** A pellet hit the block at pos. */
    public static void onDoorShot(World world, BlockPos pos, EntityLivingBase shooter) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockDoor) {
            BlockPos lower = lowerHalf(pos, state);
            if (opensByHand(block)) {
                openDoor(world, lower, (BlockDoor) block);
            } else {
                breach(world, lower);
            }
        } else if (block instanceof BlockTGDoor3x3) {
            BlockPos master = findMaster(world, pos, state, (BlockTGDoor3x3) block);
            TileEntity tile = world.getTileEntity(master);
            if (tile instanceof Door3x3TileEntity && opensByHand((Door3x3TileEntity) tile, shooter)) {
                if (world.getBlockState(master).getValue(BlockTGDoor3x3.STATE) == EnumDoorState.CLOSED) {
                    ((BlockTGDoor3x3) block).toggleState(world, master);
                }
            } else {
                breach(world, master);
            }
        }
    }

    /**
     * Whether a BlockDoor opens with right click for anyone: vanilla's rule
     * (not iron) - and Techguns' bunker door, which is iron but opens by hand.
     */
    private static boolean opensByHand(Block door) {
        return door instanceof BlockTGDoor2x1 || door.getDefaultState().getMaterial() != Material.IRON;
    }

    private static boolean opensByHand(Door3x3TileEntity door, EntityLivingBase shooter) {
        return door.isOpenWithRightClick() && shooter instanceof EntityPlayer
                && door.isUseableByPlayer((EntityPlayer) shooter);
    }

    private static void openDoor(World world, BlockPos lower, BlockDoor door) {
        IBlockState state = world.getBlockState(lower);
        if (state.getBlock() == door && !state.getValue(BlockDoor.OPEN)) {
            door.toggleDoor(world, lower, true);
        }
    }

    /** Remembers the door as breached; the first time with a "lock broken" clank. */
    private static void breach(World world, BlockPos key) {
        if (Data.get(world).add(key)) {
            world.playSound(null, key, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.BLOCKS,
                    0.6F, 1.4F + world.rand.nextFloat() * 0.2F);
        }
    }

    // ------------------------------------------------------------------
    // Right click on a breached door
    // ------------------------------------------------------------------

    /**
     * Opens or closes a breached door like a wooden one (whoever clicks).
     * Early, so the door's own right click (refusing, or asking for its owner)
     * does not run. Like any door, not while sneaking with something in hand.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        if (player.isSneaking() && (!player.getHeldItemMainhand().isEmpty()
                || !player.getHeldItemOffhand().isEmpty())) {
            return;
        }
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        BlockPos key;
        if (block instanceof BlockDoor) {
            key = lowerHalf(pos, state);
        } else if (block instanceof BlockTGDoor3x3) {
            key = findMaster(world, pos, state, (BlockTGDoor3x3) block);
        } else {
            return;
        }
        Data data = Data.get(world);
        if (!data.contains(key)) {
            return;
        }

        // Toggle once per click: both hands fire this event.
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }
        if (block instanceof BlockDoor) {
            IBlockState lower = world.getBlockState(key);
            if (lower.getBlock() == block) {
                ((BlockDoor) block).toggleDoor(world, key, !lower.getValue(BlockDoor.OPEN));
            } else {
                data.remove(key);
            }
        } else {
            IBlockState master = world.getBlockState(key);
            if (master.getBlock() == block && master.getValue(BlockTGDoor3x3.MASTER)) {
                EnumDoorState doorState = master.getValue(BlockTGDoor3x3.STATE);
                // Not while it is moving, as with Techguns' own right click.
                if (doorState == EnumDoorState.CLOSED || doorState == EnumDoorState.OPENED) {
                    ((BlockTGDoor3x3) block).toggleState(world, key);
                }
            } else {
                data.remove(key);
            }
        }
        player.swingArm(EnumHand.MAIN_HAND);
    }

    // ------------------------------------------------------------------
    // Forgetting doors that are gone
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!event.isCanceled() && !event.getWorld().isRemote) {
            forget(event.getWorld(), event.getPos(), event.getState());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlace(BlockEvent.PlaceEvent event) {
        if (!event.isCanceled() && !event.getWorld().isRemote) {
            Data data = Data.get(event.getWorld());
            data.remove(event.getPos());
        }
    }

    private static void forget(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        Data data = Data.get(world);
        if (block instanceof BlockDoor) {
            data.remove(lowerHalf(pos, state));
        } else if (block instanceof BlockTGDoor3x3) {
            data.remove(findMaster(world, pos, state, (BlockTGDoor3x3) block));
        }
    }

    // ------------------------------------------------------------------
    // Door helpers
    // ------------------------------------------------------------------

    private static BlockPos lowerHalf(BlockPos pos, IBlockState state) {
        return state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ? pos.down() : pos;
    }

    /** The 3x3 door's offsets around its centre (Techguns' pos_x / pos_z, which are not public). */
    private static final Vec3i[] AROUND_X = {
            new Vec3i(0, 1, 0), new Vec3i(0, -1, 0), new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0),
            new Vec3i(-1, -1, 0), new Vec3i(1, -1, 0), new Vec3i(-1, 1, 0), new Vec3i(1, 1, 0)};
    private static final Vec3i[] AROUND_Z = {
            new Vec3i(0, 1, 0), new Vec3i(0, -1, 0), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1),
            new Vec3i(0, -1, -1), new Vec3i(0, -1, 1), new Vec3i(0, 1, -1), new Vec3i(0, 1, 1)};

    /** BlockTGDoor3x3.findMaster (protected there): the centre block that holds the door's tile. */
    private static BlockPos findMaster(World world, BlockPos pos, IBlockState state, BlockTGDoor3x3 door) {
        if (state.getValue(BlockTGDoor3x3.MASTER)) {
            return pos;
        }
        for (Vec3i offset : state.getValue(BlockTGDoor3x3.ZPLANE) ? AROUND_Z : AROUND_X) {
            BlockPos p = pos.add(offset);
            IBlockState s = world.getBlockState(p);
            if (s.getBlock() == door && s.getValue(BlockTGDoor3x3.MASTER)) {
                return p;
            }
        }
        return pos;
    }

    // ------------------------------------------------------------------
    // Saved list of breached doors (per dimension)
    // ------------------------------------------------------------------

    public static class Data extends WorldSavedData {

        public static final String NAME = TechgunsExpanded.MODID + "_breached_doors";

        private final Set<Long> doors = new HashSet<Long>();

        public Data(String name) {
            super(name);
        }

        public static Data get(World world) {
            MapStorage storage = world.getPerWorldStorage();
            Data data = (Data) storage.getOrLoadData(Data.class, NAME);
            if (data == null) {
                data = new Data(NAME);
                storage.setData(NAME, data);
            }
            return data;
        }

        public boolean contains(BlockPos pos) {
            return this.doors.contains(pos.toLong());
        }

        /** @return true if it was not breached before */
        public boolean add(BlockPos pos) {
            boolean added = this.doors.add(pos.toLong());
            if (added) {
                this.markDirty();
            }
            return added;
        }

        public void remove(BlockPos pos) {
            if (this.doors.remove(pos.toLong())) {
                this.markDirty();
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            this.doors.clear();
            NBTTagList list = tag.getTagList("doors", 4); // 4 = long
            for (int i = 0; i < list.tagCount(); i++) {
                this.doors.add(((NBTTagLong) list.get(i)).getLong());
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            NBTTagList list = new NBTTagList();
            for (Long pos : this.doors) {
                list.appendTag(new NBTTagLong(pos));
            }
            tag.setTag("doors", list);
            return tag;
        }
    }
}
