package io.github.leva25se.foglock.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.leva25se.foglock.client.Configuration;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class FogLock {
    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Float[] fogType1 = Configuration.get(camera, cameraSubmersionType);
        if (fogType1 != null) {
            RenderSystem.setShaderFogStart(fogType1[0]);
            RenderSystem.setShaderFogEnd(fogType1[1]);
        }
    }

}
