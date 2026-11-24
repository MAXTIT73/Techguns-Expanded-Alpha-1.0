package com.TGaddon.techgunsexpanded.init;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import techguns.gui.PoweredTileEntGui;

/**
 * CLIENT ONLY. Window of a multi-line machine (TileEntityMultiLineMachine):
 * Techguns' machine window (texture, power bar, redstone and security buttons,
 * upgrade slot) with one progress arrow per line, placed by a MachineLayout.
 * Machines add their own buttons and text (drawMachineForeground).
 */
public abstract class GuiMultiLineMachine extends PoweredTileEntGui {

    protected final TileEntityMultiLineMachine machine;
    protected final MachineLayout layout;

    protected GuiMultiLineMachine(Container container, TileEntityMultiLineMachine machine, MachineLayout layout) {
        super(container, machine);
        this.machine = machine;
        this.layout = layout;
        this.tex = layout.texture;
        this.xSize = layout.width;
        this.ySize = layout.height();
        // Techguns draws the upgrade slot's frame here (1 pixel around the slot).
        this.upgradeSlotX = layout.upgradeX - 1;
        this.upgradeSlotY = layout.upgradeY - 1;
    }

    /**
     * The background of a window wider than 256 pixels (MachineLayout.isExtraWide),
     * whose texture is larger than the 256 x 256 Techguns' windows draw from.
     * What Techguns' window classes draw in their background layer, redone
     * here: the window texture (TGBaseGui), the security and redstone button
     * backs and the redstone lights (OwnedTileEntGui, RedstoneTileEntGui), the
     * power bar and the upgrade slot frame (PoweredTileEntGui). A window that
     * needs it overrides drawGuiContainerBackgroundLayer to call this.
     */
    protected void drawExtraWideBackground() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(this.layout.texture);
        Gui.drawModalRectWithCustomSizedTexture(k, l, 0, 0, this.xSize, this.ySize,
                this.layout.textureWidth, this.layout.textureHeight);

        this.mc.getTextureManager().bindTexture(security_texture);
        this.drawTexturedModalRect(k - 27, l + this.heightSecurityButton - 5, 195, 0, 27, 30);
        this.drawTexturedModalRect(k - 27, l + 5, 195, 0, 27, 30);
        if (this.redstoneTileEnt.isRedstoneEnabled()) {
            this.drawTexturedModalRect(k + 6, l + 5, 240, 0, 5, 5);
            this.drawTexturedModalRect(k + 11, l + 5, 245, 5, 5, 5);
        } else {
            this.drawTexturedModalRect(k + 6, l + 5, 240, 5, 5, 5);
            this.drawTexturedModalRect(k + 11, l + 5, 245, 0, 5, 5);
        }

        this.mc.getTextureManager().bindTexture(power_texture);
        int max = this.poweredTileEnt.getEnergyStorage().getMaxEnergyStored();
        int power = max > 0 ? this.poweredTileEnt.getEnergyStorage().getEnergyStored() * 48 / max : 0;
        int diff = techguns.TGConfig.machinesNeedNoPower ? 0 : 48 - power;
        this.drawTexturedModalRect(k + 8, l + 17 + diff, 251, 1 + diff, 4, 48);
        if (this.hasUpgradeSlot) {
            this.drawTexturedModalRect(k + this.upgradeSlotX, l + this.upgradeSlotY, 119, 59, 20, 20);
        }
    }

    /** The machine's own text and the like, in window coordinates. */
    protected void drawMachineForeground(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Progress arrows (the Techguns texture's, filling downwards). Here
        // rather than in the background layer: Techguns draws that (texture,
        // power bar, upgrade slot), and it cannot be extended from this code.
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.layout.texture);
        for (TileEntityMultiLineMachine.Line line : this.machine.lines) {
            if (line.isWorking()) {
                int h = line.getProgressScaled(MachineLayout.ARROW_H);
                Gui.drawModalRectWithCustomSizedTexture(this.layout.lineX[line.index] + this.layout.arrowDX,
                        this.layout.lineY[line.index] + this.layout.arrowDY, this.layout.arrowU, this.layout.arrowV,
                        MachineLayout.ARROW_W, h + 1, this.layout.textureWidth, this.layout.textureHeight);
            }
        }
        this.drawMachineForeground(mouseX, mouseY);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
