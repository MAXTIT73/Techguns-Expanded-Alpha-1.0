package com.TGaddon.techgunsexpanded.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;

/**
 * The Laser Drill's own area mining and fluid converter (server side).
 *
 * Area: replaces Techguns' GenericGunMeleeCharge.onBlockStartBreak, which only
 * knows a radius (odd sizes). Here the size comes from the drill's settings,
 * 1x1 .. 5x5, on the plane of the face that was hit. Even sizes have no centre
 * column/row, so they grow towards the side of the block the player aims at
 * (the half of the face the crosshair is on). The block checks and the way each
 * block is broken are Techguns' (permission event, harvest level, drops, XP,
 * block update to the client), with one fix: Techguns drops every block's items
 * at the centre block, this drops them where each block was.
 *
 * Fluid converter: lava (source or flowing) becomes obsidian, water becomes
 * ice - around every block the drill breaks, and where its mining beam points.
 * Only vanilla lava and water; other mods' fluids are left alone.
 */
public final class LaserDrillMining {

    private LaserDrillMining() {
    }

    // ------------------------------------------------------------------
    // Area mining
    // ------------------------------------------------------------------

    /**
     * Called from ItemLaserDrill.onBlockStartBreak for the block the player has
     * just finished breaking (the game breaks that one itself afterwards).
     */
    public static void onBlockStartBreak(ItemLaserDrill drill, ItemStack stack, BlockPos center,
                                         EntityPlayerMP player) {
        World world = player.world;
        int size = LaserDrillSettings.getSize(stack);
        List<BlockPos> area = new ArrayList<BlockPos>();

        RayTraceResult aim = drill.rayTraceForMining(world, player);
        boolean aimedHere = aim != null && aim.typeOfHit == RayTraceResult.Type.BLOCK
                && center.equals(aim.getBlockPos());

        // Sneaking does not matter (unlike Techguns' drills, where it mines 1x1):
        // the area is whatever the settings say, 1x1 included.
        if (size > 1 && aimedHere) {
            area = planeAround(center, aim.sideHit.getAxis(), size, aim.hitVec);
            // Energy: the block that was hit always costs 1 - the game breaks it
            // after this and GenericGunMeleeCharge.onBlockDestroyed takes the 1.
            // Without the Energy Efficiency upgrade every other block of the
            // area costs 1 more, and only as many are broken as there is energy
            // for (1 is kept for the hit block). With the upgrade they are free:
            // 1 per dig, whatever the size.
            boolean perBlock = !LaserDrillUpgrades.has(stack, LaserDrillUpgrades.ENERGY_EFFICIENCY);
            for (BlockPos pos : area) {
                if (pos.equals(center) || !canMine(drill, stack, world, pos, player)) {
                    continue;
                }
                if (perBlock && drill.getCurrentAmmo(stack) <= 1) {
                    break; // not enough energy for more blocks
                }
                if (breakBlock(stack, world, pos, player) && perBlock) {
                    drill.useAmmo(stack, 1);
                }
            }
        }

        if (LaserDrillSettings.isOn(stack, LaserDrillSettings.FLUID_CONVERTER)) {
            area.add(center);
            for (BlockPos pos : area) {
                convertFluidsAround(world, player, pos, 1);
            }
        }
    }

    /**
     * The size x size square on the plane perpendicular to the hit face's axis.
     * Odd sizes are centred on the block; even ones extend one more block to
     * the side of the block where the crosshair is.
     */
    static List<BlockPos> planeAround(BlockPos center, EnumFacing.Axis axis, int size, Vec3d hit) {
        int[] u, v;
        switch (axis) {
            case X:  // east/west face: the plane is Y-Z
                u = range(size, hit.y - center.getY());
                v = range(size, hit.z - center.getZ());
                return square(center, 1, 2, u, v);
            case Y:  // top/bottom face: the plane is X-Z
                u = range(size, hit.x - center.getX());
                v = range(size, hit.z - center.getZ());
                return square(center, 0, 2, u, v);
            default: // north/south face: the plane is X-Y
                u = range(size, hit.x - center.getX());
                v = range(size, hit.y - center.getY());
                return square(center, 0, 1, u, v);
        }
    }

