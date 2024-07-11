package io.github.leva25se.foglock.client.setting;

import net.minecraft.client.render.Camera;

public class FloatFog implements FogSetting {

    private final float f;
    private final long time;

    public FloatFog(float f, long time) {
        this.f = f;
        this.time = time;
    }

    @Override
    public float get(Camera camera, float vieDistance, boolean thickFog, float current) {
        return f;
    }

    @Override
    public long getTime() {
        return time;
    }
}