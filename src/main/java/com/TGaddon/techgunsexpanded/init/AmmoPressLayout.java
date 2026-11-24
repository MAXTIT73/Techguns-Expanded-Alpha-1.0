package com.TGaddon.techgunsexpanded.init;

/**
 * Window layout of a multi-line Ammo Press (MachineLayout): each line is the
 * Ammo Press's slot group - three inputs (metal, metal, powder) side by side,
 * the progress arrow under the middle one, the output below it - plus the
 * line's build plan: its ammo shown left of the arrow, "<" and ">" either
 * side of the output.
 */
public final class AmmoPressLayout extends MachineLayout {

    public static final int INPUT_SPACING = 20;
    public static final int OUTPUT_OFFSET_X = 20;
    public static final int OUTPUT_DY = 43;
    /** The plan's ammo icon, relative to the first input. */
    public static final int PLAN_ICON_DX = 0;
    public static final int PLAN_ICON_DY = 21;
    /** "<" and ">" (16x16), relative to the first input. */
    public static final int PREV_DX = 0;
    public static final int NEXT_DX = 40;
    public static final int BUTTONS_DY = 44;
    public static final int BUTTON_SIZE = 16;

    /** Advanced: two lines side by side. */
    public static final AmmoPressLayout ADVANCED = new AmmoPressLayout("advanced_ammo_press_gui",
            new int[] {20, 92}, row(2), 0);

    /**
     * Professional: three lines side by side in a wider window (256 pixels;
     * the player inventory stays centred), the upgrade slot right of the
     * lines. The arrow sprite is below the window in this texture.
     */
    public static final AmmoPressLayout PROFESSIONAL = new AmmoPressLayout("professional_ammo_press_gui",
            new int[] {20, 92, 164}, row(3), 256, 230, UPGRADE_Y, 0, 170);

    /**
     * Elite: four lines side by side in a 328 pixel window (player inventory
     * centred), the upgrade slot right of the lines. Wider than a 256 texture:
     * the texture is 512 x 256 and the window draws its own background
     * (GuiEliteAmmoPress). The arrow sprite is below the window.
     */
    public static final AmmoPressLayout ELITE = new AmmoPressLayout("elite_ammo_press_gui",
            new int[] {20, 92, 164, 236}, row(4), 328, 302, UPGRADE_Y, 0, 170, 512, 256);

    private AmmoPressLayout(String texture, int[] lineX, int[] lineY, int inventoryOffsetY) {
        // The Ammo Press's arrow: under the middle input, 19 right of the first, 19 below.
        super(texture, lineX, lineY, 19, 19, inventoryOffsetY);
    }

    /** A wider window. */
    private AmmoPressLayout(String texture, int[] lineX, int[] lineY, int width, int upgradeX, int upgradeY,
                            int arrowU, int arrowV) {
        super(texture, lineX, lineY, 19, 19, 0, width, upgradeX, upgradeY, arrowU, arrowV);
    }

    /** A window wider than 256, with a larger texture. */
    private AmmoPressLayout(String texture, int[] lineX, int[] lineY, int width, int upgradeX, int upgradeY,
                            int arrowU, int arrowV, int textureWidth, int textureHeight) {
        super(texture, lineX, lineY, 19, 19, 0, width, upgradeX, upgradeY, arrowU, arrowV,
                textureWidth, textureHeight);
    }
}
