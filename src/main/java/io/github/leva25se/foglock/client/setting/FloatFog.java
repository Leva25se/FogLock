package io.github.leva25se.foglock.client.setting;

import net.minecraft.client.render.Camera;

public class FloatFog implements FogSetting {

    private final float f;

    public FloatFog(float f) {
        this.f = f;
    }

    @Override
    public float get(Camera camera, float vieDistance, boolean thickFog) {
        return f;
    }
}