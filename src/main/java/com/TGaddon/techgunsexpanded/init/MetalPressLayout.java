package com.TGaddon.techgunsexpanded.init;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.util.ResourceLocation;

/**
 * Where a multi-line Metal Press's window puts things (container slots and
 * the drawn window). Each line is the Metal Press's slot group - two inputs
 * side by side, the progress arrow under them, the output below - placed at
 * its first input slot (lineX, lineY). The upgrade slot is where the Metal
 * Press has it. Windows with more lines than fit in a row are taller: the
 * player inventory moves down by inventoryOffsetY. The window textures are the
 * Metal Press's with the slot group copied to each line's place.
 */
public final class MetalPressLayout {

    public static final int INPUT_SPACING = 20;
    /** The Metal Press's input row. */
    public static final int INPUTS_Y = 17;
    public static final int OUTPUT_OFFSET_X = 10;
    /** The output slot, below the line's inputs. */
    public static final int OUTPUT_DY = 43;
    /** The upgrade slot (the Metal Press's). */
    public static final int UPGRADE_X = 152;
    public static final int UPGRADE_Y = 60;
    /** The progress arrow, relative to the line's first input slot. */
    public static final int ARROW_DX = 9;
    public static final int ARROW_DY = 19;
    /** The Metal Press window's height. */
    public static final int BASE_HEIGHT = 166;

    /** Advanced: line 1 left of the Metal Press's slots, line 2 in their place; split switch on the left. */
    public static final MetalPressLayout ADVANCED = new MetalPressLayout("advanced_metal_press_gui",
            new int[] {50, 110}, new int[] {INPUTS_Y, INPUTS_Y}, 0, 16, 19, 16, 30, 28);

    /** Professional: three lines from the left; split switch top right, above the upgrade slot. */
    public static final MetalPressLayout PROFESSIONAL = new MetalPressLayout("professional_metal_press_gui",
            new int[] {18, 62, 106}, new int[] {INPUTS_Y, INPUTS_Y, INPUTS_Y}, 0, 147, 19, 149, 30, 24);

    /**
     * Elite: four lines in two rows of two, so the window is 66 pixels taller
     * and the player inventory sits that much lower; split switch top right.
     */
    public static final MetalPressLayout ELITE = new MetalPressLayout("elite_metal_press_gui",
            new int[] {30, 90, 30, 90}, new int[] {INPUTS_Y, INPUTS_Y, INPUTS_Y + 66, INPUTS_Y + 66}, 66,
            147, 19, 149, 30, 24);

    public final ResourceLocation texture;
    /** Each line's first input slot. */
    public final int[] lineX;
    public final int[] lineY;
    /** How much lower than in the Metal Press window the player inventory is. */
    public final int inventoryOffsetY;
    public final int splitLabelX;
    public final int splitLabelY;
    public final int splitButtonX;
    public final int splitButtonY;
    public final int splitButtonWidth;

    private MetalPressLayout(String texture, int[] lineX, int[] lineY, int inventoryOffsetY,
                             int splitLabelX, int splitLabelY,
                             int splitButtonX, int splitButtonY, int splitButtonWidth) {
        this.texture = new ResourceLocation(TechgunsExpanded.MODID, "textures/gui/" + texture + ".png");
        this.lineX = lineX;
        this.lineY = lineY;
        this.inventoryOffsetY = inventoryOffsetY;
        this.splitLabelX = splitLabelX;
        this.splitLabelY = splitLabelY;
        this.splitButtonX = splitButtonX;
        this.splitButtonY = splitButtonY;
        this.splitButtonWidth = splitButtonWidth;
    }

    /** Height of the window. */
    public int height() {
        return BASE_HEIGHT + this.inventoryOffsetY;
    }
}
