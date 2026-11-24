package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import techguns.Techguns;
import techguns.tileentities.BasicOwnedTileEnt;
import techguns.util.TextUtil;

/**
 * Block of a multi-line machine (TileEntityMultiLineMachine). Like Techguns'
 * machine blocks: no block model of its own - the tile entity renderer draws
 * the Techguns machine's model with the tier's own texture (see ClientProxy) -
 * turned to face the player who placed it, owned by that player (Techguns'
 * security button), and dropping its contents when broken.
 */
public class BlockMultiLineMachine extends Block implements ITileEntityProvider {

    private final java.util.function.Supplier<? extends TileEntityMultiLineMachine> tile;
    private final int guiId;

    /**
     * @param name  registry and translation name
     * @param tile  makes the press's tile entity
     * @param guiId its window (GuiHandler)
     */
    public BlockMultiLineMachine(String name, java.util.function.Supplier<? extends TileEntityMultiLineMachine> tile,
                                    int guiId) {
        super(Material.IRON);
        this.tile = tile;
        this.guiId = guiId;
        setUnlocalizedName(name);
        setRegistryName(TechgunsExpanded.MODID, name);
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setCreativeTab(Techguns.tabTechgun);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return this.tile.get();
    }

    /** Drawn by the tile entity renderer only, as Techguns' presses. */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /** BasicMachine.onBlockPlacedByExtended: owner, and facing the placer. */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMultiLineMachine) {
            TileEntityMultiLineMachine press = (TileEntityMultiLineMachine) tile;
            if (placer instanceof EntityPlayer) {
                press.setOwner((EntityPlayer) placer);
            }
            int dir = MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            press.rotation = (byte) (dir % 4);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
            EntityPlayer player, EnumHand hand, EnumFacing facing,
            float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMultiLineMachine) {
                if (((BasicOwnedTileEnt) te).isUseableByPlayer(player)) {
                    player.openGui(TechgunsExpanded.instance, this.guiId,
                            world, pos.getX(), pos.getY(), pos.getZ());
                } else {
                    player.sendStatusMessage(new TextComponentString(
                            TextUtil.trans("techguns.container.security.denied")), true);
                }
            }
        }
        return true;
    }

    /** Drops what is in the press (BasicMachine.breakBlock). */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMultiLineMachine) {
            ((TileEntityMultiLineMachine) tile).onBlockBreak();
        }
        super.breakBlock(world, pos, state);
    }
}
