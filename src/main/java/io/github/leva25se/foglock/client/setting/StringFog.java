package io.github.leva25se.foglock.client.setting;

import io.github.leva25se.foglock.client.value.ApplyPlaceholders;
import io.github.leva25se.foglock.client.value.StringValue;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public class StringFog implements FogSetting {
    private final String str;
    private final StringValue stringValue;
    private final long time;
    private final ApplyPlaceholders applyPlaceholders;

    public StringFog(String str, StringValue stringValue, long time, ApplyPlaceholders applyPlaceholders) {
        this.str = str;
        this.stringValue = stringValue;
        this.time = time;
        this.applyPlaceholders = applyPlaceholders;
    }

    @Override
    public float get(Camera camera, float vieDistance, boolean thickFog, float current, ClientLevel clientLevel) {
        if (stringValue.placeholders()) {
            applyPlaceholders.create(camera, vieDistance, thickFog, current, clientLevel);
            float f = stringValue.getValue(str);
            applyPlaceholders.clear();
            return f;
        } else {
            return stringValue.getValue(str);
        }
    }

    @Override
    public long getTime() {
        return time;
    }


}