    /**
     * Offsets {first, last} along one side of the square, relative to the
     * centre block. frac is where the crosshair is inside the block (0..1).
     */
    private static int[] range(int size, double frac) {
        if (size % 2 == 1) {
            int r = size / 2;
            return new int[] {-r, r};
        }
        int half = size / 2;
        return frac >= 0.5D ? new int[] {-(half - 1), half} : new int[] {-half, half - 1};
    }

    /** Blocks of the square; uAxis/vAxis: 0 = X, 1 = Y, 2 = Z. */
    private static List<BlockPos> square(BlockPos center, int uAxis, int vAxis, int[] u, int[] v) {
        List<BlockPos> out = new ArrayList<BlockPos>();
        for (int a = u[0]; a <= u[1]; a++) {
            for (int b = v[0]; b <= v[1]; b++) {
                int dx = (uAxis == 0 ? a : 0) + (vAxis == 0 ? b : 0);
                int dy = (uAxis == 1 ? a : 0) + (vAxis == 1 ? b : 0);
                int dz = (uAxis == 2 ? a : 0) + (vAxis == 2 ? b : 0);
                out.add(center.add(dx, dy, dz));
            }
        }
        return out;
    }

    /**
     * Techguns' checks (BlockUtils.getBlockPlaneAroundAxisForMining and the
     * loop in GenericGunMeleeCharge.onBlockStartBreak): no tile entities, a
     * solid harvestable block, within the drill's harvest level, the drill is
     * the right tool, and the block is not unbreakable.
     */
    private static boolean canMine(ItemLaserDrill drill, ItemStack stack, World world, BlockPos pos,
                                   EntityPlayer player) {
        if (!world.isBlockLoaded(pos) || world.getTileEntity(pos) != null) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.AIR || !block.canCollideCheck(state, false)
                || !block.canHarvestBlock(world, pos, player)) {
            return false;
        }
        String tool = block.getHarvestTool(state);
        if (tool != null && block.getHarvestLevel(state) > drill.getHarvestLevel(stack, tool, player, state)) {
            return false;
        }
        return state.getPlayerRelativeBlockHardness(player, world, pos) > 0.0F
                && drill.isEffectiveToolForState(stack, state);
    }

    /** Breaks one block the way Techguns' area mining does (see class comment for the one fix). */
    /** @return whether the block was broken */
    private static boolean breakBlock(ItemStack stack, World world, BlockPos pos, EntityPlayerMP player) {
        IBlockState state = world.getBlockState(pos);
        int xp = ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameType(), player, pos);
        if (xp < 0) {
            return false; // cancelled, e.g. by a protection mod
        }
        Block block = state.getBlock();
        block.onBlockHarvested(world, pos, state, player);
        TileEntity tile = world.getTileEntity(pos);
        if (block.removedByPlayer(state, world, pos, player, true)) {
            block.onBlockDestroyedByPlayer(world, pos, state);
            if (!player.capabilities.isCreativeMode) {
                block.harvestBlock(world, player, pos, state, tile, stack);
                block.dropXpOnBlockBreak(world, pos, xp);
            }
            world.playEvent(2001, pos, Block.getStateId(state));
            player.connection.sendPacket(new SPacketBlockChange(world, pos));
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Fluid converter
    // ------------------------------------------------------------------

    /** Turns vanilla lava into obsidian and water into ice in the cube around pos. */
    public static void convertFluidsAround(World world, EntityPlayer player, BlockPos pos, int radius) {
        for (BlockPos p : BlockPos.getAllInBoxMutable(pos.add(-radius, -radius, -radius),
                pos.add(radius, radius, radius))) {
            if (!world.isBlockLoaded(p)) {
                continue;
            }
            Block block = world.getBlockState(p).getBlock();
            IBlockState into;
            if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                into = Blocks.OBSIDIAN.getDefaultState();
            } else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                into = Blocks.ICE.getDefaultState();
            } else {
                continue;
            }
            if (world.isBlockModifiable(player, p)) {
                world.setBlockState(p.toImmutable(), into);
            }
        }
    }
}
