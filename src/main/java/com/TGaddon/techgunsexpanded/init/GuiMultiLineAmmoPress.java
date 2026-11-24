package com.TGaddon.techgunsexpanded.init;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import techguns.util.TextUtil;

/**
 * CLIENT ONLY. Multi-line Ammo Press window (GuiMultiLineMachine): the Ammo
 * Press's slot group and arrow per line, and per line its build plan - the
 * ammo it makes, shown left of the arrow (name on hover), with "<" and ">"
 * beside the output to change it. A test layout; the real one comes later.
 */
public class GuiMultiLineAmmoPress extends GuiMultiLineMachine {

    private final TileEntityMultiLineAmmoPress press;

    public GuiMultiLineAmmoPress(InventoryPlayer player, TileEntityMultiLineAmmoPress press,
                                 AmmoPressLayout layout) {
        super(new ContainerMultiLineAmmoPress(player, press, layout), press, layout);
        this.press = press;
    }

    @Override
    public void initGui() {
        super.initGui();
        int size = AmmoPressLayout.BUTTON_SIZE;
        for (int i = 0; i < this.press.lines.length; i++) {
            int x = this.guiLeft + this.layout.lineX[i];
            int y = this.guiTop + this.layout.lineY[i] + AmmoPressLayout.BUTTONS_DY;
            this.buttonList.add(new GuiButtonExt(TileEntityMultiLineAmmoPress.prevButton(i),
                    x + AmmoPressLayout.PREV_DX, y, size, size, "<"));
            this.buttonList.add(new GuiButtonExt(TileEntityMultiLineAmmoPress.nextButton(i),
                    x + AmmoPressLayout.NEXT_DX, y, size, size, ">"));
        }
    }

    @Override
    protected void drawMachineForeground(int mouseX, int mouseY) {
        int mx = mouseX - this.guiLeft;
        int my = mouseY - this.guiTop;
        String hovered = null;

        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 0; i < this.press.lines.length; i++) {
            ItemStack plan = this.press.ammoLine(i).getPlanResult();
            int x = this.layout.lineX[i] + AmmoPressLayout.PLAN_ICON_DX;
            int y = this.layout.lineY[i] + AmmoPressLayout.PLAN_ICON_DY;
            this.itemRender.renderItemAndEffectIntoGUI(plan, x, y);
            if (mx >= x && mx < x + 16 && my >= y && my < y + 16) {
                hovered = TextUtil.trans("techguns.gui.ammopress.build") + ": " + plan.getDisplayName();
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();

        if (hovered != null) {
            this.drawHoveringText(hovered, mx, my);
        }
    }
}
