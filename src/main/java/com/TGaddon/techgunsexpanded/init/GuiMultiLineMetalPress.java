package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import techguns.gui.PoweredTileEntGui;
import techguns.util.TextUtil;

/**
 * CLIENT ONLY. Multi-line Metal Press window: Techguns' MetalPressGui (power
 * bar, redstone and security buttons, upgrade slot) with the press slots and
 * progress arrow once per line, placed by a MetalPressLayout - its texture is
 * the Metal Press's with the slot graphics copied to each line. The auto split
 * switch (for all lines) is a small On/Off button. A test layout; the real one
 * comes later.
 */
public class GuiMultiLineMetalPress extends PoweredTileEntGui {

    private final TileEntityMultiLineMetalPress press;
    private final MetalPressLayout layout;
    private GuiButtonExt splitButton;

    public GuiMultiLineMetalPress(InventoryPlayer player, TileEntityMultiLineMetalPress press,
                                  MetalPressLayout layout) {
        super(new ContainerMultiLineMetalPress(player, press, layout), press);
        this.press = press;
        this.layout = layout;
        this.tex = layout.texture;
        this.ySize = layout.height();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.splitButton = new GuiButtonExt(TileEntityMultiLineMetalPress.BUTTON_ID_AUTOSPLIT,
                this.guiLeft + this.layout.splitButtonX, this.guiTop + this.layout.splitButtonY,
                this.layout.splitButtonWidth, 14, "");
        this.buttonList.add(this.splitButton);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Progress arrows (the Metal Press texture's, filling downwards). Here
        // rather than in the background layer: Techguns draws that (texture,
        // power bar, upgrade slot), and it cannot be extended from this code.
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.layout.texture);
        for (int i = 0; i < this.press.lines.length; i++) {
            TileEntityMultiLineMetalPress.Line line = this.press.lines[i];
            if (line.isWorking()) {
                int h = line.getProgressScaled(21);
                this.drawTexturedModalRect(this.layout.lineX[i] + MetalPressLayout.ARROW_DX,
                        this.layout.lineY[i] + MetalPressLayout.ARROW_DY, 176, 0, 19, h + 1);
            }
        }

        int color = 4210752;
        this.splitButton.displayString = this.press.getAutoSplitMode() == 0
                ? TextUtil.trans("techguns.container.info.off")
                : TextUtil.trans("techguns.container.info.on");
        this.fontRenderer.drawString(I18n.format("gui." + TechgunsExpanded.MODID + ".advanced_metal_press.split"),
                this.layout.splitLabelX, this.layout.splitLabelY, color);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
