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

import java.util.ArrayList;

@Mixin(BackgroundRenderer.class)
public class FogLock {
    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        ArrayList<Float> fogType1 = Configuration.get(camera, cameraSubmersionType);
        if (fogType1 != null) {
            switch (fogType1.size()){
                case 6:
                    RenderSystem.getShaderFogColor()[0] = fogType1.get(5);
                    RenderSystem.getShaderFogColor()[1] = fogType1.get(4);
                    RenderSystem.getShaderFogColor()[2] = fogType1.get(3);
                case 3:
                    RenderSystem.getShaderFogColor()[3] = fogType1.get(2);
                case 2: RenderSystem.setShaderFogStart(fogType1.get(0));
                    RenderSystem.setShaderFogEnd(fogType1.get(1));
            }
        }
    }

}
