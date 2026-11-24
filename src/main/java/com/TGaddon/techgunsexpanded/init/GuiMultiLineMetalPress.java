package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import techguns.util.TextUtil;

/**
 * CLIENT ONLY. Multi-line Metal Press window (GuiMultiLineMachine): the Metal
 * Press's slot group and arrow per line, and the auto split switch (for all
 * lines) as a small On/Off button. A test layout; the real one comes later.
 */
public class GuiMultiLineMetalPress extends GuiMultiLineMachine {

    private final TileEntityMultiLineMetalPress press;
    private final MetalPressLayout pressLayout;
    private GuiButtonExt splitButton;

    public GuiMultiLineMetalPress(InventoryPlayer player, TileEntityMultiLineMetalPress press,
                                  MetalPressLayout layout) {
        super(new ContainerMultiLineMetalPress(player, press, layout), press, layout);
        this.press = press;
        this.pressLayout = layout;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.splitButton = new GuiButtonExt(TileEntityMultiLineMetalPress.BUTTON_ID_AUTOSPLIT,
                this.guiLeft + this.pressLayout.splitButtonX, this.guiTop + this.pressLayout.splitButtonY,
                this.pressLayout.splitButtonWidth, 14, "");
        this.buttonList.add(this.splitButton);
    }

    @Override
    protected void drawMachineForeground(int mouseX, int mouseY) {
        this.splitButton.displayString = this.press.getAutoSplitMode() == 0
                ? TextUtil.trans("techguns.container.info.off")
                : TextUtil.trans("techguns.container.info.on");
        this.fontRenderer.drawString(I18n.format("gui." + TechgunsExpanded.MODID + ".advanced_metal_press.split"),
                this.pressLayout.splitLabelX, this.pressLayout.splitLabelY, 4210752);
    }
}
