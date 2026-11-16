package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Map;

/**
 * CLIENT ONLY. The Laser Drill upgrades window (Tab in the settings window).
 *
 * Layout ported from the MCreator design "Laserdrillupcrades" (1.20.1 there):
 * same 176x166 background with the player inventory, the input slot at
 * (62, 60), "Put Upgrade" next to it, and at the top the list of installed
 * upgrades - each with its count - under "Enabled Upgrades".
 */
public class GuiLaserDrillUpgrades extends GuiContainer {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/gui/laser_drill_upgrades.png");

    private static final int LABEL_COLOR = 0x3C3C3C;
    private static final int EMPTY_COLOR = 0x808080;

    /** Room for the list between the title and the input slot. */
    private static final int LIST_TOP = 17;
    private static final int LINE_HEIGHT = 10;
    private static final int MAX_LINES = 4;

    private static final String LANG = "gui." + TechgunsExpanded.MODID + ".laser_drill_upgrades.";

    private final EntityPlayer player;

    public GuiLaserDrillUpgrades(EntityPlayer player) {
        super(new ContainerLaserDrillUpgrades(player));
        this.player = player;
        this.xSize = 176;
        this.ySize = 166;
    }

    /** Tab: back to the settings window (the server switches windows). */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_TAB) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId,
                    ContainerLaserDrillUpgrades.BUTTON_OPEN_SETTINGS);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        Gui.drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0, 0,
                this.xSize, this.ySize, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format(LANG + "enabled"), 8, 5, LABEL_COLOR);
        this.fontRenderer.drawString(I18n.format(LANG + "put"), 81, 62, LABEL_COLOR);

        Map<String, Integer> installed = LaserDrillUpgrades.getInstalled(this.player.getHeldItemMainhand());
        if (installed.isEmpty()) {
            this.fontRenderer.drawString(I18n.format(LANG + "none"), 8, LIST_TOP, EMPTY_COLOR);
            return;
        }
        int line = 0;
        for (Map.Entry<String, Integer> upgrade : installed.entrySet()) {
            if (line == MAX_LINES - 1 && installed.size() > MAX_LINES) {
                this.fontRenderer.drawString(I18n.format(LANG + "more", installed.size() - line),
                        8, LIST_TOP + line * LINE_HEIGHT, EMPTY_COLOR);
                break;
            }
            this.fontRenderer.drawString(name(upgrade.getKey()) + " x" + upgrade.getValue(),
                    8, LIST_TOP + line * LINE_HEIGHT, LABEL_COLOR);
            line++;
        }
    }

    /** "upgrade.techguns_expanded.<id>.name" from the lang file, or the id itself. */
    private static String name(String id) {
        String key = "upgrade." + TechgunsExpanded.MODID + "." + id + ".name";
        return I18n.hasKey(key) ? I18n.format(key) : id;
    }
}
