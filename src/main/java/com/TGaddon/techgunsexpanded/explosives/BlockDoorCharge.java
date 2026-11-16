package com.TGaddon.techgunsexpanded.explosives;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import techguns.Techguns;
import techguns.blocks.machines.BlockExplosiveCharge;
import techguns.events.TechgunsGuiHandler;
import techguns.tileentities.BasicInventoryTileEnt;
import techguns.tileentities.ExplosiveChargeTileEnt;
import techguns.util.TextUtil;

/**
 * Door Charge: a breaching charge for doors.
 *
 * Destroys only the block it sticks to: a door completely (it drops), any
 * other block on its own. Its tile entity, TileDoorCharge, is Techguns' Mining
 * Charge tile (arming, fuse, ticking) with that explosion; its window,
 * GuiDoorCharge, has only the Detonate button. It looks like the Mining Charge,
 * black and half the size (models/block/door_charge*.json).
 *
 * Its own block rather than a new type of Techguns' block, because Techguns'
 * blocks register themselves under Techguns' name. This is Techguns'
 * BlockExplosiveCharge (and the parts of BasicMachine it uses) for one type:
 *  - FACING points at the block it sticks to; it drops off when that is gone;
 *  - ARMED is Techguns' own property object: the tile entity sets it when armed;
 *  - right click opens its window through Techguns' handler (TechgunsGuiHandler
 *    finds it by the tile entity class, DoorChargeGuiRegistration), only for
 *    the owner (whoever placed it).
 */
public class BlockDoorCharge extends Block {

    public static final String NAME = "door_charge";

    /** The same property objects the tile entity uses (BlockDirectional.FACING, BlockExplosiveCharge.ARMED). */
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyBool ARMED = BlockExplosiveCharge.ARMED;

    /**
     * Half of the Mining Charge's boxes (its model is halved the same way:
     * towards the face it sticks to). Order D, U, N, S, W, E.
     */
    private static final AxisAlignedBB[] BOXES = {
            box(5, 0, 5, 11, 2.5, 11),
            box(5, 13.5, 5, 11, 16, 11),
            box(5, 5, 0, 11, 11, 2.5),
            box(5, 5, 13.5, 11, 11, 16),
            box(0, 5, 5, 2.5, 11, 11),
            box(13.5, 5, 5, 16, 11, 11)
    };

    private static AxisAlignedBB box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AxisAlignedBB(x1 / 16d, y1 / 16d, z1 / 16d, x2 / 16d, y2 / 16d, z2 / 16d);
    }

    public BlockDoorCharge() {
        super(Material.IRON); // BasicMachine: iron, metal sound, hardness 4
        this.setSoundType(SoundType.METAL);
        this.setHardness(4.0F);
        this.setRegistryName(TechgunsExpanded.MODID, NAME);
        this.setUnlocalizedName(TechgunsExpanded.MODID + "." + NAME);
        this.setCreativeTab(Techguns.tabTechgun);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.DOWN).withProperty(ARMED, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ARMED);
    }

    // --- tile entity: Techguns' Mining Charge ------------------------------

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDoorCharge();
    }

    /** ARMED is not stored in the meta, it comes from the tile entity (also old Mining Charge tiles). */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ExplosiveChargeTileEnt) {
            return state.withProperty(ARMED, ((ExplosiveChargeTileEnt) tile).isArmed());
        }
        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (placer instanceof EntityPlayer && tile instanceof ExplosiveChargeTileEnt) {
            ((ExplosiveChargeTileEnt) tile).setOwner((EntityPlayer) placer);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof BasicInventoryTileEnt) {
            if (((BasicInventoryTileEnt) tile).isUseableByPlayer(player)) {
                TechgunsGuiHandler.openGuiForPlayer(player, tile);
            } else {
                player.sendStatusMessage(new TextComponentString(
                        TextUtil.trans("techguns.container.security.denied")), true);
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof BasicInventoryTileEnt) {
            ((BasicInventoryTileEnt) tile).onBlockBreak();
        }
        super.breakBlock(world, pos, state);
    }

    // --- placement and attachment ------------------------------------------

    /** Faces the block it was put on. */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX,
                                            float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, facing.getOpposite());
    }

    /** Drops off when the block it sticks to is gone. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isAirBlock(pos.offset(state.getValue(FACING)))) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOXES[state.getValue(FACING).getIndex()];
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
    }

    /** Facing is placement only: the item is always the same. */
    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withProperty(FACING, mirror.mirror(state.getValue(FACING)));
    }

    // --- rendering: a small model, not a full cube -------------------------

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }
}
