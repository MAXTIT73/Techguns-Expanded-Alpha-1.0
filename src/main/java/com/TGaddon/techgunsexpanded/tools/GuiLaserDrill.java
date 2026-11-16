package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * CLIENT ONLY. The Laser Drill settings window (Shift + right click).
 *
 * Layout ported from the MCreator design (workspace "atern", GUI "Laserdrill"):
 * same 176x166 background, same positions. MCreator generated it for Minecraft
 * 1.20.1, whose GUI code does not exist in 1.12.2, so this is the same window
 * written with 1.12.2's GuiContainer, GuiButton and Forge's GuiCheckBox.
 *
 *  - left: Silk Touch, Auto Smelting, Fluid Converter checkboxes. A setting
 *    whose upgrade is not installed (LaserDrillSettings.requiredUpgrade) is
 *    greyed out: cannot be clicked, label grey, tooltip says which upgrade;
 *  - right: mining area 1x1 .. 5x5; the selected one has green text.
 *
 * Tab switches to the upgrades window (GuiLaserDrillUpgrades).
 */
public class GuiLaserDrill extends GuiContainer {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/gui/laser_drill.png");

    private static final int LABEL_COLOR = 0x3C3C3C;
    /** Green: clearly different from the light yellow of a button under the mouse. */
    private static final int SELECTED_COLOR = 0x55FF55;

    /** MCreator draws 20x20 checkboxes; Forge's box is 11x11, centred in that spot. */
    private static final int CHECKBOX_INSET = 4;

    private final EntityPlayer player;
    private final GuiButton[] sizeButtons = new GuiButton[LaserDrillSettings.MAX_SIZE + 1];

    /** The three settings: flag, button id, checkbox spot (MCreator layout), label spot. */
    private static final String[] FLAGS = {LaserDrillSettings.SILK_TOUCH,
            LaserDrillSettings.AUTO_SMELTING, LaserDrillSettings.FLUID_CONVERTER};
    private static final int[] FLAG_BUTTONS = {ContainerLaserDrill.BUTTON_SILK_TOUCH,
            ContainerLaserDrill.BUTTON_AUTO_SMELTING, ContainerLaserDrill.BUTTON_FLUID_CONVERTER};
    private static final String[] FLAG_LABELS = {"silk_touch", "auto_smelting", "fluid_converter"};
    private static final int[][] BOX_POS = {{13, 12}, {13, 44}, {13, 78}};
    private static final int[][] LABEL_POS = {{36, 17}, {35, 48}, {35, 84}};

    private final GuiCheckBox[] flagBoxes = new GuiCheckBox[FLAGS.length];

    private static final int UNAVAILABLE_COLOR = 0x8B8B8B;

    public GuiLaserDrill(EntityPlayer player) {
        super(new ContainerLaserDrill(player));
        this.player = player;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int[] sizeY = {0, 10, 34, 57, 80, 103};
        for (int size = LaserDrillSettings.MIN_SIZE; size <= LaserDrillSettings.MAX_SIZE; size++) {
            GuiButton button = new GuiButton(size, this.guiLeft + 129, this.guiTop + sizeY[size],
                    34, 20, size + "x" + size);
            this.sizeButtons[size] = button;
            this.buttonList.add(button);
        }
        ItemStack drill = this.drill();
        for (int i = 0; i < FLAGS.length; i++) {
            this.flagBoxes[i] = this.checkbox(FLAG_BUTTONS[i], BOX_POS[i][0], BOX_POS[i][1],
                    LaserDrillSettings.isOn(drill, FLAGS[i]));
        }
    }

    private GuiCheckBox checkbox(int id, int x, int y, boolean checked) {
        GuiCheckBox box = new GuiCheckBox(id, this.guiLeft + x + CHECKBOX_INSET,
                this.guiTop + y + CHECKBOX_INSET, "", checked);
        this.buttonList.add(box);
        return box;
    }

    private boolean available(int i) {
        return LaserDrillSettings.isAvailable(this.drill(), FLAGS[i]);
    }

    private ItemStack drill() {
        return this.player.getHeldItemMainhand();
    }

    /**
     * A click: apply it here at once (the window shows the drill's NBT) and
     * send it to the server, which applies the same change to the real item.
     * A checkbox has already flipped itself when this is called.
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (this.inventorySlots.enchantItem(this.player, button.id)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
        }
    }

    /** Tab: to the upgrades window (the server switches windows). */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_TAB) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId,
                    ContainerLaserDrill.BUTTON_OPEN_UPGRADES);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int selected = LaserDrillSettings.getSize(this.drill());
        for (int size = LaserDrillSettings.MIN_SIZE; size <= LaserDrillSettings.MAX_SIZE; size++) {
            // Sizes the drill cannot use yet are greyed out and cannot be clicked.
            this.sizeButtons[size].enabled = LaserDrillSettings.isSizeAvailable(this.drill(), size);
            this.sizeButtons[size].packedFGColour = size == selected ? SELECTED_COLOR : 0;
        }
        // A disabled button cannot be clicked (and does not flip). Each box
        // always shows the drill's real setting, never a state of its own.
        ItemStack drill = this.drill();
        for (int i = 0; i < FLAGS.length; i++) {
            this.flagBoxes[i].enabled = this.available(i);
            this.flagBoxes[i].setIsChecked(LaserDrillSettings.isOn(drill, FLAGS[i]));
        }
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        for (int i = 0; i < FLAGS.length; i++) {
            if (!this.flagBoxes[i].enabled && this.isOverFlag(i, mouseX, mouseY)) {
                String upgrade = LaserDrillSettings.requiredUpgrade(FLAGS[i]);
                this.drawHoveringText(I18n.format("gui." + TechgunsExpanded.MODID
                        + ".laser_drill.needs_upgrade", I18n.format("upgrade."
                        + TechgunsExpanded.MODID + "." + upgrade + ".name")), mouseX, mouseY);
            }
        }
        for (int size = LaserDrillSettings.MIN_SIZE; size <= LaserDrillSettings.MAX_SIZE; size++) {
            GuiButton button = this.sizeButtons[size];
            if (!button.enabled && button.isMouseOver()) {
                this.drawHoveringText(I18n.format("gui." + TechgunsExpanded.MODID
                        + ".laser_drill.needs_upgrade", I18n.format("upgrade." + TechgunsExpanded.MODID
                        + "." + LaserDrillUpgrades.AREA_EXPANSION + ".name")), mouseX, mouseY);
            }
        }
    }

    /** Over a setting's checkbox or its label (relative to the screen). */
    private boolean isOverFlag(int i, int mouseX, int mouseY) {
        int x = mouseX - this.guiLeft;
        int y = mouseY - this.guiTop;
        return x >= BOX_POS[i][0] && x < 125 && y >= BOX_POS[i][1] && y < BOX_POS[i][1] + 20;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        // The texture is 176x166 itself, not part of a 256x256 sheet.
        Gui.drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0, 0,
                this.xSize, this.ySize, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (int i = 0; i < FLAGS.length; i++) {
            this.label(FLAG_LABELS[i], LABEL_POS[i][0], LABEL_POS[i][1],
                    this.available(i) ? LABEL_COLOR : UNAVAILABLE_COLOR);
        }
        this.label("drill_area", 131, 127, LABEL_COLOR);
    }

    private void label(String key, int x, int y, int color) {
        this.fontRenderer.drawString(I18n.format("gui." + TechgunsExpanded.MODID + ".laser_drill." + key),
                x, y, color);
    }
}
