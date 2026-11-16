package com.TGaddon.techgunsexpanded.rockets.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import techguns.client.models.projectiles.ModelRocket;

/**
 * Renderer for our high-power rockets. Reuses Techguns' ModelRocket (same shape
 * as the vanilla Techguns rockets) but binds our recolored texture. The GL
 * transforms mirror Techguns' RenderRocketProjectile so orientation/scale match.
 *
 * One instance is registered per rocket type with its own texture (orange/blue).
 */
public class RenderHighPowerRocket extends Render<Entity> {

    private final ModelBase model = new ModelRocket();
    private final ResourceLocation texture;

    public RenderHighPowerRocket(RenderManager renderManager, ResourceLocation texture) {
        super(renderManager);
        this.texture = texture;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);

        // -90, not +90: Techguns' RenderRocketProjectile subtracts here, and the
        // ModelRocket is built around that. With +90 the model sits 180 degrees
        // off, so the rocket flies tail-first (and looks sideways when fired
        // horizontally).
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        GlStateManager.rotate(yaw - 90.0F, 0.0F, 1.0F, 0.0F);

        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);

        GlStateManager.scale(0.9F, 0.9F, 0.9F);

        bindEntityTexture(entity);
        this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
