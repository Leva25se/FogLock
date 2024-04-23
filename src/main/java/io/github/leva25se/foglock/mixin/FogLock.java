package io.github.leva25se.foglock.mixin;

import io.github.leva25se.foglock.client.FogLockClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BackgroundRenderer.class, priority = 1002)
public class FogLock {
    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        FogLockClient.getCustomFog().setFog(camera, fogType, viewDistance, thickFog, tickDelta);
    }
}