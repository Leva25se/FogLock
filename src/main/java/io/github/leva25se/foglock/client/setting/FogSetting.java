package io.github.leva25se.foglock.client.setting;


import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public interface FogSetting {
    float get(Camera camera, float vieDistance, boolean thickFog, float current, ClientLevel clientLevel);

    long getTime();
}