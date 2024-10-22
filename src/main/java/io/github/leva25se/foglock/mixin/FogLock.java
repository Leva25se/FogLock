package io.github.leva25se.foglock.mixin;

import io.github.leva25se.foglock.client.FogLockClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BackgroundRenderer.class, priority = 1002)
public class FogLock {
    @Inject(at = @At("RETURN"), method = "applyFog", cancellable = true)
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<Fog> cir) {
        Fog fog = FogLockClient.getCustomFog().setFog(camera, cir.getReturnValue(), viewDistance, thickenFog);
        if (fog != null) {
            cir.setReturnValue(fog);
        }
    }
}