package io.github.leva25se.foglock.mixin;

import io.github.leva25se.foglock.client.FogLockClient;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FogRenderer.class, priority = 1001)
public class FogRendererMixin {
    @Inject(at = @At("RETURN"), method = "setupFog", cancellable = true)
    private static void setFog(Camera camera, FogRenderer.FogMode fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<FogParameters> cir) {
        if(camera.getEntity() instanceof LocalPlayer localPlayer) {
            FogParameters fog = FogLockClient.getCustomFog().setFog(camera, cir.getReturnValue(), viewDistance, thickenFog, localPlayer.clientLevel);
            if (fog != null) {
                cir.setReturnValue(fog);
            }
        }
    }
}