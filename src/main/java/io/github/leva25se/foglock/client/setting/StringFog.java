package io.github.leva25se.foglock.client.setting;

import io.github.leva25se.foglock.client.value.StringValue;
import net.minecraft.client.render.Camera;

public class StringFog implements FogSetting {
    private final String str;
    private final StringValue stringValue;

    public StringFog(String str, StringValue stringValue, long time) {
        this.str = str;
        this.stringValue = stringValue;
        this.time = time;
    }

    @Override
    public float get(Camera camera, float vieDistance, boolean thickFog) {
        return stringValue.getValue(str, camera, vieDistance, thickFog);
    }

    private final long time;

    @Override
    public long getTime() {
        return time;
    }


}