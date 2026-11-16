package com.TGaddon.techgunsexpanded.explosives;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import techguns.TGConfig;
import techguns.TGPackets;
import techguns.blocks.BlockTGDoor3x3;
import techguns.packets.PacketSpawnParticle;
import techguns.tileentities.ExplosiveChargeTileEnt;

/**
 * The Door Charge's tile entity. Techguns' Mining Charge tile (owner, arming,
 * fuse, ticking, sync to the client, the window's Detonate button) with its
 * own explosion: only the block the charge sticks to.
 *  - a door: the whole door is destroyed, whatever it is made of, and drops
 *    as an item;
 *  - anything else: just that one block, dropped as when mined - if the
 *    Mining Charge could break it (hardness up to Techguns' config limit;
 *    bedrock and the like never).
 * No radius or length: the window only has the Detonate button (GuiDoorCharge),
 * every other button is ignored here. The fuse is the Mining Charge's default.
 */
public class TileDoorCharge extends ExplosiveChargeTileEnt {

    /** Arms the charge - the Mining Charge's button 0 ("Detonate"). */
    public static final int BUTTON_ARM = 0;

    @Override
    public void buttonClicked(int id, EntityPlayer ply, String data) {
        if (id == BUTTON_ARM) {
            super.buttonClicked(id, ply, data);
        }
    }

    /** Fuse seconds, for the window. */
    public int getFuseSeconds() {
        return this.fusetime / 20;
    }

    /** The block the charge sticks to. */
    public BlockPos getTargetPos() {
        return this.getPos().offset(this.getOrientation());
    }

    @Override
    protected void explode() {
        World world = this.world;
        if (world.isRemote) {
            return;
        }
        BlockPos pos = this.getPos();
        BlockPos target = this.getTargetPos();
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F,
                (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        TGPackets.network.sendToAllAround(new PacketSpawnParticle("MiningChargeBlockExplosion",
                        target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D),
                TGPackets.targetPointAroundBlockPos(world.provider.getDimension(), target, 50));

        world.setBlockToAir(pos); // the charge itself

        IBlockState state = world.getBlockState(target);
        if (!canBreak(world, target, state)) {
            return;
        }
        // As the Mining Charge: only for an owner who is online, and protection
        // mods can cancel it (the break event in the owner's name).
        EntityPlayer owner = world.getPlayerEntityByUUID(this.getOwner());
        if (owner == null) {
            return;
        }
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, target, state, owner);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }

        if (isDoor(state.getBlock())) {
            breakDoor(world, target, state, owner);
        } else {
            world.destroyBlock(target, true);
        }
    }

    /**
     * Whether a block is a door. Vanilla doors and almost every mod's door
     * (Techguns' 2x1 doors too) are BlockDoor; Techguns' big 3x3 doors are
     * their own class.
     */
    public static boolean isDoor(Block block) {
        return block instanceof BlockDoor || block instanceof BlockTGDoor3x3;
    }

    /** Doors always; other blocks if the Mining Charge could break them. */
    public static boolean canBreak(World world, BlockPos pos, IBlockState state) {
        if (world.isAirBlock(pos)) {
            return false;
        }
        if (isDoor(state.getBlock())) {
            return true;
        }
        float hardness = state.getBlockHardness(world, pos);
        return hardness >= 0.0F && hardness < TGConfig.explosiveChargeMaxBlockHardness;
    }

    /** The whole door goes, and drops once. */
    private static void breakDoor(World world, BlockPos pos, IBlockState state, EntityPlayer owner) {
        Block block = state.getBlock();
        if (block instanceof BlockDoor) {
            // The lower half drops the door; breaking it takes the upper half too.
            BlockPos lower = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ? pos.down() : pos;
            world.destroyBlock(lower, true);
            if (world.getBlockState(pos).getBlock() == block) {
                world.setBlockToAir(pos); // if a mod's door did not take its other half
            }
        } else {
            // Techguns' 3x3 door: drops only when a player breaks it by hand, so
            // its item (with its door type) is dropped here; removing any part
            // removes the whole door.
            ItemStack drop = block.getPickBlock(state, null, world, pos, owner);
            world.playEvent(2001, pos, Block.getStateId(state));
            world.setBlockToAir(pos);
            if (!drop.isEmpty()) {
                Block.spawnAsEntity(world, pos, drop);
            }
        }
    }
}
