package io.github.leva25se.foglock.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

public class EffectApply {
    public void applyBlindness(BackgroundRenderer.FogType fogType, StatusEffectInstance effect, float viewDistance) {
        float f = effect.isInfinite() ? 5.0F : MathHelper.lerp(Math.min(1.0F, (float)effect.getDuration() / 20.0F), viewDistance, 5.0F);
        if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(f * 0.8F);
        } else {
            RenderSystem.setShaderFogStart(f * 0.25F);
            RenderSystem.setShaderFogEnd(f);
        }

    }

    public void applyDark(BackgroundRenderer.FogType fogType, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
        float f = MathHelper.lerp(effect.getFadeFactor(entity, tickDelta), viewDistance, 15.0F);
        RenderSystem.setShaderFogStart(fogType == BackgroundRenderer.FogType.FOG_SKY ? 0.0F : f * 0.75F);
        RenderSystem.setShaderFogEnd(f);
    }

}
