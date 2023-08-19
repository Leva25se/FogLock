package io.github.leva25se.foglock.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.leva25se.foglock.client.Configuration;
import io.github.leva25se.foglock.client.FloatType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(BackgroundRenderer.class)
public class FogLock {
    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        HashMap<FloatType, Float> fogType1 = Configuration.get(camera, cameraSubmersionType);
        if (fogType1 != null) {
            if (fogType1.containsKey(FloatType.START)){
                RenderSystem.setShaderFogStart(fogType1.get(FloatType.START));
            }
            if (fogType1.containsKey(FloatType.END)){
                RenderSystem.setShaderFogEnd(fogType1.get(FloatType.END));
            }
            if (fogType1.containsKey(FloatType.ALPHA)){
                RenderSystem.getShaderFogColor()[3] = fogType1.get(FloatType.ALPHA);
            }
            if (fogType1.containsKey(FloatType.R)){
                RenderSystem.getShaderFogColor()[0] = fogType1.get(FloatType.R);
                RenderSystem.getShaderFogColor()[1] = fogType1.get(FloatType.G);
                RenderSystem.getShaderFogColor()[2] = fogType1.get(FloatType.B);
            }
        }
    }

}
