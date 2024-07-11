package io.github.leva25se.foglock.client.setting;

import net.minecraft.client.render.Camera;

public interface FogSetting {
    float get(Camera camera, float vieDistance, boolean thickFog, float current);

    long getTime();
}