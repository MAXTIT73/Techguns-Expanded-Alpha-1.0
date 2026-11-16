package com.TGaddon.techgunsexpanded.explosives;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import techguns.gui.ExplosiveChargeGui;

/**
 * CLIENT ONLY. The Door Charge's window: Techguns' Mining Charge window
 * (black background) without the radius / length / delay buttons - only
 * Detonate, which arms the charge and closes the window, as there. The panel
 * says what the charge sticks to and what will happen to it.
 */
public class GuiDoorCharge extends ExplosiveChargeGui {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/gui/door_charge_gui.png");

    private static final int TEXT_COLOR = 4210752;
    private static final int DOOR_COLOR = 0x1E7A1E;
    private static final int NO_COLOR = 0xA01010;
    /** Inside the grey panel. */
    private static final int TEXT_X = 37;
    private static final int TEXT_WIDTH = 104;

    private final TileDoorCharge charge;

    public GuiDoorCharge(InventoryPlayer player, TileDoorCharge charge) {
        super(player, charge);
        this.charge = charge;
        this.tex = TEXTURE;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonExt(TileDoorCharge.BUTTON_ARM, this.guiLeft + 57, this.guiTop + 73,
                60, 20, I18n.format("gui." + TechgunsExpanded.MODID + ".door_charge.detonate")));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        World world = this.charge.getWorld();
        BlockPos target = this.charge.getTargetPos();
        IBlockState state = world.getBlockState(target);

        String name;
        ItemStack pick = world.isAirBlock(target) ? ItemStack.EMPTY
                : state.getBlock().getPickBlock(state, null, world, target, this.mc.player);
        name = pick.isEmpty() ? state.getBlock().getLocalizedName() : pick.getDisplayName();

        String effect;
        int effectColor;
        if (!TileDoorCharge.canBreak(world, target, state)) {
            effect = "too_hard";
            effectColor = NO_COLOR;
        } else if (TileDoorCharge.isDoor(state.getBlock())) {
            effect = "door";
            effectColor = DOOR_COLOR;
        } else {
            effect = "block";
            effectColor = TEXT_COLOR;
        }

        this.line(I18n.format(key("target")), 28, TEXT_COLOR);
        this.line(name, 38, TEXT_COLOR);
        this.line(I18n.format(key(effect)), 50, effectColor);
        this.line(I18n.format(key("delay"), this.charge.getFuseSeconds()), 61, TEXT_COLOR);
    }

    private void line(String text, int y, int color) {
        this.fontRenderer.drawString(this.fontRenderer.trimStringToWidth(text, TEXT_WIDTH), TEXT_X, y, color);
    }

    private static String key(String name) {
        return "gui." + TechgunsExpanded.MODID + ".door_charge." + name;
    }
}
