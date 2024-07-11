package io.github.leva25se.foglock.client.setting;

import io.github.leva25se.foglock.client.value.StringValue;
import net.minecraft.client.render.Camera;

public class StringFog implements FogSetting {
    private final String str;
    private final StringValue stringValue;
    private final long time;

    public StringFog(String str, StringValue stringValue, long time) {
        this.str = str;
        this.stringValue = stringValue;
        this.time = time;
    }

    @Override
    public float get(Camera camera, float vieDistance, boolean thickFog, float current) {
        return stringValue.getValue(str, camera, vieDistance, thickFog, current);
    }

    @Override
    public long getTime() {
        return time;
    }


}