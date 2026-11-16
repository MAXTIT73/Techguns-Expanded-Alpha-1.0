package com.TGaddon.techgunsexpanded.tools;

import com.TGaddon.techgunsexpanded.TechgunsExpanded;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import techguns.client.render.TGRenderHelper;
import techguns.entities.projectiles.LaserProjectile;

/**
 * CLIENT ONLY. Draws the Laser Drill's beams (LaserDrillShot, LaserDrillBeam).
 *
 * This is Techguns' RenderLaserProjectile (the Laser Rifle's beam renderer) line
 * for line - same shape, width, fade and scrolling - with the purple beam
 * textures. A copy rather than a subclass because Techguns keeps its textures in
 * private fields, and the start texture is bound directly from one of them.
 */
public class RenderLaserDrillBeam extends Render<LaserProjectile> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/fx/laser_drill_beam.png");
    private static final ResourceLocation TEXTURE_START =
            new ResourceLocation(TechgunsExpanded.MODID, "textures/fx/laser_drill_beam_start.png");

    private static final double LASER_WIDTH = 3.0D;

    public RenderLaserDrillBeam(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(LaserProjectile laser, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        double distance = laser.distance;
        short maxTicks = laser.maxTicks;
        float prog = (float) laser.ticksExisted / (float) maxTicks;
        double width = LASER_WIDTH * Math.sin(Math.sqrt(prog) * Math.PI) * 2.0D;
        double distanceStart = Math.min(1.0D, distance);
        double u = distance / LASER_WIDTH * 2.0D;

        this.bindEntityTexture(laser);
        GlStateManager.pushMatrix();
        TGRenderHelper.enableBlendMode(TGRenderHelper.RenderType.ADDITIVE);
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(laser.laserYaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(laser.laserPitch, 0.0F, 0.0F, 1.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float scale = 0.0125F;
        distance *= 80.0D;
        distanceStart *= 80.0D;
        float rotX = 45.0F + prog * 180.0F;
        GlStateManager.rotate(rotX + 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        float brightness = (float) Math.sin(Math.sqrt(prog) * Math.PI);

        // The beam
        if (distance > distanceStart) {
            GlStateManager.pushMatrix();
            for (int i = 0; i < 2; ++i) {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.glNormal3f(0.0F, 0.0F, scale);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.pos(distance, -width, 0.0D).tex(u + prog, 0.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
                buffer.pos(distanceStart, -width, 0.0D).tex(prog, 0.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
                buffer.pos(distanceStart, width, 0.0D).tex(prog, 1.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
                buffer.pos(distance, width, 0.0D).tex(u + prog, 1.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
                tessellator.draw();
            }
            GlStateManager.popMatrix();
        }

        // The start at the muzzle
        GlStateManager.pushMatrix();
        this.renderManager.renderEngine.bindTexture(TEXTURE_START);
        for (int i = 0; i < 2; ++i) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, scale);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(distanceStart, -width, 0.0D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            buffer.pos(0.0D, -width, 0.0D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            buffer.pos(0.0D, width, 0.0D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            buffer.pos(distanceStart, width, 0.0D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, brightness).endVertex();
            tessellator.draw();
        }
        GlStateManager.popMatrix();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        TGRenderHelper.disableBlendMode(TGRenderHelper.RenderType.ADDITIVE);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(LaserProjectile entity) {
        return TEXTURE;
    }
}
