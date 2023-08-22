package io.github.leva25se.foglock.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.leva25se.foglock.client.Configuration;
import io.github.leva25se.foglock.client.FloatType;
import io.github.leva25se.foglock.client.FogLockClient;
import io.github.leva25se.foglock.client.FogType;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(value = BackgroundRenderer.class, priority = 1002)
public class FogLock {

    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Configuration configuration = FogLockClient.getConfiguration();
        FogType fogType1 = configuration.toType(camera, cameraSubmersionType);
        HashMap<FloatType, FogSetting> fogSetting = configuration.get(fogType1);
        float[] color =  RenderSystem.getShaderFogColor();
        if (fogSetting != null) {
            if (fogSetting.containsKey(FloatType.START)){
                RenderSystem.setShaderFogStart(fogSetting.get(FloatType.START).get(camera, viewDistance, thickFog));
            }
            if (fogSetting.containsKey(FloatType.END)){
                RenderSystem.setShaderFogEnd(fogSetting.get(FloatType.END).get(camera, viewDistance, thickFog));
            }
            if (fogSetting.containsKey(FloatType.R)){
               color[0] = fogSetting.get(FloatType.R).get(camera, viewDistance, thickFog);
            }
            if (fogSetting.containsKey(FloatType.G)){
               color[1] = fogSetting.get(FloatType.G).get(camera, viewDistance, thickFog);
            }
            if (fogSetting.containsKey(FloatType.B)){
               color[2] = fogSetting.get(FloatType.B).get(camera, viewDistance, thickFog);
            }
            if (fogSetting.containsKey(FloatType.ALPHA)){
               color[3] = fogSetting.get(FloatType.ALPHA).get(camera, viewDistance, thickFog);
            }
        }
    }
}
