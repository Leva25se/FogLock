package io.github.leva25se.foglock.client.setting;

import net.minecraft.client.render.Camera;

public class StringFog implements FogSetting {
    private final String str;

    public StringFog(String str) {
        this.str = str;
    }

    @Override
    public float get(Camera camera, float vieDistance, boolean thickFog) {
        return Float.parseFloat(str);
    }


}