package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.util.ResourceLocation;

/**
 * Where a multi-line machine's window (TileEntityMultiLineMachine) puts
 * things: each line's slot group at (lineX, lineY) - the position of its first
 * input slot - and the progress arrow at a fixed offset from it. The upgrade
 * slot is where Techguns' machine windows have it, unless the layout moves
 * it. A window wider than Techguns' 176 pixels keeps the player inventory
 * centred; a taller one (inventoryOffsetY > 0) has it that much lower. The
 * window textures are the Techguns machine's with the slot group copied to
 * each line's place; the progress arrow sprite is at (176, 0), or below the
 * window in textures of wide windows (arrowU, arrowV).
 */
public class MachineLayout {

    /** The upgrade slot (Techguns' machine windows). */
    public static final int UPGRADE_X = 152;
    public static final int UPGRADE_Y = 60;
    /** The Techguns machine window's size. */
    public static final int BASE_WIDTH = 176;
    public static final int BASE_HEIGHT = 166;
    /** The progress arrow (Techguns' machine windows): its sprite size; JEI's click area is a little taller. */
    public static final int ARROW_W = 19;
    public static final int ARROW_H = 21;
    public static final int ARROW_CLICK_H = 22;
    /** The Techguns machines' input row. */
    public static final int INPUTS_Y = 17;

    public final ResourceLocation texture;
    /** Each line's first input slot. */
    public final int[] lineX;
    public final int[] lineY;
    /** The progress arrow, relative to the line's first input slot. */
    public final int arrowDX;
    public final int arrowDY;
    /** How much lower than in Techguns' window the player inventory is. */
    public final int inventoryOffsetY;
    /** Window width (Techguns': 176); the player inventory is centred in it. */
    public final int width;
    public final int upgradeX;
    public final int upgradeY;
    /** Where the progress arrow sprite is in the texture. */
    public final int arrowU;
    public final int arrowV;
    /** The texture's size: 256 x 256, or larger for windows wider than 256 (see GuiMultiLineMachine). */
    public final int textureWidth;
    public final int textureHeight;

    /** A window of Techguns' width. */
    protected MachineLayout(String texture, int[] lineX, int[] lineY, int arrowDX, int arrowDY,
                            int inventoryOffsetY) {
        this(texture, lineX, lineY, arrowDX, arrowDY, inventoryOffsetY, BASE_WIDTH, UPGRADE_X, UPGRADE_Y, 176, 0);
    }

    protected MachineLayout(String texture, int[] lineX, int[] lineY, int arrowDX, int arrowDY,
                            int inventoryOffsetY, int width, int upgradeX, int upgradeY, int arrowU, int arrowV) {
        this(texture, lineX, lineY, arrowDX, arrowDY, inventoryOffsetY, width, upgradeX, upgradeY, arrowU, arrowV,
                256, 256);
    }

    protected MachineLayout(String texture, int[] lineX, int[] lineY, int arrowDX, int arrowDY,
                            int inventoryOffsetY, int width, int upgradeX, int upgradeY, int arrowU, int arrowV,
                            int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.texture = new ResourceLocation(TechgunsExpanded.MODID, "textures/gui/" + texture + ".png");
        this.lineX = lineX;
        this.lineY = lineY;
        this.arrowDX = arrowDX;
        this.arrowDY = arrowDY;
        this.inventoryOffsetY = inventoryOffsetY;
        this.width = width;
        this.upgradeX = upgradeX;
        this.upgradeY = upgradeY;
        this.arrowU = arrowU;
        this.arrowV = arrowV;
    }

    /** Height of the window. */
    public int height() {
        return BASE_HEIGHT + this.inventoryOffsetY;
    }

    /** Wider than a 256 x 256 texture can hold: the window draws its own background. */
    public boolean isExtraWide() {
        return this.width > 256;
    }

    /** How much further right than in Techguns' window the player inventory is (centred). */
    public int inventoryOffsetX() {
        return (this.width - BASE_WIDTH) / 2;
    }

    /** n times the Techguns input row. */
    protected static int[] row(int n) {
        int[] y = new int[n];
        java.util.Arrays.fill(y, INPUTS_Y);
        return y;
    }
}
