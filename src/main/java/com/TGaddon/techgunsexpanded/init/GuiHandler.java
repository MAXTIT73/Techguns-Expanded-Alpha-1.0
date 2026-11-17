package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.tools.ContainerLaserDrill;
import com.TGaddon.techgunsexpanded.tools.ContainerLaserDrillUpgrades;
import com.TGaddon.techgunsexpanded.tools.GuiLaserDrill;
import com.TGaddon.techgunsexpanded.tools.GuiLaserDrillUpgrades;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    /** Advanced Metal Press window (two press lines). */
    public static final int ADVANCED_METAL_PRESS = 7;

    /** Professional Metal Press window (three press lines). */
    public static final int PROFESSIONAL_METAL_PRESS = 10;

    /** Elite Metal Press window (four press lines). */
    public static final int ELITE_METAL_PRESS = 11;

    /** Laser Drill settings window - bound to the drill in hand, not to a block. */
    public static final int LASER_DRILL = 8;

    /** Laser Drill upgrades window (Tab in the settings window). */
    public static final int LASER_DRILL_UPGRADES = 9;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == LASER_DRILL) {
            return new ContainerLaserDrill(player);
        }
        if (id == LASER_DRILL_UPGRADES) {
            return new ContainerLaserDrillUpgrades(player);
        }
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (id == 0 && te instanceof TileEntityFuelGenerator) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGenerator) te);
        }
        if (id == 1 && te instanceof TileEntityFuelGeneratorMk2) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk2) te);
        }
        if (id == 2 && te instanceof TileEntityFuelGeneratorMk3) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk3) te);
        }
        if (id == 3 && te instanceof TileEntityFuelGeneratorMk4) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk4) te);
        }
        if (id == 4 && te instanceof TileEntityFuelGeneratorMk5) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk5) te);
        }
        if (id == 5 && te instanceof TileEntityFuelGeneratorMk6) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk6) te);
        }
        if (id == 6 && te instanceof TileEntityFuelGeneratorCreative) {
            return new ContainerFuelGenerator(player.inventory, (TileEntityFuelGeneratorCreative) te);
        }
        if (id == ADVANCED_METAL_PRESS && te instanceof TileEntityAdvancedMetalPress) {
            return new ContainerAdvancedMetalPress(player.inventory, (TileEntityAdvancedMetalPress) te);
        }
        if (id == PROFESSIONAL_METAL_PRESS && te instanceof TileEntityProfessionalMetalPress) {
            return new ContainerMultiLineMetalPress(player.inventory, (TileEntityProfessionalMetalPress) te,
                    MetalPressLayout.PROFESSIONAL);
        }
        if (id == ELITE_METAL_PRESS && te instanceof TileEntityEliteMetalPress) {
            return new ContainerMultiLineMetalPress(player.inventory, (TileEntityEliteMetalPress) te,
                    MetalPressLayout.ELITE);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == LASER_DRILL) {
            return new GuiLaserDrill(player);
        }
        if (id == LASER_DRILL_UPGRADES) {
            return new GuiLaserDrillUpgrades(player);
        }
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (id == 0 && te instanceof TileEntityFuelGenerator) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGenerator) te);
        }
        if (id == 1 && te instanceof TileEntityFuelGeneratorMk2) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk2) te);
        }
        if (id == 2 && te instanceof TileEntityFuelGeneratorMk3) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk3) te);
        }
        if (id == 3 && te instanceof TileEntityFuelGeneratorMk4) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk4) te);
        }
        if (id == 4 && te instanceof TileEntityFuelGeneratorMk5) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk5) te);
        }
        if (id == 5 && te instanceof TileEntityFuelGeneratorMk6) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorMk6) te);
        }
        if (id == 6 && te instanceof TileEntityFuelGeneratorCreative) {
            return new GuiFuelGenerator(player.inventory, (TileEntityFuelGeneratorCreative) te);
        }
        if (id == ADVANCED_METAL_PRESS && te instanceof TileEntityAdvancedMetalPress) {
            return new GuiAdvancedMetalPress(player.inventory, (TileEntityAdvancedMetalPress) te);
        }
        if (id == PROFESSIONAL_METAL_PRESS && te instanceof TileEntityProfessionalMetalPress) {
            return new GuiProfessionalMetalPress(player.inventory, (TileEntityProfessionalMetalPress) te);
        }
        if (id == ELITE_METAL_PRESS && te instanceof TileEntityEliteMetalPress) {
            return new GuiEliteMetalPress(player.inventory, (TileEntityEliteMetalPress) te);
        }
        return null;
    }
}
