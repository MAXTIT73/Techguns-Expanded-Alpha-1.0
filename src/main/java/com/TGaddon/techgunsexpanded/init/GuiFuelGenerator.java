package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiFuelGenerator extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            TechgunsExpanded.MODID, "textures/gui/fuel_generator.png"
    );

    private final TileEntityFuelGenerator te;

    public GuiFuelGenerator(InventoryPlayer playerInventory, TileEntityFuelGenerator te) {
        super(new ContainerFuelGenerator(playerInventory, te));
        this.te = te;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        // Основной фон GUI
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        // Иконка огня (показывает прогресс горения)
        if (te.isBurning() && te.getTotalBurnTime() > 0) {
            int fireHeight = 14 * te.getBurnTime() / te.getTotalBurnTime();
            drawTexturedModalRect(x + 79, y + 21 + (14 - fireHeight), 176, 14 - fireHeight, 14, fireHeight);
        }

        // Полоска энергии (оранжевая)
        if (te.getMaxEnergy() > 0) {
            int barHeight = 50 * te.getEnergyStored() / te.getMaxEnergy();
            drawTexturedModalRect(x + 152, y + 17 + (50 - barHeight), 190, 50 - barHeight, 16, barHeight);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = "Fuel Generator";
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, ySize - 94, 0x404040);

        // Тултип при наведении на полоску энергии
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        if (mouseX >= x + 152 && mouseX <= x + 168 && mouseY >= y + 17 && mouseY <= y + 67) {
            drawHoveringText(
                    Collections.singletonList(te.getEnergyStored() + " / " + te.getMaxEnergy() + " RF"),
                    mouseX - x, mouseY - y
            );
        }
    }
}
