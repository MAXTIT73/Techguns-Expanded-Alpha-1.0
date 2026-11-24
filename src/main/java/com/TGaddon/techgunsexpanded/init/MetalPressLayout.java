package com.TGaddon.techgunsexpanded.init;

/**
 * Window layout of a multi-line Metal Press (MachineLayout): each line is the
 * Metal Press's slot group - two inputs side by side, the progress arrow under
 * them, the output below - plus the auto split switch (for all lines).
 */
public final class MetalPressLayout extends MachineLayout {

    public static final int INPUT_SPACING = 20;
    public static final int OUTPUT_OFFSET_X = 10;
    /** The output slot, below the line's inputs. */
    public static final int OUTPUT_DY = 43;

    /** Advanced: line 1 left of the Metal Press's slots, line 2 in their place; split switch on the left. */
    public static final MetalPressLayout ADVANCED = new MetalPressLayout("advanced_metal_press_gui",
            new int[] {50, 110}, row(2), 0, 16, 19, 16, 30, 28);

    /** Professional: three lines from the left; split switch top right, above the upgrade slot. */
    public static final MetalPressLayout PROFESSIONAL = new MetalPressLayout("professional_metal_press_gui",
            new int[] {18, 62, 106}, row(3), 0, 147, 19, 149, 30, 24);

    /**
     * Elite: four lines in two rows of two, so the window is 66 pixels taller
     * and the player inventory sits that much lower; split switch top right.
     */
    public static final MetalPressLayout ELITE = new MetalPressLayout("elite_metal_press_gui",
            new int[] {30, 90, 30, 90}, new int[] {INPUTS_Y, INPUTS_Y, INPUTS_Y + 66, INPUTS_Y + 66}, 66,
            147, 19, 149, 30, 24);

    public final int splitLabelX;
    public final int splitLabelY;
    public final int splitButtonX;
    public final int splitButtonY;
    public final int splitButtonWidth;

    private MetalPressLayout(String texture, int[] lineX, int[] lineY, int inventoryOffsetY,
                             int splitLabelX, int splitLabelY,
                             int splitButtonX, int splitButtonY, int splitButtonWidth) {
        // The Metal Press's arrow: 9 right of the first input, 19 below it.
        super(texture, lineX, lineY, 9, 19, inventoryOffsetY);
        this.splitLabelX = splitLabelX;
        this.splitLabelY = splitLabelY;
        this.splitButtonX = splitButtonX;
        this.splitButtonY = splitButtonY;
        this.splitButtonWidth = splitButtonWidth;
    }
}
